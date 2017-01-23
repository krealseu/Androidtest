package org.kreal.ftpserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RequestStartStopReceiver extends BroadcastReceiver {
    static final String TAG = RequestStartStopReceiver.class.getSimpleName();

    public RequestStartStopReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received: " + intent.getAction());

        // TODO: analog code as in ServerPreferenceActivity.start/stopServer(), refactor
        try {
            FtpServerAndroid.Start(context,intent.getAction());
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
