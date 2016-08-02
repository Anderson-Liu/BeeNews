package cn.peacesky.beenews.util;

/*
 * Created by anderson on 7/21/16.
 */
public class Constant {
    // 七牛云文章图片域名
    public static final String BUCKET_HOST_NAME = "http://7xriwb.com1.z0.glb.clouddn.com/";
    //轮播图片的数目
    public static final int COUNT_ROTATION = 5;
    // 控制还剩几项新闻的时候加载更多
    public static final int VISIBLE_THRESHOLD = 3;
    public static final String LOGGER_TAG = "LoggerTag";
    // MongoDB中存储轮播图片的collection
    public static final String IMG_COLLECTION = "rotationImage";
    // MongoDB中存储SimpleArticle的collection
    public static final String SIMP_COLLECTION = "SimpleArticle";
    // MongoDB中存储ArticleItem的collection
    public static final String FULL_COLLECTION = "FullArticle";
    public static final int NOTIFI_NOT_FOUND_TYPE = 0;
    public static final int NOTIFI_NOT_FOUND_ID = 0;
    public static final int NOT_FOUND_ID = 1;
    public static final int NO_FOUND_TYPE = 0;
    // 七牛云随机图片域名
    static final String RANDOM_IMAGE = "http://7xriwb.com1.z0.glb.clouddn.com/";
    // 七牛云中随机图片的总数
    static final int COUNT_IMAGE = 965;
    // 七牛云mongo主机地址，也是python EVE框架的地址
    private static final String MONGO_HOST = "http://mongo.peacesky.cn";
    // Python EVE 的服务器监听窗口
    private static final int EVEPORT = 123;
    // Python EVE 的服务器 IP + PORT
    public static final String EVE_HOST = MONGO_HOST + ":" + EVEPORT;
    // 添加此后缀可自动调用七牛云的图像压缩
    public static String IMG_SUFIX = "?imageMogr2/thumbnail/500x/strip/quality/50/format/webp";
}