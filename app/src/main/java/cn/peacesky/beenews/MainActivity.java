package cn.peacesky.beenews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import cn.peacesky.beenews.ui.activity.menu.SettingActivity;
import cn.peacesky.beenews.ui.activity.menu.SiteActivity;
import cn.peacesky.beenews.ui.activity.menu.SystemActivity;
import cn.peacesky.beenews.ui.fragment.ArticleFragmentContainer;
import cn.peacesky.beenews.util.CacheUtil;
import cn.peacesky.beenews.util.PrefUtils;
import cn.peacesky.beenews.util.TimeExpiringLruCache;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String savedTab = "savedTab";

    private static final String Log_FILTER = "param";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @InjectView(R.id.nav_view)
    NavigationView navView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private ActionBarDrawerToggle toggle;//代替监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        navView.setNavigationItemSelectedListener(this);
        // 获取软件可用内存以分配缓存内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        final int cursorCacheSize = cacheSize / 5;
        // set the expire Time of cache, default 20min;
        final int expireTime = 1200000;
        CacheUtil.detailArticleCache = new TimeExpiringLruCache<>(cacheSize, expireTime);
        CacheUtil.simpleListCache = new TimeExpiringLruCache<>(cursorCacheSize, expireTime);
        initToolbar();
        // 启动主页新闻的fragment_container
        FragmentManager fm = getSupportFragmentManager();
        ArticleFragmentContainer container = new ArticleFragmentContainer();
        fm.beginTransaction().add(R.id.fragment_container, container).commit();
    }

    /**
     * 设置"更多"菜单项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * 设置ToolBar
     */
    protected void initToolbar() {

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        // setTile 要在下面这句话上面，不然会失效
        setSupportActionBar(toolbar);

        // 监听DrawerLayout
        // 将抽屉事件和 toolbar联系起来，这是 material design 的设计
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(toggle);
    }

    // 设置搜索框
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                showSingeChoiceDialog();
                return true;
            // 设置夜间模式
            case R.id.men_action_change_mode:
                PrefUtils.setDarkMode(!PrefUtils.isDarkMode());
                MainActivity.this.recreate();//重新创建当前Activity实例
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 搜索跳转
     */
    private void showSingeChoiceDialog() {
        int colorId = R.color.accent;
        if (PrefUtils.isDarkMode()) {
            colorId = R.color.colorAccentDarkTheme;
        }

        new MaterialDialog.Builder(this).title(R.string.action_search_title)
                .items(R.array.search_content_main)
                .itemColorRes(colorId)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which,
                                               CharSequence text) {
                        return true;
                    }

                })
                .positiveText(R.string.search_positive_btn_main)
                .show();
    }

    /**
     * 回退键弹窗
     */
    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this).iconRes(
                R.mipmap.ic_launcher).limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.exit_app_title)
                .content(R.string.exit_app_hint)
                .positiveText(R.string.exit_app_positive)
                .negativeText(R.string.exit_app_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        finish();
                        //退出应用程序
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onRestart() {
        Log.i(Log_FILTER, "in main onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.i(Log_FILTER, "in main onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(Log_FILTER, "in main onResume");
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        Log.i(Log_FILTER, "in main onPause");
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        Log.i(Log_FILTER, "in main onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(Log_FILTER, "in main onDestroy");
        super.onDestroy();
        ButterKnife.reset(this);
    }

    private boolean prepareIntent(Class clazz) {
        startActivity(new Intent(MainActivity.this, clazz));
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }
        switch (menuItem.getItemId()) {
            case R.id.nav_site:
                return prepareIntent(SiteActivity.class);
            case R.id.nav_setting:
                return prepareIntent(SettingActivity.class);
            case R.id.nav_system:
                return prepareIntent(SystemActivity.class);
            case R.id.nav_news:
                // MainActivity.this.recreate();//重新创建当前Activity实例
                return true;
            default:
                return true;
        }
    }
}