package cn.peacesky.beenews.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.peacesky.beenews.ui.activity.first.DetailActivity;

/**
 * 通知推送事件处理。
 * 用户点击后跳转，统计用户对于推送通知的点击率等。
 * 发送通知的协议：
 * 通知中需要在Extra_Extra中包含两个字段：
 * type: 文章的类型，为(1, 2, 3, 6)中的一个
 * id: 文章的ID
 * 随后会在DetailActivity中通过type + id到数据库查找文章并显示。
 */
public class JpushReceiver extends BroadcastReceiver {

    public static final String IS_FROM_JPUSH = "isFromJpush";
    private static final String TAG = "JPush: ";

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Logger.i(TAG + "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(e, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Logger.d(TAG + "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        // TODO: 7/23/16 发送的默认推送消息，做些统计或者其他工作
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG + "[JpushReceiver] 接收到推送下来的通知");
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.d(TAG + "[JpushReceiver] 接收到推送下来的通知的ID: " + notificationId);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Logger.d(TAG + "[JpushReceiver] 接收到推送下来的通知的EXTRA内容: " + extra);

        } else if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.d(TAG + "[JpushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG + "[JpushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.d(TAG + "[JpushReceiver] 用户点击打开了通知");
            // 用户点击后的行为，自定义页面
            // 打开自定义的Activity
            Intent i = new Intent(context, DetailActivity.class);
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.d(TAG + "[JpushReceiver] 接收到推送下来的通知的ID: " + notificationId);

            i.putExtra(IS_FROM_JPUSH, true);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }
}