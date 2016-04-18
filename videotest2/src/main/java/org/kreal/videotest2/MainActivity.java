package org.kreal.videotest2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.kreal.akvideoplayer.decoder.CCMediaDecoder;
import org.kreal.akvideoplayer.view.AkVideoPlayer;
import org.kreal.akvideoplayer.view.AkVideoPlayer2;
import org.kreal.akvideoplayer.view.CCVideoView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE=111;
    private CCVideoView ccVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ccVideoView = new CCVideoView(this);
        //setContentView(ccVideoView);
        showChooser();
    }
    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent("video/*");
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, "Chooser Video");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        Log.i("sdf", "Uri = " + uri.toString());
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
//                            ccVideoView.setVideoPath(path);
//                            ccVideoView.start();
//                            ccVideoView.seekTo(40);
                            ((AkVideoPlayer2) getSupportFragmentManager().findFragmentById(R.id.VideoView)).playUrl(path);
                        } catch (Exception e) {
                            finish();
                        }
                    }
                }
                else finish();
                break;
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }



}
