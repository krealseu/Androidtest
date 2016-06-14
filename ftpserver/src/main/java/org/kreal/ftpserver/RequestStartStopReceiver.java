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
            if (intent.getAction().equals(FtpServerCC.ACTION_START_FTPSERVER)) {
                Intent serverService = new Intent(context, FtpServerCC.class);
                context.startService(serverService);

            } else if (intent.getAction().equals(FtpServerCC.ACTION_STOP_FTPSERVER)) {
                Intent serverService = new Intent(context, FtpServerCC.class);
                context.stopService(serverService);
            }else if (intent.getAction().equals(FtpServerCC.ACTION_PAUSE_FTPSERVER)) {
                FtpServerCC.FTPServerPause();
            }else if (intent.getAction().equals(FtpServerCC.ACTION_RESUME_FTPSERVER)) {
                FtpServerCC.FTPServerResume();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
