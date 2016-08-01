package cn.peacesky.beenews.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.peacesky.beenews.model.ListArticleItem;
import cn.peacesky.beenews.util.Constant;

/**
 * 头部轮播图片的适配类
 * Created by tomchen on 2015/8/28.
 */
public class RotationImageAdapter extends PagerAdapter {

    private Context context;
    private List<ListArticleItem> articles;
    private List<SimpleDraweeView> sdvs = new ArrayList<>();

    public RotationImageAdapter(Context context, List<ListArticleItem> articles) {
        this.context = context;
        if (articles == null || articles.size() == 0) {
            this.articles = new ArrayList<>();
        } else {
            this.articles = articles;
        }

        for (int i = 0; i < articles.size(); i++) {
            SimpleDraweeView sdv = new SimpleDraweeView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            sdv.setLayoutParams(layoutParams);
            sdv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Uri uri = Uri.parse(Constant.BUCKET_HOST_NAME + articles.get(i).getImageUrls()[0] + Constant.IMG_SUFIX);
            sdv.setImageURI(uri);
            sdvs.add(sdv);
            Logger.d(Constant.BUCKET_HOST_NAME + articles.get(i).getImageUrls()[0] + Constant.IMG_SUFIX);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(sdvs.get(position));
        return sdvs.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(sdvs.get(position));
    }


    @Override
    public int getCount() {
        return Constant.COUNT_ROTATION;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
}
