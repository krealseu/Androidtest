package org.kreal.wallpaper;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SwitchWallpaper extends IntentService {
    private final String TAG = SwitchWallpaper.class.getSimpleName();
    private String fileroot = "/sdcard/CC/BiZhi" ;
    private static int num = 0;
    private static final String ACTION_RANDOM = "org.kreal.wallpaper.action.RANDOMSWITCH";

    public SwitchWallpaper() {
        super("SwitchWallpaper");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRandom(Context context) {
        Intent intent = new Intent(context, SwitchWallpaper.class);
        intent.setAction(ACTION_RANDOM);
        context.startService(intent);
    }
    public static Intent getActionRandom(Context context) {
        Intent intent = new Intent(context, SwitchWallpaper.class);
        intent.setAction(ACTION_RANDOM);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RANDOM.equals(action)) {
//                Log.v(TAG,"Random Switch "+num);
/*                try {
                    new File("/sdcard/CC",""+ SystemClock.elapsedRealtime()/1000+" "+num).createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                RandomPaper();
            }
        }
    }

    synchronized private void RandomPaper(){
        WallpaperManager wpManager = WallpaperManager.getInstance(getApplicationContext());
        File wallfile = new File(fileroot);
        String[] strings = wallfile.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg")||filename.endsWith(".png");
            }
        });
        if (strings == null)
            return;
        int i ;
        do {
            i = new Random().nextInt(strings.length);
        }while (i == num);
        num = i;
        try {
            wpManager.setStream(new FileInputStream(fileroot+File.separator+strings[num]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
