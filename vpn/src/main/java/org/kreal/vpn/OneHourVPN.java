package org.kreal.vpn;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class OneHourVPN extends IntentService {
    private String mima = null;
    public OneHourVPN() {
        super("OneHourVPN");
    }

    public static Intent getIntent(Context context) {
        Intent intent =  new Intent(context, OneHourVPN.class);
        intent.setAction("fuck");
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getActiveNetworkInfo()!=null) {
                Intent i = new Intent("android.net.vpn.SETTINGS");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
                Thread thread = new Thread(new GetMima());
                thread.start();
                try {
                    thread.join(3000);
                    if(mima != null) {
                        Toast.makeText(getApplicationContext(), "vpnb.org <^_^> SECRET:" + mima.trim(), Toast.LENGTH_SHORT).show();
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("mima", mima.trim()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else Toast.makeText(getApplicationContext(),"Net >_<",Toast.LENGTH_SHORT).show();
        }
    }

    private class GetMima implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL("http://74.207.253.106/mm.txt");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
//                connection.connect();
                InputStream is = connection.getInputStream();
                mima=read(is);
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private  String read(InputStream in){
            StringBuffer out = new StringBuffer();
            String result=new String();
            byte[] b = new byte[1024];
            int len=0;
            try {
                while ((len=in.read(b))!=-1){
                    out.append(new String(b,0,len));
                }
                result=out.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
