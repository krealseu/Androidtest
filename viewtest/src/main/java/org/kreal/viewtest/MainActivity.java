package org.kreal.viewtest;

import android.app.FragmentManager;
import android.content.Intent;
import android.media.MediaCodec;
import android.net.VpnService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final int VIDEO_REQUEST_CODE = 0x0E;
    private boolean waitingForVPNStart;
    MediaCodec mediaCodec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=findview(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*"); //通过type来让系统选择 视频video/*
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, VIDEO_REQUEST_CODE);
            }
        });
        final Button vpnbutton=findview(R.id.vpn);
        vpnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Main2Activity.class));
            }
        });
        final ImageView imageView= findview(R.id.imageView);
        ViewGestureListener listener=new ViewGestureListener();
        final GestureDetector gestureDetector=new GestureDetector(this,listener);
        gestureDetector.setOnDoubleTapListener(listener);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        Log.i("fsd", "123");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    <T> T findview(int id){
        return (T)findViewById(id);
    }

}
