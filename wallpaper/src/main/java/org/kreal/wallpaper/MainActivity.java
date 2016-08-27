package org.kreal.wallpaper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.LineLayout).setBackground(WallpaperManager.getInstance(getApplicationContext()).getDrawable());
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v==button1)
            SwitchWapper.startService(getApplicationContext());
        else if(v == button2)
            SwitchWapper.stoptService(getApplicationContext());
        else if(v == button4)
            SwitchWapper.nextWall(getApplicationContext());
        else if(v == button3)
            SwitchWapper.updataImage(getApplicationContext());

    }
}
