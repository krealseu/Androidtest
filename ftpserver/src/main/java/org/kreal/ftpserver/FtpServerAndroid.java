package org.kreal.ftpserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.kreal.ftpserver.configure.SharedConfigure;
import org.kreal.ftpserver.virtualfilesystem.FileSystemFactoryCC;

import java.util.ArrayList;
import java.util.List;

public class FtpServerAndroid extends Service {
    static final String TAG = FtpServerAndroid.class.getSimpleName();
    private FtpServer ftpServer= null;
    static public final String ACTION_START_FTPSERVER = "org.kreal.FtpServer.ACTION_START_FTPSERVER";
    static public final String ACTION_STOP_FTPSERVER = "org.kreal.FtpServer.ACTION_STOP_FTPSERVER";
    static public final String ACTION_PAUSE_FTPSERVER = "org.kreal.FtpServer.ACTION_PAUSE_FTPSERVER";
    static public final String ACTION_RESUME_FTPSERVER = "org.kreal.FtpServer.ACTION_RESUME_FTPSERVER";
    static public final String FTPSERVER_STARTED = "org.kreal.FtpServer.FTPSERVER_STARTED";
    static public final String FTPSERVER_STOPED = "org.kreal.FtpServer.FTPSERVER_STOPED";
    static public final String FTPSERVER_PAUSED = "org.kreal.FtpServer.FTPSERVER_PAUSED";

    static public void Start(Context context ,String action){
        Intent intent = new Intent(context,FtpServerAndroid.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public void FtpServerInit(Context context) {
        Log.i(TAG,"create");
        SharedConfigure sharedConfigure = new SharedConfigure();
        FtpServerFactory serverFactory = new FtpServerFactory();
        //服务用户管理
        UserManager mUserManager= new PropertiesUserManagerFactory().createUserManager();
        serverFactory.setUserManager(mUserManager);
        BaseUser baseUser = new BaseUser();
        baseUser.setName(sharedConfigure.getUser(context));
        if (sharedConfigure.getPassword(context) != null)
            baseUser.setPassword(sharedConfigure.getPassword(context));
        baseUser.setHomeDirectory(sharedConfigure.getRoot(context));
        if (true){
            List<Authority> authorities = new ArrayList<Authority>();
            authorities.add(new WritePermission());
            baseUser.setAuthorities(authorities);
        }
        try {
            mUserManager.save(baseUser);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        //自定义文件系统
        serverFactory.setFileSystem(new FileSystemFactoryCC(context));
        //服务监听端口
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(sharedConfigure.getPort(context));
        serverFactory.addListener("default", listenerFactory.createListener());
        ftpServer = serverFactory.createServer();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String action = intent.getAction();
//        if (action == ACTION_START_FTPSERVER) {
//            if(mftpServer.start()){
//                FtpServerInit(getApplicationContext());
//                Intent receiver = new Intent(FTPSERVER_STARTED);
//                getApplicationContext().sendBroadcast(receiver);
//            }else onDestroy();
//        } else if (action == ACTION_STOP_FTPSERVER){
//            mftpServer.stop();
//            Intent receiver = new Intent(FTPSERVER_STOPED);
//            getApplicationContext().sendBroadcast(receiver);
////            onDestroy();
//        } else if (action == ACTION_PAUSE_FTPSERVER){
//            mftpServer.pause();
//            Intent receiver = new Intent(FTPSERVER_PAUSED);
//            getApplicationContext().sendBroadcast(receiver);
//        } else if (action == ACTION_RESUME_FTPSERVER){
//            mftpServer.resume();
//            Intent receiver = new Intent(FTPSERVER_STARTED);
//            getApplicationContext().sendBroadcast(receiver);
//
//        } else onDestroy();
//        return START_STICKY;
//    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == ACTION_START_FTPSERVER) {
           if (ftpServer == null)
               FtpServerInit(getApplicationContext());
            try {
                ftpServer.start();
                Intent receiver = new Intent(FTPSERVER_STARTED);
                sendBroadcast(receiver);
            } catch (FtpException e) {
                e.printStackTrace();
            }
        } else if (action == ACTION_STOP_FTPSERVER){
            if (ftpServer!=null)
                ftpServer.stop();
            ftpServer = null;
            Intent receiver = new Intent(FTPSERVER_STOPED);
            sendBroadcast(receiver);
            stopSelf();
        } else if (action == ACTION_PAUSE_FTPSERVER){
            if (ftpServer!=null)
                ftpServer.suspend();
            Intent receiver = new Intent(FTPSERVER_PAUSED);
            getApplicationContext().sendBroadcast(receiver);
        } else if (action == ACTION_RESUME_FTPSERVER){
            if (ftpServer!=null)
                ftpServer.suspend();
            Intent receiver = new Intent(FTPSERVER_STARTED);
            getApplicationContext().sendBroadcast(receiver);

        } else onDestroy();
        return START_REDELIVER_INTENT;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

}
