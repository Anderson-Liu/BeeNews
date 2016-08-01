package cn.peacesky.beenews.ui.activity.menu;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.peacesky.beenews.BaseActivity;
import cn.peacesky.beenews.R;

public class SiteActivity extends BaseActivity {

    @InjectView(R.id.toolbar_site)
    Toolbar mToolbar;
    @InjectView(R.id.spinner)
    Spinner spinner;
    @InjectView(R.id.result)
    TextView result;

    private ArrayAdapter<CharSequence> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_site);
        ButterKnife.inject(this);
        initToolbar();

        adapter = ArrayAdapter.createFromResource(this, R.array.cities, R.layout.custom_spinner_item);
        //设置下拉列表风格
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        //将适配器添加到spinner中去
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                result.setText("  您的选择是：" + ((TextView) view).getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle(getResources().getString(R.string.site_title));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_left_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 选项菜单
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}