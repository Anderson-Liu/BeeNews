package cn.peacesky.beenews.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.model.ListArticleItem;
import cn.peacesky.beenews.ui.activity.first.DetailActivity;
import cn.peacesky.beenews.util.ApiUrl;
import cn.peacesky.beenews.util.Constant;
import cn.peacesky.beenews.util.OnItemClickLitener;

import static cn.peacesky.beenews.ui.fragment.LatestArticleFragment.ARTICLE_DATE;
import static cn.peacesky.beenews.ui.fragment.LatestArticleFragment.ARTICLE_ID;
import static cn.peacesky.beenews.ui.fragment.LatestArticleFragment.ARTICLE_READ;
import static cn.peacesky.beenews.ui.fragment.LatestArticleFragment.ARTICLE_TITLE;
import static cn.peacesky.beenews.ui.fragment.LatestArticleFragment.COLUMN_TYPE;

/**
 * 新闻列表的适配器
 * 最新栏目的适配器
 * 这个栏目上方有轮播图片
 * 上方是 ViewPager，下面是列表新闻
 * Created by tomchen on 1/11/16.
 */
public class LatestArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int TYPE_MULTI_IMAGES = 3; // 多个图片的文章
    public final static int TYPE_FOOTER = 4;//底部--往往是loading_more
    public final static int TYPE_NORMAL = 2; // 正常的一条文章
    private static final int TYPE_ROTATION = 1;
    //Handler 用到的参数值
    private static final int UPTATE_VIEWPAGER = 0;
    Timer timer = new Timer();
    int savePosition;
    //新闻列表
    private List<ListArticleItem> articleList;
    //设置当前 第几个图片 被选中
    private int savedIndex = 0;
    //context
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ImageView[] mCircleImages;//底部只是当前页面的小圆点
    private OnItemClickLitener mOnItemClickLitener;//点击 RecyclerView 中的 Item

    /**
     * 注意这儿的 articleList 和原来的articleList 是同一个引用
     * fragment 的文章list增加了数据
     * 这儿的list也增加数据
     *
     * @param context
     * @param articleList
     */
    public LatestArticleAdapter(Context context, List<ListArticleItem> articleList) {
        this.context = context;

        if (articleList == null) {
            this.articleList = new ArrayList<>();
        } else {
            //头部viewpager图片固定是5张，剩下的是列表的数据
            this.articleList = articleList;
        }

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //理论上应该把最可能返回的 TYPE 放在前面

        RecyclerView.ViewHolder vh;
        View view;
        switch (viewType) {

            //其他无法处理的情况使用viewholder_article_simple
            default:
            case TYPE_NORMAL:
                view = mLayoutInflater.inflate(
                        R.layout.item_article_normal, parent, false);
                vh = new ItemArticleViewHolder(view);
                return vh;
            case TYPE_FOOTER:
                Logger.d("下拉刷新 底部类型 in TYPE_FOOTER");
                view = mLayoutInflater.inflate(
                        R.layout.recyclerview_footer, parent, false);
                vh = new FooterViewHolder(view);
                return vh;
            case TYPE_MULTI_IMAGES:
                view = mLayoutInflater.inflate(
                        R.layout.item_article_multi_images, parent, false);
                vh = new MultiImagesViewHolder(view);
                return vh;
            case TYPE_ROTATION:
                view = mLayoutInflater.inflate(
                        R.layout.item_article_rotations, parent, false);
                vh = new RotationViewHolder(view);
                return vh;
        }

//        //可以抛出异常，没有对应的View类型
//        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    //返回最底的文章id，为了下拉刷新从该id开始加载
    public int getBottomArticleId() {
        if (articleList == null || articleList.size() == 0)
            return -1;
        return articleList.get(articleList.size() - 1).getId();
    }

    //返回最顶的文章id，为了下拉刷新从该id开始加载
    public int getTopOriginArticleId() {
        if (articleList == null || articleList.size() == 0)
            return -1;
        return articleList.get(Constant.COUNT_ROTATION).getId();
    }

    /**
     * 当Item 超出屏幕后，就会重新执行onBindViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        //这时候 article是 null，先把 footer 处理了
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).rcvLoadMore.spin();
            return;
        }

        // 注意RecyclerView第0项是 ViewPager 占据了0 1 2 3 4图片
        // 那么下面的列表展示是 RecyclerView 的第1项，从第5项开始
        ListArticleItem article = articleList.get(position + Constant.COUNT_ROTATION - 1);
        String[] imageUrls = article.getImageUrls();

        if (holder instanceof ItemArticleViewHolder) {

            //转型
            ItemArticleViewHolder newHolder = (ItemArticleViewHolder) holder;

            if (imageUrls[0].isEmpty()) {
                newHolder.rcvArticlePhoto.setImageURI(Uri.parse(ApiUrl.randomImageUrl(article.getId()) + Constant.IMG_SUFIX));
            } else {
                newHolder.rcvArticlePhoto.setImageURI(Uri.parse
                        (Constant.BUCKET_HOST_NAME + article.getImageUrls()[0] + Constant.IMG_SUFIX));
                Logger.d(Constant.BUCKET_HOST_NAME + article.getImageUrls()[0] + Constant.IMG_SUFIX);

            }
            newHolder.rcvArticleTitle.setText(article.getTitle());
            newHolder.rcvArticleDate.setText(article.getPublishDate());
            // 注意这个阅读次数是 int 类型，需要转化为 String 类型
            newHolder.rcvArticleReadtimes.setText(article.getReadTimes() + "阅");
            newHolder.rcvArticleSummary.setText(article.getSummary());

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(pos);
                    }
                });
            }

        } else if (holder instanceof MultiImagesViewHolder) {
            MultiImagesViewHolder newHolder = (MultiImagesViewHolder) holder;
            newHolder.articleTitle.setText(article.getTitle());
            newHolder.articlePic1.setImageURI(Uri.parse(imageUrls[0]));
            newHolder.articlePic2.setImageURI(Uri.parse(imageUrls[1]));
            newHolder.articlePic3.setImageURI(Uri.parse(imageUrls[2]));
            newHolder.countPics.setText("图片: " + imageUrls.length);
            newHolder.countRead.setText("浏览: " + article.getReadTimes());

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(pos);
                    }
                });
            }

        } else if (holder instanceof RotationViewHolder) {

            RotationViewHolder newHolder = (RotationViewHolder) holder;
            List<ListArticleItem> headers = articleList.subList(0, Constant.COUNT_ROTATION);

            newHolder.tvCollegeBroadcast.setText("小喇叭: 欢迎来到蜜蜂病虫害监测风险评估预警系统!");
            setUpViewPager(newHolder.vpHottest, newHolder.llHottestIndicator, headers);

        }
    }

    /**
     * @param vp             轮播图片
     * @param llBottom       底部的小圆点
     * @param headerArticles 新闻，包括了图片url、标题等属性
     */
    private void setUpViewPager(final ViewPager vp, LinearLayout llBottom, final List<ListArticleItem> headerArticles) {
        RotationImageAdapter imageAdapter = new RotationImageAdapter(context, headerArticles);
        //??这儿有些疑惑，Adapter 里面嵌套设置 Adapter 是否优雅？
        vp.setAdapter(imageAdapter);

        //下面是设置动画切换的样式
        vp.setPageTransformer(true, new RotateUpTransformer());

        //创建底部指示位置的导航栏
        final ImageView[] mCircleImages = new ImageView[headerArticles.size()];

        //先去除已有的View，所有的小圆点

        llBottom.removeAllViews();

        for (int i = 0; i < mCircleImages.length; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.indicator_select);
            } else {
                imageView.setBackgroundResource(R.drawable.indicator_not_select);
            }

            mCircleImages[i] = imageView;
            //把指示作用的原点图片加入底部的视图中
            llBottom.addView(mCircleImages[i]);
        }

        // 没办法直接onClickListener,只能变相通过onTouchListener的按键动作来判断是滑动还是点击
        // 如果是点击，触发点击事件，并在OnClickListener中进行处理。
        vp.setOnTouchListener(
                new View.OnTouchListener() {
                    private boolean moved;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            moved = false;
                            return true;
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            moved = true;
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            if (!moved) {
                                view.performClick();
                            }
                        }
                        return false;
                    }
                }
        );

        //设置自动轮播图片，5s后执行，周期是5s

        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPTATE_VIEWPAGER:
                        if (msg.arg1 != 0) {
                            vp.setCurrentItem(msg.arg1);
                        } else {
                            //false 当从末页调到首页是，不显示翻页动画效果，
                            vp.setCurrentItem(msg.arg1, false);
                        }
                        break;
                }
            }
        };


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                if (savedIndex == headerArticles.size() - 1) {
                    savedIndex = -1;
                }
                message.arg1 = savedIndex + 1;
                mHandler.sendMessage(message);
            }
        }, 6000, 6000);


        // then you can simply use the standard onClickListener ...
        vp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ListArticleItem articleItem = articleList.get(savedIndex);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra(COLUMN_TYPE, articleItem.getType());
                        intent.putExtra(ARTICLE_ID, articleItem.getId());
                        intent.putExtra(ARTICLE_TITLE, articleItem.getTitle());
                        intent.putExtra(ARTICLE_DATE, articleItem.getPublishDate());
                        intent.putExtra(ARTICLE_READ, articleItem.getReadTimes());
                        context.startActivity(intent);
                    }
                }
        );


        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //图片左右滑动时候，将当前页的圆点图片设为选中状态
            @Override
            public void onPageSelected(int position) {

                // 一定几个图片，几个圆点，但注意是从0开始的
                int total = mCircleImages.length;
                for (int j = 0; j < total; j++) {
                    if (j == position) {
                        mCircleImages[j].setBackgroundResource(R.drawable.indicator_select);
                    } else {
                        mCircleImages[j].setBackgroundResource(R.drawable.indicator_not_select);
                    }
                }
                //设置全局变量，currentIndex为选中图标的 index
                savedIndex = position;
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public int getItemCount() {
        // 得到当前RecyclerView中Item的总数目
        // 大于一定数目时，自动加载更多数据
        // 因为多了一个头部，所以是+1,但是头部 ViewPager 占了5个
        // 所以实际是少了4个
        if (articleList != null) {
            return articleList.size() + 1 - Constant.COUNT_ROTATION;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_ROTATION;
        else {
            ListArticleItem article = articleList.get(position + Constant.COUNT_ROTATION - 1);
            if (article == null) {
                return TYPE_FOOTER;
            } else if (article.getImageUrls().length >= 3) {
                return TYPE_MULTI_IMAGES;
            } else {
                return TYPE_NORMAL;
            }
        }
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    class RotationViewHolder extends RecyclerView.ViewHolder {

        //轮播的最热新闻图片
        @InjectView(R.id.vp_hottest)
        ViewPager vpHottest;
        //轮播图片下面的小圆点
        @InjectView(R.id.ll_hottest_indicator)
        LinearLayout llHottestIndicator;

        // 广播信息
        @InjectView(R.id.tv_college_broadcast)
        TextView tvCollegeBroadcast;

        public RotationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemArticleViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.rcv_article_photo)
        SimpleDraweeView rcvArticlePhoto;
        @InjectView(R.id.rcv_article_title)
        TextView rcvArticleTitle;
        @InjectView(R.id.rcv_article_date)
        TextView rcvArticleDate;
        @InjectView(R.id.rcv_article_readtimes)
        TextView rcvArticleReadtimes;
        @InjectView(R.id.rcv_article_summary)
        TextView rcvArticleSummary;

        public ItemArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * 大于3 张图片使用的ViewHolder
     */
    class MultiImagesViewHolder extends RecyclerView.ViewHolder {


        @InjectView(R.id.article_title)
        TextView articleTitle;
        @InjectView(R.id.article_pic1)
        SimpleDraweeView articlePic1;
        @InjectView(R.id.article_pic2)
        SimpleDraweeView articlePic2;
        @InjectView(R.id.article_pic3)
        SimpleDraweeView articlePic3;
        @InjectView(R.id.count_pics)
        TextView countPics;
        @InjectView(R.id.count_read)
        TextView countRead;

        public MultiImagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * 底部加载更多
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.rcv_load_more)
        ProgressWheel rcvLoadMore;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}