package cn.peacesky.beenews.ui.activity.menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    @InjectView(R.id.goto_btn)
    Button goto_btn;

    private ArrayAdapter<CharSequence> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int[] url_sufix = {9, 10, 4, 6, 5, 8, 3, 11, 7, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};

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
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                result.setText("  您的选择是：" + ((TextView) view).getText() + "\n"
                        + "这是第" + spinner.getSelectedItemPosition() + "个城市" + position);
                goto_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://xmgl.ahau.edu.cn/station/show_station.aspx?stationid="
                                + url_sufix[position];
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                });
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