package cn.peacesky.beenews.ui.activity.first;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import cn.peacesky.beenews.R;
import cn.peacesky.beenews.Receiver.JpushReceiver;
import cn.peacesky.beenews.model.ArticleItem;
import cn.peacesky.beenews.ui.fragment.LatestArticleFragment;
import cn.peacesky.beenews.util.ApiUrl;
import cn.peacesky.beenews.util.CacheUtil;
import cn.peacesky.beenews.util.Constant;
import cn.peacesky.beenews.util.DataUtil;

import static android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK;

/**
 * Created by anderson on 2/23/16.
 */
public class DetailActivity extends AppCompatActivity {
    @InjectView(R.id.article_body)
    WebView articleBody;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.article_image)
    SimpleDraweeView articleImage;
    @InjectView(R.id.detail_title)
    TextView detailTitle;
    @InjectView(R.id.detail_date)
    TextView detailDate;
    @InjectView(R.id.detail_source)
    TextView detailSource;
    @InjectView(R.id.detail_read)
    TextView detailRead;
    @InjectView(R.id.detail_article)
    LinearLayout detailArticle;

    private int columnType;
    private int articleID;
    private String photoKey;
    private String title;
    private String date;
    private int read;
    private DataUtil dataUtil = new DataUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        ButterKnife.inject(this);

        initToolbar();

        Intent intent = getIntent();
        boolean isFromJpush = intent.getBooleanExtra(JpushReceiver.IS_FROM_JPUSH, false);

        if (isFromJpush) {
            Bundle bundle = getIntent().getExtras();
            //从通知栏的推送跳转过来
            title = bundle.getString(JPushInterface.EXTRA_ALERT, "");
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            JSONObject object = null;
            try {
                object = new JSONObject(extras);
                columnType = Integer.parseInt(object.getString("type"));
                articleID = Integer.parseInt(object.getString("aid"));
                Logger.json(object.toString());
            } catch (JSONException e) {
                Logger.e(e, "JSONException");
                // 如果推送中没有数据，打开默认的文章
                columnType = Constant.NOTIFI_NOT_FOUND_TYPE;
                articleID = Constant.NOTIFI_NOT_FOUND_ID;
            }
            Logger.d("获得的extras" + extras);
        } else {
            //从列表跳转过来
            columnType = intent.getIntExtra(LatestArticleFragment.COLUMN_TYPE, 0);
            articleID = intent.getIntExtra(LatestArticleFragment.ARTICLE_ID, 7948);
            title = intent.getStringExtra(LatestArticleFragment.ARTICLE_TITLE);
            date = intent.getStringExtra(LatestArticleFragment.ARTICLE_DATE);
            read = intent.getIntExtra(LatestArticleFragment.ARTICLE_READ, 452);
        }
        new GetArticleTask().execute();
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_left_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * 根据 type 和 aid 获取新闻详情
     */
    private ArticleItem getArticleDetail(int articleID, int type) {

        // Try to get the article from cache.
        ArticleItem articleItem = CacheUtil.detailArticleCache.get(articleID);
        if (null == articleItem) {
            String preUrl = Constant.EVE_HOST + "/%s?where={%s:%d, %s:%d}";
            String url = String.format(Locale.CHINESE, preUrl, Constant.FULL_COLLECTION,
                    "\"type\"", type, "\"aid\"", articleID);
            String result;
            try {
                result = dataUtil.request(url);
                if (result != null) {
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray items = (JSONArray) resultJson.get("_items");
                    JSONObject item = items.getJSONObject(0);
                    articleItem = dataUtil.parseJson2Article(item);
                    CacheUtil.detailArticleCache.put(articleID, articleItem);
                } else {
                    // 找不到文章，显示错误提示文章
                    getArticleDetail(Constant.NOT_FOUND_ID, Constant.NO_FOUND_TYPE);
                }
            } catch (JSONException e) {
                Logger.e(Arrays.toString(e.getStackTrace()));
            }

            Logger.d("当前lruCache中的内容：" + CacheUtil.detailArticleCache);
            if (articleItem == null) {
                Logger.d("根据type: " + type + " , 和AID: " + articleID + "找不到结果。" + articleItem);
            } else {
                Logger.d("根据type: " + type + " , 和AID: " + articleID + " 建立的文档对象:" + articleItem);
            }
        } else {
            Logger.d("本文章取自缓存" + articleItem);
        }
        return articleItem;
    }

    /**
     * 选项菜单
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //返回键 back的箭头
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }

    // 通过type和articleID获取文章
    private class GetArticleTask extends AsyncTask<Integer, Void, ArticleItem> {

        @Override
        protected ArticleItem doInBackground(Integer... params) {
            return getArticleDetail(articleID, columnType);
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         */
        @Override
        protected void onPostExecute(ArticleItem articleItem) {
            super.onPostExecute(articleItem);
            if (articleItem != null) {
                detailSource.setText(String.format(Locale.CHINESE, "来源：%s", articleItem.getSource()));
                collapsingToolbar.setTitle(articleItem.getTitle());
                detailTitle.setText(articleItem.getTitle());
                detailDate.setText(articleItem.getPublishDate());
                detailRead.setText(String.format(Locale.CHINESE, "%d 浏览", articleItem.getReadTimes()));
                String[] imageUrls = articleItem.getImageUrls();

                //当图片小于3张时候 选取第1张图片
                if (!imageUrls[0].isEmpty()) {
                    articleImage.setImageURI(Uri.parse(Constant.BUCKET_HOST_NAME + imageUrls[0] + Constant.IMG_SUFIX));
                    Logger.d(Constant.BUCKET_HOST_NAME + imageUrls[0] + Constant.IMG_SUFIX);
                } else {
                    articleImage.setImageURI(Uri.parse(ApiUrl.randomImageUrl(articleItem.getId()) + Constant.IMG_SUFIX));
                }
                articleBody.getSettings().setCacheMode(LOAD_CACHE_ELSE_NETWORK);
                articleBody.getSettings().setAllowContentAccess(false);
                articleBody.getSettings().setSavePassword(false);
                // 防范远程命令执行，http://jaq.alibaba.com/gc/devcenter.htm?helpid=75
                articleBody.removeJavascriptInterface("searchBoxJavaBridge_");
                articleBody.removeJavascriptInterface("accessibilityTraversal");
                articleBody.removeJavascriptInterface("accessibility");
                articleBody.loadDataWithBaseURL("", "<meta name=\"viewport\" content=\"" +
                        "width=device-width, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, " +
                        "user-scalable=no\" />" + "<style>img{display: inline;height: auto;max-width: 100%;}" +
                        "</style>" + articleItem.getBody(), "text/html", "UTF-8", "");
            } else {
                articleID = Constant.NOT_FOUND_ID;
                columnType = Constant.NO_FOUND_TYPE;
                new GetArticleTask().execute();
            }
        }
    }
}