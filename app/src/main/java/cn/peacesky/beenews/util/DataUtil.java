package cn.peacesky.beenews.util;

import android.util.Log;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.peacesky.beenews.model.ArticleItem;
import cn.peacesky.beenews.model.ListArticleItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 处理文章数据获取的相关事项
 * Created by anderson on 7/31/16.
 */

public class DataUtil {

    private OkHttpClient client = new OkHttpClient();

    private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic YW5kZXJzb246YW5kZXJzb24=")
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    /**
     * 把Json对象解析为ListArticleItem
     *
     * @param jsonItem Json 对象
     * @return ListArticleItem对象
     */
    private ListArticleItem parseJson2ListArticle(JSONObject jsonItem) {
        ListArticleItem listArticleItem = new ListArticleItem();
        try {
            int type = jsonItem.getInt("type");
            String summary = jsonItem.getString("summary");
            int aid = jsonItem.getInt("aid");
            String title = jsonItem.getString("title");
            String imageUrls = jsonItem.getString("imageUrls");
            String publishDate = jsonItem.getString("publishDate");
            int readTime = jsonItem.getInt("readTime");
            listArticleItem.setId(aid);
            listArticleItem.setType(type);
            listArticleItem.setTitle(title);
            listArticleItem.setSummary(summary);
            listArticleItem.setImageUrls(new String[]{imageUrls});
            listArticleItem.setPublishDate(publishDate);
            listArticleItem.setReadTimes(readTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listArticleItem;
    }


    /**
     * 将Json对象解析为 ArticleItem 对象
     *
     * @param jsonItem Json对象
     * @return 解析得到的 ArticleItem 对象
     */
    public ArticleItem parseJson2Article(JSONObject jsonItem) {
        ArticleItem articleItem = new ArticleItem();
        try {
            int type = jsonItem.getInt("type");
            int aid = jsonItem.getInt("aid");
            String title = jsonItem.getString("title");
            String content = jsonItem.getString("content");
            String source = jsonItem.getString("source");
            String imageUrls = jsonItem.getString("imageUrls");
            String publishDate = jsonItem.getString("publishDate");
            int readTime = jsonItem.getInt("readTime");
            articleItem.setId(aid);
            articleItem.setType(type);
            articleItem.setTitle(title);
            articleItem.setSource(source);
            articleItem.setBody(content);
            articleItem.setImageUrls(new String[]{imageUrls});
            articleItem.setPublishDate(publishDate);
            articleItem.setReadTimes(readTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return articleItem;
    }

    /**
     * 根据Url发送请求，返回得到的Body
     *
     * @param url 目标url
     * @return 返回得到的Body, 为String
     */
    public String request(String url) {
        String resultBody = null;
        try {
            resultBody = run(url);
        } catch (IOException e) {
            Logger.e(e, "IO exception");
            Log.e("BeeNews", "IOException", e);
        }
        return resultBody;
    }

    /**
     * 根据请求得到的Body构造成JSONArray对象
     *
     * @param result 请求得到的Body
     * @return 得到的JSONArray对象
     */
    public List<ListArticleItem> getListFromResult(String result) {
        List<ListArticleItem> list = new ArrayList<>();
        try {
            if (result == null) {
                return list;
            }
            JSONObject resultJson = new JSONObject(result);
            JSONArray items = (JSONArray) resultJson.get("_items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonItem = items.getJSONObject(i);
                ListArticleItem listArticleItem = this.parseJson2ListArticle(jsonItem);
                list.add(listArticleItem);
            }
        } catch (JSONException e) {
            Logger.e(e, "JSONException");
        }
        return list;
    }
}