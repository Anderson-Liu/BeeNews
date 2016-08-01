package cn.peacesky.beenews.util;

import android.app.Activity;

import cn.peacesky.beenews.R;

/**
 * 改变主题工具类
 * 全局变量可以利用 类静态变量 或 preference
 */
public class ThemeTool {
    public static void changeTheme(Activity activity) {
        if (PrefUtils.isDarkMode()) {
            activity.setTheme(R.style.AppThemeDark);
            activity.setContentView(R.layout.activity_main);
        }
    }
}
