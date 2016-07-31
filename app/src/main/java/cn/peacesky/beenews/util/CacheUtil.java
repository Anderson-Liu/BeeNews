package cn.peacesky.beenews.util;

import java.util.List;

import cn.peacesky.beenews.model.ArticleItem;
import cn.peacesky.beenews.model.ListArticleItem;

/**
 * Created by anderson on 7/27/16.
 */

public class CacheUtil {
    public static TimeExpiringLruCache<String, List<ListArticleItem>> simpleListCache;
    public static TimeExpiringLruCache<Integer, ArticleItem> detailArticleCache;
}
