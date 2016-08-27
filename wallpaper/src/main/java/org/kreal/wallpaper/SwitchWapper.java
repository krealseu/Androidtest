package org.kreal.wallpaper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SwitchWapper extends Service {
    private final String TAG = SwitchWapper.class.getSimpleName();
    private String fileroot = "/sdcard/CCbizhi" ;
    private AlarmManager alarmManager = null;
    private PendingIntent pi;
    private ArrayList<String> files = new ArrayList<String>();
    private WallpaperManager wpManager = null;
    private int lasti = 0;
    private int count = 0;

    static public void startService(Context context){
        Intent intent=new Intent(context,SwitchWapper.class);
        context.startService(intent);
    }
    static public void nextWall(Context context){
        Intent intent=new Intent(context,SwitchWapper.class);
        intent.setAction("NEXT");
        context.startService(intent);
    }
    static public void updataImage(Context context){
        Intent intent=new Intent(context,SwitchWapper.class);
        intent.setAction("UPDATA");
        context.startService(intent);
    }
    static public void stoptService(Context context){
        Intent intent=new Intent(context,SwitchWapper.class);
        context.stopService(intent);
    }
    public SwitchWapper() {

    }

    private void updataImage(){
        File wallfile = new File(fileroot);
        String[] strings = wallfile.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg")||filename.endsWith(".png");
            }
        });
        files.clear();
        for(int i = 0;i < strings.length;i++){
            files.add(strings[i]);
        }
    }

    private void changewallpaper(){
        Log.v(TAG,"Rand change wallpaper");
        if(files.size()==0)
            return;
        int Tint;
        do {
            Tint = new Random().nextInt(files.size());
        }
        while(lasti == Tint);
        lasti = Tint;
        setWallPaper(fileroot+"/"+files.get(lasti));
    }

    private void nextWallpaper(){
        lasti = (lasti+1)%files.size();
        setWallPaper(fileroot+"/"+files.get(lasti));
    }

    private void setWallPaper(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wpManager.setBitmap(BitmapFactory.decodeFile(url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG,"onCreate");
        updataImage();
        wpManager = WallpaperManager.getInstance(getApplicationContext());
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(),SwitchWapper.class);
        pi = PendingIntent.getService(getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),AlarmManager.INTERVAL_HOUR,pi);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG,"onstartcommand");
        String action = intent.getAction();
        if(action == null)
            changewallpaper();
        else if(action.equals("NEXT"))
            nextWallpaper();
        else if(action.equals("UPDATA"))
            updataImage();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestory");
        alarmManager.cancel(pi);
    }
}
