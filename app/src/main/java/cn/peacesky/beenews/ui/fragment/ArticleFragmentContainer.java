package cn.peacesky.beenews.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.util.ColumnName;

/**
 * 资讯页面
 * 将首页最新资讯（LatestArticleFragment）与 其他页面（OriginalArticleFragment）
 * 放置到frag_first_container中，形成动态更新的TabLayout + ViewPager
 * 实现滑动切换几个资讯栏目进行分类阅读
 */
public class ArticleFragmentContainer extends Fragment {
    private static final String PARAM = "param";

    private static final String LOG = "PAGER_LOG";

    @InjectView(R.id.tablayout_article)
    TabLayout tablayout; // Tablayout
    @InjectView(R.id.viewpager_article)
    ViewPager vp; // ViewPager
    private String mParam;
    private Activity mAct; // 托管的Activity
    private String[] mTitles = new String[]{ColumnName.LATEST, ColumnName.NOTIFIC,
            ColumnName.BACHELOR, ColumnName.MASTER}; // TabLayout 标题集合

    public static ArticleFragmentContainer newInstance(String param) {
        ArticleFragmentContainer fragment = new ArticleFragmentContainer();
        Bundle args = new Bundle();
        args.putString(PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(PARAM);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_first_container, null);
        mAct = getActivity();
        //注入
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化Tablayout
//        setTablayout();
        //初始化ViewPager
        setViewPager();
    }

//    /**
//     * 设置Tablayout
//     */
//    private void setTablayout() {
//        tablayout.addTab(tablayout.newTab().setText(LATEST), true);
//        tablayout.addTab(tablayout.newTab().setText(CYDT));
//    }

    /**
     * 设置ViewPager
     */
    private void setViewPager() {
        //设置适配器
        ArticleFragmentAdapter adapter = new ArticleFragmentAdapter(getChildFragmentManager());
        vp.setAdapter(adapter);
        //绑定tab
        tablayout.setupWithViewPager(vp);
        tablayout.setTabsFromPagerAdapter(adapter);
        vp.setPageTransformer(true, new DepthPageTransformer());
    }

    /**
     * Fragment切换时隐藏控件
     */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 处理类库ButterKnife
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * 适配器
     */
    class ArticleFragmentAdapter extends FragmentStatePagerAdapter {

        public ArticleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //首页和其他页不同
            if (position == 0) {
                return LatestArticleFragment.newInstance("");
            } else {
                Logger.t(LOG).i("in getItem " + position);
                return OriginalArticleFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        /**
         * 标签卡上方的标题
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    // 栏目切换动画 Depth Page Transformer
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
