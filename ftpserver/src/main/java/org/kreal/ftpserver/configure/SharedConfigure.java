package org.kreal.ftpserver.configure;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lthee on 2016/10/23.
 */
public class SharedConfigure {
    public String getUser(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP", Context.MODE_PRIVATE);
        return sharedPref.getString("USER","Misaka");
    }
    public String getPassword(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP", Context.MODE_PRIVATE);
        return sharedPref.getString("PASSWARD","wonder");
    }
    public int getPort(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP", Context.MODE_PRIVATE);
        return sharedPref.getInt("PORT",2121);
    }
    public String getRoot(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP", Context.MODE_PRIVATE);
        return sharedPref.getString("ROOT","(/,/)");
    }
    public void setUser(Context context, String user){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("USER",user);
        editor.commit();
    }
    public void setPassword(Context context, String password){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("PASSWORD",password);
        editor.commit();
    }
    public void setPort(Context context, int port){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("USER",port);
        editor.commit();
    }
    public void setRoot(Context context, String root){
        SharedPreferences sharedPref = context.getSharedPreferences("FTP",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ROOT",root);
        editor.commit();
    }
}
