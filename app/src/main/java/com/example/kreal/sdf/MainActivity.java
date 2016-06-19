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
import java.io.IOException;


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
                File file1 = new File("/storage/9016-4EF8/sd.txt");
                try {
                    file1.createNewFile();
                    Log.i("sdf","ss");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("sdf","fail");
                }
                Log.i("sdf", "2");
                File[] files = getExternalFilesDirs(null);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent,1);
                for(File file:files){
                    Log.i("sdf", file.getAbsolutePath());
                }
                getApplicationContext().getExternalFilesDir(null);
            }
        });
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
