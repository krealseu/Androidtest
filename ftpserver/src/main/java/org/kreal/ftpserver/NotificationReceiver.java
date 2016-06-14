package org.kreal.ftpserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    static final String TAG = RequestStartStopReceiver.class.getSimpleName();
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received: " + intent.getAction());

        // TODO: analog code as in ServerPreferenceActivity.start/stopServer(), refactor
        try {
            if (intent.getAction().equals(FtpServerCC.FTPSERVER_STARTED)) {
                FTPNotification.notify(context,FtpServerCC.getLocalIpAddress(context),FtpServerCC.getFtpServerState());
            } else if (intent.getAction().equals(FtpServerCC.FTPSERVER_STOPED)) {
                FTPNotification.cancel(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
