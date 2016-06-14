package org.kreal.ftpserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FtpServerCC extends Service {
    static final String TAG = FtpServerCC.class.getSimpleName();
    private static FtpServer ftpServer = null;
    private static Context mContext = null;
    static public final String ACTION_START_FTPSERVER = "org.kreal.FtpServer.ACTION_START_FTPSERVER";
    static public final String ACTION_STOP_FTPSERVER = "org.kreal.FtpServer.ACTION_STOP_FTPSERVER";
    static public final String ACTION_PAUSE_FTPSERVER = "org.kreal.FtpServer.ACTION_PAUSE_FTPSERVER";
    static public final String ACTION_RESUME_FTPSERVER = "org.kreal.FtpServer.ACTION_RESUME_FTPSERVER";
    static public final String FTPSERVER_STARTED = "org.kreal.FtpServer.FTPSERVER_STARTED";
    static public final String FTPSERVER_STOPED = "org.kreal.FtpServer.FTPSERVER_STOPED";

    public FtpServerCC() {
    }

    public static void FTPServerPause(){
        if(ftpServer!=null)
            ftpServer.suspend();
        Intent receiver = new Intent(FTPSERVER_STARTED);
        mContext.sendBroadcast(receiver);
    }
    public static void FTPServerResume(){
        if(ftpServer!=null)
            ftpServer.resume();
        Intent receiver = new Intent(FTPSERVER_STARTED);
        mContext.sendBroadcast(receiver);
    }
    public static int getFtpServerState()
    {
        if(ftpServer==null)
            return 0;
        else if(ftpServer.isSuspended())
            return 2;
        else return 1;
    }
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
        // return null;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplication();
        if (ftpServer != null) {
            if((ftpServer.isStopped()||ftpServer.isSuspended())){
                Log.v(TAG, "Ftp Server Resuming");
                ftpServer.resume();
                Intent receiver = new Intent(FTPSERVER_STARTED);
                sendBroadcast(receiver);
                return START_STICKY;
            }
            else{
                Log.v(TAG, "Ftp Server is Started");
                return START_STICKY;
            }
        }
        ftpServer = createftp(2121);
        try {
            ftpServer.start();
            Log.v(TAG, "Start New Ftp Server");
            Intent receiver = new Intent(FTPSERVER_STARTED);
            sendBroadcast(receiver);
            return START_STICKY;
        } catch (FtpException e) {
            e.printStackTrace();
            Log.v(TAG, "Start Ftp Server Failed  >_<");
            onDestroy();
        }
        return START_STICKY;
    }
    private FtpServer createftp(int port){
        FtpServerFactory serverFactory = new FtpServerFactory();

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        try {
            InputStream inputStream = getAssets().open("users.properties");
            File temp = File.createTempFile("user",".properties");

            FileOutputStream fileOutputStream = new FileOutputStream(temp);
            byte[] buffer = new byte[1024];
            int len = 0;
            while (true) {
                len = inputStream.read(buffer);
                if (len == -1)
                    break;
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
            inputStream.close();
            userManagerFactory.setFile(temp);
            serverFactory.setUserManager(userManagerFactory.createUserManager());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port);
        serverFactory.addListener("default", factory.createListener());

        return serverFactory.createServer();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ftpServer != null) {
            ftpServer.stop();
            ftpServer = null;
        }
        Intent receiver = new Intent(FTPSERVER_STOPED);
        sendBroadcast(receiver);
    }
}
