package com.example.kreal.sdf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.TwoStatePreference;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.net.InetAddress;

import be.ppareit.swiftp.FsApp;
import be.ppareit.swiftp.FsService;
import be.ppareit.swiftp.FsSettings;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //int port = FsSettings.getPortNumber();
        //Switch sf = (Switch) findViewById (R.id.sf);
        Switch sf=findPref(R.id.sf);
        updateRunningState();
        sf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startServer();
                } else {
                    stopServer();
                }
            }
        });
        Button bu=findPref(R.id.button);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRunningState();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startServer() {
        sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
    }

    private void stopServer() {
        sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
    }


    private void updateRunningState() {
        Switch sf = findPref(R.id.sf);
        if (FsService.isRunning() == true) {
            sf.setChecked(true);
            InetAddress address = FsService.getLocalInetAddress();
            if (address == null) {
                Log.v(TAG, "Unable to retreive wifi ip address");
                return;
            }
            String iptext = "ftp://" + address.getHostAddress() + ":"
                    + FsSettings.getPortNumber() + "/";
            TextView tv=findPref(R.id.textView);
            tv.setText(iptext);
        }else {
            sf.setChecked(false);
        }
    }
    protected <T> T findPref(int key) {
        return (T) this.findViewById(key);
    }
}
