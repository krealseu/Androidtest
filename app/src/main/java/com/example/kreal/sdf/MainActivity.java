package com.example.kreal.sdf;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);
//        Button button = findPref(R.id.button);
        imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/123.png"));
        Matrix matrix= new Matrix();
//        matrix.setRotate(20);
        matrix.setScale(0.3f,0.3f);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageMatrix(matrix);
//        imageView.setImageBitmap(BitmapFactory.decodeFile("/sdcard/123.png"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Log.i("sdf",data.getData().toString());
            File file1 = new File("/storage/9016-4EF8/sd.txt");
            try {
                file1.createNewFile();
                Log.i("sdf","ss");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("sdf","fail");
            }
        }
    }

    protected <T> T findPref(int key) {
        return (T) this.findViewById(key);
    }

}
