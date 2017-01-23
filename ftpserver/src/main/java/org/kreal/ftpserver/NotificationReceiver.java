package org.kreal.ftpserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.kreal.ftpserver.Util.Ip;

public class NotificationReceiver extends BroadcastReceiver {
    static final String TAG = RequestStartStopReceiver.class.getSimpleName();
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received: " + intent.getAction());

        // TODO: analog code as in ServerPreferenceActivity.start/stopServer(), refactor
        try {
            if (intent.getAction().equals(FtpServerAndroid.FTPSERVER_STARTED)) {
                FTPNotification.notify(context, Ip.getLocalIpAddress(context),1);
            } else if (intent.getAction().equals(FtpServerAndroid.FTPSERVER_STOPED)) {
                FTPNotification.cancel(context);
            } else if (intent.getAction().equals(FtpServerAndroid.FTPSERVER_PAUSED)) {
                FTPNotification.notify(context, Ip.getLocalIpAddress(context),2);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
