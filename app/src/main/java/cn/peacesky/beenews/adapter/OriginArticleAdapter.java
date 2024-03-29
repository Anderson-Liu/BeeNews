package cn.peacesky.beenews.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.model.ListArticleItem;
import cn.peacesky.beenews.util.ApiUrl;
import cn.peacesky.beenews.util.Constant;
import cn.peacesky.beenews.util.OnItemClickLitener;

/**
 * 新闻列表的适配器
 * 只有列表新闻
 * 没有头部的的轮播图片
 */

public class OriginArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final static int TYPE_MULTI_IMAGES = 2; // 多个图片的文章
    private final static int TYPE_FOOTER = 3;//底部--往往是loading_more
    private final static int TYPE_NORMAL = 1; // 正常的一条文章
    //新闻列表
    private List<ListArticleItem> articleList;
    //context
    private Context context;

    private LayoutInflater mLayoutInflater;

    private OnItemClickLitener mOnItemClickLitener;//点击 RecyclerView 中的 Item

    public OriginArticleAdapter(Context context, List<ListArticleItem> articleList) {
        this.context = context;
        if (articleList == null) {
            this.articleList = new ArrayList<>();
        } else {
            this.articleList = articleList;
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 根据不同的类别返回不同的ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
                view = mLayoutInflater.inflate(
                        R.layout.recyclerview_footer, parent, false);
                vh = new FooterViewHolder(view);
                return vh;
            case TYPE_MULTI_IMAGES:
                view = mLayoutInflater.inflate(
                        R.layout.item_article_multi_images, parent, false);
                vh = new MultiImagesViewHolder(view);
                return vh;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        //这时候 article是 null，先把 footer 处理了
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).rcvLoadMore.spin();
            return;
        }

        ListArticleItem article = articleList.get(position);
        String[] imageUrls = article.getImageUrls();

        if (holder instanceof ItemArticleViewHolder) {
            ItemArticleViewHolder newHolder = (ItemArticleViewHolder) holder;
            newHolder.rcvArticleTitle.setText(article.getTitle());
            newHolder.rcvArticleDate.setText(article.getPublishDate());
            //当图片小于3张时候 选取第1张图片
            if (!imageUrls[0].isEmpty()) {
                newHolder.rcvArticlePhoto.setImageURI(Uri.parse(Constant.BUCKET_HOST_NAME + imageUrls[0] + Constant.IMG_SUFIX));
                Logger.d(Constant.BUCKET_HOST_NAME + imageUrls[0] + Constant.IMG_SUFIX);
            } else {
                newHolder.rcvArticlePhoto.setImageURI(Uri.parse(ApiUrl.randomImageUrl(article.getId()) + Constant.IMG_SUFIX));
            }
            //注意这个阅读次数是 int 类型，需要转化为 String 类型
            int readTime = article.getReadTimes();
            newHolder.rcvArticleReadtimes.setText(String.format(Constant.LOCAL, "浏览: %d", readTime));

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
        } else {
            MultiImagesViewHolder newHolder = (MultiImagesViewHolder) holder;
            newHolder.articleTitle.setText(article.getTitle());
            newHolder.articlePic1.setImageURI(Uri.parse(imageUrls[0]));
            newHolder.articlePic2.setImageURI(Uri.parse(imageUrls[1]));
            newHolder.articlePic3.setImageURI(Uri.parse(imageUrls[2]));
            newHolder.countPics.setText(String.format(Constant.LOCAL, "图片: %d", imageUrls.length));
            newHolder.countRead.setText(String.format(Constant.LOCAL, "浏览: %d", article.getReadTimes()));

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
        }
    }


    /**
     * 传进来的 List 末尾增加一个 null
     * null 作为是上拉的 progressBar 的标记
     * http://android-pratap.blogspot.com/2015/06/endless-recyclerview-with-progress-bar.html
     * 参看的这篇文章
     */
    @Override
    public int getItemViewType(int position) {

        ListArticleItem article = articleList.get(position);
        if (article == null) {
            return TYPE_FOOTER;
        } else if (article.getImageUrls().length >= 3) {
            return TYPE_MULTI_IMAGES;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (articleList != null) {
            return articleList.size();
        } else {
            return 0;
        }
    }

    public int getBottomArticleId() {
        if (articleList == null || articleList.size() == 0)
            return -1;
        return articleList.get(articleList.size() - 1).getId();
    }

    //返回最底的文章id，为了下拉刷新从该id开始加载
    public int getTopArticleId() {
        if (articleList == null || articleList.size() == 0)
            return -1;
        return articleList.get(0).getId();
    }


    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
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

        ItemArticleViewHolder(View itemView) {
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

        MultiImagesViewHolder(View itemView) {
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

        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
