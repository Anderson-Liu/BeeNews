package cn.peacesky.beenews.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.adapter.LatestArticleAdapter;
import cn.peacesky.beenews.model.ListArticleItem;
import cn.peacesky.beenews.ui.activity.first.DetailActivity;
import cn.peacesky.beenews.util.CacheUtil;
import cn.peacesky.beenews.util.Constant;
import cn.peacesky.beenews.util.DataUtil;
import cn.peacesky.beenews.util.OnItemClickLitener;

/**
 * 最新新闻
 * 上面是轮播图片
 * 下面是列表新闻
 * Created by tomchen on 1/10/16.
 */
public class LatestArticleFragment extends Fragment {

    public static final String ARTICLE_ID = "id";
    public static final String ARTICLE_TITLE = "title";
    public static final String ARTICLE_READ = "read_times";
    public static final String ARTICLE_DATE = "date";
    public static final String COLUMN_TYPE = "type";

    @InjectView(R.id.rcv_article_latest)
    RecyclerView mRecyclerView;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private DataUtil dataUtil = new DataUtil();
    private int totalItemCount;
    private int lastVisibleItem;
    //获取 fragment 依赖的 Activity，方便使用 Context
    private Activity mActivity;

    private LatestArticleAdapter mAdapter;

    // 新闻列表数据
    private List<ListArticleItem> mArticleList = new ArrayList<>();
    private boolean loading = false;
    private boolean bottom = false;

    public static LatestArticleFragment newInstance(String param) {
        return new LatestArticleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_first_latest, container, false);
        ButterKnife.inject(this, view);
        mActivity = getActivity();
        mArticleList = new ArrayList<>();
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));//这里用线性显示 类似于listview
        //最开始 ViewPager 没有数据
//        setUpViewPager( null );

        Logger.d("in onActivityCreated");

        mAdapter = new LatestArticleAdapter(mActivity, mArticleList);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
             * called after the scroll has completed.
             * <p/>
             * This callback will also be called if visible item range changes after a layout
             * calculation. In that case, dx and dy will be 0.
             *
             * @param recyclerView The RecyclerView which scrolled.
             * @param dx           The amount of horizontal scroll.
             * @param dy           The amount of vertical scroll.
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                totalItemCount = layoutManager.getItemCount();

                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem != totalItemCount - 1) {
                    bottom = false;
                }
                // 如果 当前显示文章总数 < 已阅读数 + 阀值
                // 执行 获取更多文章
                if (!bottom && !loading && totalItemCount < (lastVisibleItem + Constant.VISIBLE_THRESHOLD)) {
                    // new LatestArticleTask().execute(mAdapter.getBottomArticleId();
                    new LatestArticleTask().execute(totalItemCount);
                    loading = true;
                }
            }
        });
        // 文章列表的点击事件
        mAdapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(int position) {
                ListArticleItem articleItem = mArticleList.get(position + Constant.COUNT_ROTATION - 1);
                Intent intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra(COLUMN_TYPE, articleItem.getType());
                intent.putExtra(ARTICLE_ID, articleItem.getId());
                intent.putExtra(ARTICLE_TITLE, articleItem.getTitle());
                intent.putExtra(ARTICLE_DATE, articleItem.getPublishDate());
                intent.putExtra(ARTICLE_READ, articleItem.getReadTimes());
                startActivity(intent);
            }
        });


        // 顶部刷新的样式
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW);
        // 设置下拉刷新的回调事件。下拉刷新后，使用 AsyncTask
        // 根据当前RecyclerView中首个Item的id来加载更多数据。
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MoreArticleTask().execute(mAdapter.getTopOriginArticleId());
            }
        });

        // 首次进入页面就显示加载ing的动画
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // 启动刷新动画
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                    new MoreArticleTask().execute(mAdapter.getTopOriginArticleId());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    public List<ListArticleItem> getRotationItem() {
        List<ListArticleItem> list = CacheUtil.simpleListCache.get(Constant.IMG_COLLECTION);
        if (null == list) {
            String url = Constant.EVE_HOST + "/rotationImage?sort=-publishDate";
            String resultBody = dataUtil.request(url);
            list = dataUtil.getListFromResult(resultBody);
            CacheUtil.simpleListCache.put(Constant.IMG_COLLECTION, list);
        }
        return list;
    }

    /**
     * @param moreThan 大于该id的新闻数组
     */
    public List<ListArticleItem> getMoreById(int moreThan) {

//        String url = Constant.EVE_HOST + "/SimpleArticle?" +
//                "where={\"aid\":{\"$gt\":" + moreThan  +  "}}" +
//                "&&sort=-publishDate";
        String preUrl = Constant.EVE_HOST + "/%s?where={%s:{%s:%d}}&&sort=-publishDate";
        String url = String.format(preUrl, Constant.SIMP_COLLECTION
                , "\"aid\"", "\"$gt\"", moreThan);
        String resultBody = dataUtil.request(url);
        List<ListArticleItem> list = dataUtil.getListFromResult(resultBody);
        if (0 == list.size()) {
            Logger.d("JSON没获得数据");
        }
        return list;
    }


    public List<ListArticleItem> getArticleList(int offset) {

        String url = Constant.EVE_HOST + "/SimpleArticle?sort=-publishDate";
        String resultBody = dataUtil.request(url);
        List<ListArticleItem> list = dataUtil.getListFromResult(resultBody);
        if (list.size() == offset - 1 || list.size() < offset) {
            return new ArrayList<>();
        }
        list = list.subList(offset - 1, list.size() - 1);
        if (0 == list.size()) {
            Logger.d("JSON没获得数据");
        }
        return list;
    }

    //Integer 是输入参数
    // 得到比某个id大的新闻数组
    class MoreArticleTask extends AsyncTask<Integer, Void, List<ListArticleItem>> {

        /**
         * 在onPreExecute()完成后立即执行，用于执行较为费时的
         * 操作，此方法将接收输入参数和返回计算结果.。
         */
        @Override
        protected List<ListArticleItem> doInBackground(Integer... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<ListArticleItem> data = new ArrayList<ListArticleItem>();

            List<ListArticleItem> rotationItem = getRotationItem();
            if (rotationItem != null) {
                int rotationSize = rotationItem.size();
                if (rotationSize >= Constant.COUNT_ROTATION) {
                    // 轮播文章, 放在列表前五位
                    data.addAll(rotationItem.subList(0, Constant.COUNT_ROTATION));
                } else if (rotationSize != 0) {
                    data.addAll(rotationItem.subList(0, rotationSize));
                }
            }
            // 其他全部文章
            data.addAll(getMoreById(params[0]));
            return data;
        }

        /**
         * 当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到
         * 此方法中，直接将结果显示到UI组件上.
         */
        @Override
        protected void onPostExecute(List<ListArticleItem> listArticleItems) {
            super.onPostExecute(listArticleItems);

            if (swipeRefreshLayout != null) {
                // 取消动画
                swipeRefreshLayout.setRefreshing(false);
            }
            //没有新的数据，提示消息
            if (listArticleItems == null || listArticleItems.size() == 0) {
                Snackbar.with(mActivity.getApplicationContext()) // context
                        .text(mActivity.getResources().getString(R.string.list_more_data)) // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                        .show(mActivity); // activity where it is displayed
            } else {
                mArticleList.addAll(listArticleItems);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //Integer 是输入参数
    class LatestArticleTask extends AsyncTask<Integer, Void, List<ListArticleItem>> {

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //增加底部的一个null数据，表示ProgressBar
            if (mArticleList != null && mArticleList.size() > 0) {
                mArticleList.add(null);
                // notifyItemInserted(int position)，这个方法是在第position位置
                // 被插入了一条数据的时候可以使用这个方法刷新，
                // 注意这个方法调用后会有插入的动画，这个动画可以使用默认的，也可以自己定义。
                Logger.d("增加底部footer 圆形ProgressBar");

                mAdapter.notifyItemInserted(mArticleList.size() - 1);
            }
        }

        @Override
        protected List<ListArticleItem> doInBackground(Integer... params) {
            Logger.d("in doInBackground");

            List<ListArticleItem> data = new ArrayList<>();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //只有第一次需要加载头部的轮播图片
            //下拉刷新时候不加轮播图片
            if (mArticleList.size() == 0) {
                data.addAll(getRotationItem());
            }

            data.addAll(getArticleList(params[0]));

            return data;
        }

        @Override
        protected void onPostExecute(final List<ListArticleItem> moreArticles) {
            super.onPostExecute(moreArticles);
            if (moreArticles.size() == 0) {
                Toast.makeText(getContext(), "没有更多的文章了", Toast.LENGTH_SHORT).show();
            }
            if (mArticleList.size() == 0) {
                mArticleList.addAll(moreArticles);
                mAdapter.notifyDataSetChanged();
            } else {
                //删除 footer
                mArticleList.remove(mArticleList.size() - 1);

                Logger.d("下拉增加数据 " + moreArticles);

                //只有到达最底部才加载
                //防止上拉到了倒数两三个也加载
                if (!bottom && lastVisibleItem == totalItemCount - 1 && moreArticles.size() == 0) {
                    Snackbar.with(mActivity.getApplicationContext()) // context
                            .text(mActivity.getResources().getString(R.string.list_no_data)) // text to display
                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                            .show(mActivity); // activity where it is displayed
                    bottom = true;
                }

                mArticleList.addAll(moreArticles);
                mAdapter.notifyDataSetChanged();

                loading = false;
//            mArticleList.addAll(moreArticles);
            }
        }
    }
}
