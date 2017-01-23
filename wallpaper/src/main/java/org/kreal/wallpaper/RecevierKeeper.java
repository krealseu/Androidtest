package org.kreal.wallpaper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class RecevierKeeper extends Service {

    private BroadcastReceiver broadcastReceiver = null;

    private static String Action_register = "org.kreal.wallpaper.RecevierKeeper.register";
    private static String Action_unregister = "org.kreal.wallpaper.RecevierKeeper.unregister";

    static public void registerReceiver(Context context){
        Intent intent = new Intent(context,RecevierKeeper.class);
        intent.setAction(Action_register);
        context.startService(intent);
    }
    static public void unregisterReceiver(Context context){
        Intent intent = new Intent(context,RecevierKeeper.class);
        intent.setAction(Action_unregister);
        context.startService(intent);
    }
    public RecevierKeeper() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.v(RecevierKeeper.class.getSimpleName(),intent.getAction());
        String action = intent.getAction();
        if (action == Action_register){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            if (broadcastReceiver == null)
                broadcastReceiver = new OpenReceiver();
            getApplicationContext().registerReceiver(broadcastReceiver,intentFilter);
        }else if(action == Action_unregister){
//            broadcastReceiver = null;
            if (broadcastReceiver != null)
                getApplication().unregisterReceiver(broadcastReceiver);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
