package org.kreal.ftpserver.virtualfilesystem;

import android.content.Context;
import android.util.Log;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

/**
 * Created by lthee on 2016/10/23.
 */
public class FileSystemFactoryCC implements org.apache.ftpserver.ftplet.FileSystemFactory {
    static final public String TAG = FileSystemFactoryCC.class.getSimpleName();
    private Context context = null;

    public FileSystemFactoryCC(Context context) {
        this.context = context;
    }

    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        Log.i(TAG,"creatview");
        return new FileSystemViewCC(context,user);
    }
}
