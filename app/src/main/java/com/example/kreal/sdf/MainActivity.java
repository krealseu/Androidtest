package com.example.kreal.sdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findPref(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("sdf", Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        });
    }
    protected <T> T findPref(int key) {
        return (T) this.findViewById(key);
    }

}
