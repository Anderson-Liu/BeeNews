package cn.peacesky.beenews.util;

import java.util.List;

import cn.peacesky.beenews.model.ArticleItem;
import cn.peacesky.beenews.model.ListArticleItem;

/**
 * 使用经过重写的LruCache，实现文章缓存和图片缓存
 * TimeExpiringLruCache，即添加了缓存过期时间的，LruCache
 * Created by anderson on 7/27/16.
 */

public class CacheUtil {
    public static TimeExpiringLruCache<String, List<ListArticleItem>> simpleListCache;
    public static TimeExpiringLruCache<Integer, ArticleItem> detailArticleCache;
}
