package com.dxys.demo.bingo;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by dxys on 17/10/4.
 */

public class Utlis {
    private static final boolean DEBUG = true;

    public static void log( String ifo)
    {
        if (DEBUG)
            Log.e("ifo: ",ifo);
    }

    public static void log(String tag, String ifo)
    {
        if (DEBUG)
        Log.e(tag,ifo);
    }

}
