package com.example.videotest;

import android.content.Intent;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.net.URI;
import org.kreal.akvideoplayer.view.AkVideoPlayer;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent().getAction()=="android.intent.action.MAIN") {
            Intent intent = new Intent();
            intent.setType("video/*"); //通过type来让系统选择 视频video/*
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 10);
        }
        if(getIntent().getAction() == "android.intent.action.VIEW"){
            Log.i("sf", "" + getIntent().getData().getScheme());
            File file = new File(getIntent().getData().getPath());
            Log.i("sf", "" + file.getName());
            ((AkVideoPlayer) getSupportFragmentManager().findFragmentById(R.id.VideoView)).playUrl(getIntent().getData());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            Log.i("sf", "" + data.getData());
            File file = new File(data.getData().getPath());
            Log.i("sf", "" + file.toURI());

            ((AkVideoPlayer) getSupportFragmentManager().findFragmentById(R.id.VideoView)).playUrl(data.getData());
        }
        else finish();
    }
}
