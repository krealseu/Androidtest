package org.kreal.wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Wallpaperbyshare extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent i= WallpaperManager.getInstance(getApplicationContext()).getCropAndSetWallpaperIntent((Uri)getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
        finish();
    }
}
