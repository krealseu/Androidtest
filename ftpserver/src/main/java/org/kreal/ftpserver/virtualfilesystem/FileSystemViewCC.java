package org.kreal.ftpserver.virtualfilesystem;

import android.content.Context;
import android.content.UriPermission;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.kreal.ftpserver.Util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by lthee on 2016/10/13.
 */
public class FileSystemViewCC implements FileSystemView {
    private static final String TAG = FileSystemViewCC.class.getSimpleName();
    private UserCC userCC = new UserCC();
    private String rootDir = "/";
    private String currDir = "/";


    public FileSystemViewCC(Context context , User user) {
        //init user
        userCC.user = user;
        userCC.context = context;
        userCC.homeDirMap = new DoubleList<>();
        userCC.homeDirMap.put("/sdcard",Environment.getExternalStorageDirectory().getAbsolutePath());
//        userCC.homeDirMap.put("/download",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        Log.i(TAG,user.getHomeDirectory());
        List<UriPermission> permissions = context.getContentResolver().getPersistedUriPermissions();
        if (permissions.size()>0)
            userCC.homeDirMap.put("/SD卡", FileUtil.getFullPathFromTreeUri(permissions.get(0).getUri(),userCC.context));
    }

    @Override
    public FtpFile getHomeDirectory() throws FtpException {
        return new VirtualHomeFtpFile(userCC);
    }

    @Override
    public FtpFile getWorkingDirectory() throws FtpException {
        if (currDir.length() == 1 && currDir.charAt(0) == 47)
            return getHomeDirectory();
        else return FtpFileHelper.create(userCC,currDir);
    }

    @Override
    public boolean changeWorkingDirectory(String path) throws FtpException {
        String filename ;
        if (path.charAt(0) == 47)
            filename = path;
        else filename = currDir + File.separator + path;
        filename = FtpFileHelper.normalizedPath(filename);
        if (FtpFileHelper.isDirectory(userCC,filename)) {
            currDir = filename;
            return true;
        }
        else return false;
    }

    @Override
    public FtpFile getFile(String filename) throws FtpException {
        String ftppath ;
        if (filename.startsWith(File.separator))
            ftppath = filename;
        else ftppath = currDir + File.separator + filename;

        ftppath = FtpFileHelper.normalizedPath(ftppath);
        if (ftppath.matches(File.separator))
            return getHomeDirectory();

        return FtpFileHelper.create(userCC,ftppath);
    }

    @Override
    public boolean isRandomAccessible() throws FtpException {
        return true;
    }

    @Override
    public void dispose() {

    }

}
