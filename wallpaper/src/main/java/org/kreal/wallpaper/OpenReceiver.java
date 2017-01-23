package org.kreal.wallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class OpenReceiver extends BroadcastReceiver {
    private long lastime = 0;
    public OpenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"Last Time "+lastime,Toast.LENGTH_SHORT).show();
        long time = SystemClock.elapsedRealtime();
        if (Math.abs(time - lastime)>1000*600) {
            SwitchWallpaper.startActionRandom(context);
            lastime = time;
        }
    }
}
