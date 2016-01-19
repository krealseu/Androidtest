package org.kreal.vpn;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Created by Kreal on 2015/9/22.
 */
public class GetMima implements Runnable {
    private String mima;
    private Context mcontext=null;
    private Handler hander;

    public void setHander(Handler hander) {
        this.hander = hander;
    }


    public GetMima() {

    }
    @Override
    public void run() {
        try {
            URL url = new URL("http://104.237.156.248/mm.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
//            connection.connect();
            InputStream is = connection.getInputStream();
            mima=read(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg =new Message();
        Bundle bundle=new Bundle();
        bundle.putString("mima",mima);
        msg.setData(bundle);
        hander.sendMessage(msg);
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
