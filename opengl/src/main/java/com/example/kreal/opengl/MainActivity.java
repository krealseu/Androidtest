package com.example.kreal.opengl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private MyGLSurfaceView  mGLSurfaceView;
    private GLSurfaceView  glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        mGLSurfaceView = new MyGLSurfaceView(this);
//        glSurfaceView=new GLSurfaceView(this);
        // Check if the system supports OpenGL ES 2.0.
//        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
//        glSurfaceView.setEGLContextClientVersion(2);
//        LessonOneRenderer lessonOneRenderer=new LessonOneRenderer();
//        lessonOneRenderer.setMcontext(getApplicationContext());
//        glSurfaceView.setRenderer(lessonOneRenderer);
        setContentView(mGLSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
