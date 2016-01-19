package org.kreal.viewtest;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class FullscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Intent intent=getIntent();
        Log.i("ds","dsa");
        if(intent!=null) {
            Log.i("ds","dssa");
            Log.i("ds",intent.getAction());
            ((VideoFragment) getSupportFragmentManager().findFragmentById(R.id.full_video)).play(intent.getData());
        }
        findViewById(R.id.full_video).setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_IMMERSIVE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
