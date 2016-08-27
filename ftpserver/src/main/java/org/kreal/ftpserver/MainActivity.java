package org.kreal.ftpserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(FtpServerCC.S)
            }
        });
    }
}
