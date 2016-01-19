package org.kreal.vpn;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class MainActivity extends Activity implements Handler.Callback{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo()!=null){
            GetMima getMima = new GetMima();
            getMima.setHander(new Handler(this));
            new Thread(getMima).start();
            Intent i = new Intent("android.net.vpn.SETTINGS");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(new Intent(i));
        }
        else Toast.makeText(getApplicationContext(),"Net >_<",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean handleMessage(Message msg) {
        String mima=msg.getData().getString("mima");
        Toast.makeText(getApplicationContext(),"SECRET:"+mima.trim(),Toast.LENGTH_SHORT).show();
        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("mima", mima.trim()));
        return false;
    }
}
