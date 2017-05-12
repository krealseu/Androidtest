package org.kreal.ftpserver;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Switch;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FtpServerAndroid.Start(getApplicationContext(),FtpServerAndroid.ACTION_START_FTPSERVER);
        aSwitch = (Switch)findViewById(R.id.switch1);
        Button button = (Button) findViewById(R.id.button111);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent,22);
            }
        });
        aSwitch = (Switch)findViewById(R.id.switch1);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFTPserverQR.startQRActivity(getApplicationContext(),"test");
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FtpServerAndroid.Start(getApplicationContext(),FtpServerAndroid.ACTION_START_FTPSERVER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver contentResolver =getContentResolver();
            final int task = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            List<UriPermission> list = contentResolver.getPersistedUriPermissions();
            for (UriPermission uriPermission:list)
                contentResolver.releasePersistableUriPermission(uriPermission.getUri(),task);
            contentResolver.takePersistableUriPermission(uri , task);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
