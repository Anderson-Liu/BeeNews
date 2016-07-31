package cn.peacesky.beenews.util;

import java.util.Random;

/**
 * url构造
 */
public class ApiUrl {
    /**
     * 随机图片
     */
    public static String randomImageUrl(int seed) {
        Random random = new Random(seed);
        return Constant.RANDOM_IMAGE + random.nextInt(Constant.COUNT_IMAGE);
    }
}