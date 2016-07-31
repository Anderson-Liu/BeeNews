package cn.peacesky.beenews.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nispok.snackbar.Snackbar;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.adapter.OriginArticleAdapter;
import cn.peacesky.beenews.model.ListArticleItem;
import cn.peacesky.beenews.ui.activity.first.DetailActivity;
import cn.peacesky.beenews.util.CacheUtil;
import cn.peacesky.beenews.util.ColumnType;
import cn.peacesky.beenews.util.Constant;
import cn.peacesky.beenews.util.DataUtil;
import cn.peacesky.beenews.util.OnItemClickLitener;

/**
 * 普通 展示新闻列表
 * <p/>
 * 实现上拉加载更多
 * Created by tomchen on 1/10/16.
 */
public class OriginalArticleFragment extends Fragment {


    public static final String ARTICLE_ID = "id";
    public static final String ARTICLE_TITLE = "title";
    public static final String ARTICLE_DATE = "date";
    public static final String ARTICLE_READ = "read_times";
    public static final String COLUMN_TYPE = "type";
    private static final String POSITION = "column";
    @InjectView(R.id.rcv_article_origin)
    RecyclerView mRecyclerView;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DataUtil dataUtil = new DataUtil();
    //存储的参数,是栏目的 aid 不连续
    private int mColumn;
    //获取 fragment 依赖的 Activity，方便使用 Context
    private Activity mActivity;
    private Handler mHandler;
    private OriginArticleAdapter mAdapter;
    private List<ListArticleItem> mArticleList;
    private boolean loading = false;
    private String listType;


    public static OriginalArticleFragment newInstance(int param) {
        OriginalArticleFragment fragment = new OriginalArticleFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, param);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColumn", mColumn);
        outState.putSerializable("mArticleList", (Serializable) mArticleList);
        int tmp = mRecyclerView.getAdapter().getItemCount();
        Logger.d(String.valueOf(tmp));
        if (!(mArticleList == null || mArticleList.size() == 0)) {
            listType = "list-" + mColumn;
            CacheUtil.simpleListCache.put(listType, mArticleList);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mPosition = getArguments().getInt(POSITION);
        mColumn = ColumnType.getType(mPosition);
        listType = "list-" + mColumn;
        List<ListArticleItem> list = CacheUtil.simpleListCache.get(listType);
        if (null != list) {
            mArticleList = list;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_first_other, container, false);
        ButterKnife.inject(this, view);
        mActivity = getActivity();
        mHandler = new Handler();
        mArticleList = new ArrayList<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //让 RecyclerView 每一项的高度相同
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mAdapter = new OriginArticleAdapter(mActivity, mArticleList);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setSaveEnabled(true);

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

                int totalItemCount = layoutManager.getItemCount();

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!loading && totalItemCount < (lastVisibleItem + Constant.VISIBLE_THRESHOLD)) {
                    new ArticleTask(mActivity).execute(totalItemCount);
                    loading = true;
                }
            }
        });

        mAdapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(int position) {
                ListArticleItem articleItem = mArticleList.get(position);
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
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW);
        mSwipeRefreshLayout.setSaveEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MoreArticleTask().execute(mAdapter.getTopArticleId());
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                new MoreArticleTask().execute(mAdapter.getTopArticleId());
            }
        });

//        new ArticleTask(mActivity).execute(-1);

//        mAdapter.setOnLoadMoreListener(new OriginArticleAdapter.OnLoadMoreListener() {
//            /**
//             * 加载更多
//             */
//            @Override
//            public void onLoadMore() {
//
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        //   remove progress item
//                        mArticleList.remove(mArticleList.size() - 1);
//                        //notifyItemRemoved(int position)
//                        // 第position个被删除的时候刷新，同样会有动画。
//                        mAdapter.notifyItemRemoved(mArticleList.size());
//                        List<ListArticleItem> moreArticles = getArticleList(mColumn,
//                                mArticleList.get(mArticleList.size() - 1).getId());
//
//                        for (int i = 0; i < moreArticles.size(); i++) {
//                            mArticleList.add(moreArticles.get(i));
//                            mAdapter.notifyItemInserted(mArticleList.size());
//                        }
//                        mAdapter.setLoaded();
//                    }
//                }, 1000);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * @param type     第几个栏目
     * @param moreThan 大于该id的新闻数组
     */
    public List<ListArticleItem> getMoreById(int type, int moreThan) {

//        String url = Constant.EVE_HOST + "/SimpleArticle?" +
//                "where={\"aid\":{\"$gt\":" + moreThan  +  "}, " + "\"type\": " + type + "}" +
//                "&sort=-publishDate";

        String preUrl = Constant.EVE_HOST + "/%s?where={%s:{%s:%d}, %s:%d}&sort=-publishDate";
        String url = String.format(preUrl, Constant.SIMP_COLLECTION,
                "\"aid\"", "\"$gt\"", moreThan, "\"type\"", type);

        String resultBody = dataUtil.request(url);
        List<ListArticleItem> list = dataUtil.getListFromResponse(resultBody);

        if (0 == list.size()) {
            Logger.d("JSON没获得数据");
        }
        return list;
    }


    /**
     * @param type   第几个栏目
     * @param offset 偏移 aid
     */
    public List<ListArticleItem> getArticleList(int type, int offset) {
        String preUrl = Constant.EVE_HOST + "/%s?where={%s: %d}&&sort=-publishDate";
        String url = String.format(preUrl, Constant.SIMP_COLLECTION, "\"type\"", type);
        String resultBody = dataUtil.request(url);
        List<ListArticleItem> list = dataUtil.getListFromResponse(resultBody);
        if (list.size() == offset - 1) {
            return new ArrayList<ListArticleItem>();
        }

        list = list.subList(offset - 1, list.size() - 1);
        if (0 == list.size()) {
            Logger.d("JSON没获得数据");
        }
        return list;
    }

    // 下拉刷新时获取更多数据
    // Integer 是输入参数
    // 得到比某个id大的新闻数组
    class MoreArticleTask extends AsyncTask<Integer, Void, List<ListArticleItem>> {
        @Override
        protected List<ListArticleItem> doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return getMoreById(mColumn, params[0]);
        }

        @Override
        protected void onPostExecute(List<ListArticleItem> listArticleItems) {
            super.onPostExecute(listArticleItems);

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
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


    class ArticleTask extends AsyncTask<Integer, Void, List<ListArticleItem>> {

        private Context mContext;

        public ArticleTask(Context context) {
            mContext = context;
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mArticleList != null && mArticleList.size() > 0) {
                mArticleList.add(null);
                // notifyItemInserted(int position)，这个方法是在第position位置
                // 被插入了一条数据的时候可以使用这个方法刷新，
                // 注意这个方法调用后会有插入的动画，这个动画可以使用默认的，也可以自己定义。
                Logger.d("in mArticleList.add(null)");

                mAdapter.notifyItemInserted(mArticleList.size() - 1);
            }
        }

        /**
         * @param params 偏移量 num
         */
        @Override
        protected List<ListArticleItem> doInBackground(Integer... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getArticleList(mColumn, params[0]);
        }

        @Override
        protected void onPostExecute(final List<ListArticleItem> moreArticles) {
            // 新增新闻数据
            super.onPostExecute(moreArticles);
//            Context context = getActivity().getApplicationContext();
//            if (moreArticles.size() == 0) {
//                Toast.makeText(context, "没有更多的文章了", Toast.LENGTH_SHORT).show();
//            }
            if (mArticleList.size() == 0) {
                mArticleList.addAll(moreArticles);
                mAdapter.notifyDataSetChanged();
            } else {
                //删除 footer
                mArticleList.remove(mArticleList.size() - 1);
                mArticleList.addAll(moreArticles);
                mAdapter.notifyDataSetChanged();
//                for (int i = 0; i < moreArticles.size(); i++) {
//                    mArticleList.add(moreArticles.get(i));
//                    mAdapter.notifyItemInserted(mArticleList.size());
//                }
                loading = false;
//            mArticleList.addAll(moreArticles);
            }
        }
    }
}
