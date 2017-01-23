package org.kreal.ftpserver.virtualfilesystem;

import android.util.Log;

import org.apache.ftpserver.ftplet.FtpFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lthee on 2016/10/13.
 */
public class VirtualHomeFtpFile implements FtpFileCC {
    private static final String TAG = VirtualHomeFtpFile.class.getSimpleName();
    private UserCC userCC = null;

    public VirtualHomeFtpFile(UserCC userCC) {
        this.userCC = userCC;
    }

    @Override
    public String getRealPath() {
        return "/";
    }

    @Override
    public String getType() {
        return TAG;
    }

    @Override
    public String getAbsolutePath() {
        return "/";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean doesExist() {
        return true;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public String getOwnerName() {
        return "user";
    }

    @Override
    public String getGroupName() {
        return "group";
    }

    @Override
    public int getLinkCount() {
        return 0;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean setLastModified(long l) {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public boolean mkdir() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public boolean move(FtpFile ftpFile) {
        return false;
    }

    @Override
    public List<FtpFile> listFiles() {
        List<String> filenames = this.userCC.homeDirMap.getKeyList();
        List<FtpFile> ftpFiles = new ArrayList<>();
        for (String file:filenames){
            FtpFileCC temp = FtpFileHelper.create(this.userCC,file);
            if (temp != null)
                ftpFiles.add(temp);
        }
        return ftpFiles;
    }

    @Override
    public OutputStream createOutputStream(long l) throws IOException {
        return null;
    }

    @Override
    public InputStream createInputStream(long l) throws IOException {
        return null;
    }
}
