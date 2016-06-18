package com.example.kreal.sdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findPref(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReDecodeThread.ReDecodeThreadResult reDecodeThreadResult = new ReDecodeThread.ReDecodeThreadResult() {
                    @Override
                    public void onReDecodeResult(String url) {
                        Log.i("sdf",url);
                    }
                };
                ReDecodeThread.encode(BitmapFactory.decodeFile("/sdcard/123.png"),reDecodeThreadResult);
//                Log.i("sdf", "2");
//                File[] files = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                startActivity(intent);
//                for(File file:files){
//                    Log.i("sdf", file.getAbsolutePath());
//                }
//                getApplicationContext().getExternalFilesDir(null);
            }
        });
    }
    protected <T> T findPref(int key) {
        return (T) this.findViewById(key);
    }

}
