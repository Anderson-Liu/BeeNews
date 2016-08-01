package cn.peacesky.beenews.util;

/**
 * Created by tomchen on 2/21/16.
 */
public class Constant {
    // 七牛图片链接 域名
    public static final String BUCKET_HOST_NAME = "http://7xriwb.com1.z0.glb.clouddn.com/";
    //随机图片
    public static final String RANDOM_IMAGE = "http://7xriwb.com1.z0.glb.clouddn.com/";

    public static final String MONGO_HOST = "http://mongo.peacesky.cn";
    public static final String IMG_COLLECTION = "rotationImage";
    public static final int COUNT_IMAGE = 965;
    //轮播图片的数目
    public static final int COUNT_ROTATION = 5;
    // 控制还剩几项新闻的时候加载更多
    public static final int VISIBLE_THRESHOLD = 3;
    public static final String LOGGER_TAG = "LoggerTag";
    public static final int EVEPORT = 123;
    public static final String EVE_HOST = MONGO_HOST + ":" + EVEPORT;
    public static final String SIMP_COLLECTION = "SimpleArticle";
    public static final String FULL_COLLECTION = "FullArticle";
    // 添加此后缀可自动调用七牛云的图像压缩
    public static String IMG_SUFIX = "?imageMogr2/thumbnail/500x/strip/quality/50/format/webp";
}
