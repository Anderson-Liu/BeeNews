package cn.peacesky.beenews.util;

/**
 * Created by tomchen on 2/4/16.
 */
public class ColumnType {

    public static final int LATEST = 0;         // 最新新闻
    public static final int TZGG = 1;           // 通知公告
    public static final int JSTX = 2;           // 技术体系
    public static final int CYDT = 3;           // 产业动态


    /*
    根据positon，得到对应的栏目
    注意 position 是连续的，栏目不是连续
     */
    public static int getType(int position) {
        int[] types = {LATEST, TZGG, JSTX, CYDT};
        return types[position];
    }
}
