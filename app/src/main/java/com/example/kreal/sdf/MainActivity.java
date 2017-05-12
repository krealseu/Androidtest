package com.example.kreal.sdf;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.UriPermission;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.text.LoginFilter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;


public class MainActivity extends Activity implements View.OnTouchListener {
    ImageView imageView;
    org.kreal.Gesture.GestureDetector gestureDetector = new org.kreal.Gesture.GestureDetector();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setContentView(WindowUtils.setUpView(getApplicationContext()));

        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | FLAG_NOT_TOUCH_MODAL ,
                PixelFormat.TRANSPARENT);
        getWindowManager().addView(WindowUtils.setUpView(getApplicationContext()),wmParams);
//        setContentView(R.layout.activity_main);
//        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WindowUtils.showPopupWindow(getApplicationContext());
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//                Intent intent = VpnService.prepare(getApplicationContext());
//                if (intent != null) {
//                    startActivityForResult(intent, 0);
//                } else {
//                    onActivityResult(0, RESULT_OK, null);
//                }
//            }
//        });
//        Log.v("main",""+TrafficStats.getTotalRxBytes());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            startService(new Intent(getApplicationContext(),VPNservice.class));
        }
    }

    protected <T> T findPref(int key) {
        return (T) this.findViewById(key);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
