package org.kreal.akvideoplayer.utli;

import android.util.Log;

/**
 * Created by lthee on 2016/1/26.
 */
public class DebugUtil {
    static boolean debug = true;
    public static void LogInfo(String tag ,String info){
        if(debug)
            Log.i(tag,info);
    }
}
