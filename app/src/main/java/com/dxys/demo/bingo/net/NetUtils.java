package com.dxys.demo.bingo.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by dxys on 17/3/29.
 * 网络状态检查
 */

public class NetUtils {

    public static int WIFI = 1;
    public static int MOBLIE = 2;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int get_content_type(Context context) {
        int content_type = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.isConnected()) {
                if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    content_type = WIFI;
                } else if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    content_type = MOBLIE;
                }
            }
        }
        return content_type;
    }
}
