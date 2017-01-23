package org.kreal.ftpserver;


import android.nfc.Tag;
import android.util.Log;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lthee on 2016/10/3.
 */
public class FtpServerCC {
    static final String TAG = FtpServerCC.class.getSimpleName();
    private FtpServer mftpServer = null;
    private UserManager mUserManager= new PropertiesUserManagerFactory().createUserManager();;
    private FileSystemFactory mfileSystemFactory = null;
    private int mPort = 2121;
    private Ftplet mFtplet = null;
    private String mAddress = null;
    static private FtpServerCC mInstance = null;

    static public FtpServerCC getInstance(){
        if(mInstance == null) {
            mInstance = new FtpServerCC();
        }
        return mInstance;
    }

    private  FtpServerCC() {
    }

    public boolean adduser(String name, String password, String root, boolean write){
        BaseUser baseUser = new BaseUser();
        baseUser.setName(name);
        if (password != null)
            baseUser.setPassword(password);
        baseUser.setHomeDirectory(root);
        if (write){
            List<Authority> authorities = new ArrayList<Authority>();
            authorities.add(new WritePermission());
            baseUser.setAuthorities(authorities);
        }
        try {
            mUserManager.save(baseUser);
            return true;
        } catch (FtpException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean start(){
        if (mftpServer != null) {
            if((mftpServer.isStopped()||mftpServer.isSuspended())){
                mftpServer.resume();
            }
            return true;
        }
        else {
            mftpServer = createftpserver();
            if (mftpServer == null)
                return false;
            else {
                try {
                    mftpServer.start();
                } catch (FtpException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
    }

    public void pause(){
        if(mftpServer!= null)
            mftpServer.suspend();
    }

    public void resume(){
        if(mftpServer!= null)
            mftpServer.resume();
    }

    public void stop(){
        if(mftpServer!= null) {
            mftpServer.stop();
            mftpServer = null;
        }
    }

    public void setFtplet(Ftplet mFtplet) {
        this.mFtplet = mFtplet;
    }

    public void setFileSystemFactory(FileSystemFactory mfileSystemFactory) {
        this.mfileSystemFactory = mfileSystemFactory;
    }

    public void setPort(int mPort) {
        this.mPort = mPort;
    }

    public void setAddressPort(String mAddress , int mPort) {
        this.mAddress = mAddress;
        this.mPort = mPort;
    }

    private FtpServer createftpserver(){
        FtpServerFactory serverFactory = new FtpServerFactory();
        //服务用户管理
        serverFactory.setUserManager(mUserManager);
        //事件通知
        if (mFtplet != null) {
            Map<String, Ftplet> ftpletMap = new HashMap<String, Ftplet>();
            ftpletMap.put(TAG, mFtplet);
            serverFactory.setFtplets(ftpletMap);
        }
        //自定义文件系统
        if (mfileSystemFactory != null)
            serverFactory.setFileSystem(mfileSystemFactory);
        //服务监听端口
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(mPort);
        if (mAddress != null)
            listenerFactory.setServerAddress(mAddress);
        serverFactory.addListener("default", listenerFactory.createListener());
        return serverFactory.createServer();
    }

}


