package cn.peacesky.beenews.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import butterknife.ButterKnife;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.model.ItemArticle;

/**
 * Created by anderson on 08/01/16.
 */
public class OriginalFragment extends Fragment {

    private static final String ARTICLE_LATEST_PARAM = "param";

    private static final String log = "PAGER_LOG";

    private static final int UPTATE_VIEWPAGER = 0;

    //存储的参数
    private String mParam;

    //获取 fragment 依赖的 Activity，方便使用 Context
    private Activity mAct;

    //新闻列表数据
    private List<ItemArticle> itemArticleList = new ArrayList<ItemArticle>();

    //设置当前 第几个图片 被选中
    private int autoCurrIndex = 0;
    private ImageView[] mBottomImages;//底部只是当前页面的小圆点
    private Timer timer = new Timer(); //为了方便取消定时轮播，将 Timer 设为全局

    public static OriginalFragment newInstance(String param) {
        OriginalFragment fragment = new OriginalFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_LATEST_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mParam = savedInstanceState.getString(ARTICLE_LATEST_PARAM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_first_other, container, false);
        mAct = getActivity();
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}