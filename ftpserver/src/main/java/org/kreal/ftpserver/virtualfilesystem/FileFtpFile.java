package org.kreal.ftpserver.virtualfilesystem;

import android.util.Log;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lthee on 2016/10/12.
 */
public class FileFtpFile implements FtpFileCC {
    private static final String TAG = FileFtpFile.class.getSimpleName();
    private UserCC userCC = null;
    private File file = null;
    private String ftppath = null;

    public FileFtpFile(UserCC user,String ftppath,java.io.File file) {
        this.userCC = user;
        this.ftppath = ftppath;
        this.file = file;
    }

    @Override
    public String getAbsolutePath() {
        String fullName = this.ftppath;
        int filelen = fullName.length();
        if(filelen != 1 && fullName.charAt(filelen - 1) == 47) {
            fullName = fullName.substring(0, filelen - 1);
        }
        return fullName;
    }

    @Override
    public String getName() {
        if(this.ftppath.equals("/")) {
            return "/";
        } else {
            String shortName = this.ftppath;
            int filelen = this.ftppath.length();
            if(shortName.charAt(filelen - 1) == 47) {
                shortName = shortName.substring(0, filelen - 1);
            }

            int slashIndex = shortName.lastIndexOf(47);
            if(slashIndex != -1) {
                shortName = shortName.substring(slashIndex + 1);
            }

            return shortName;
        }
    }

    @Override
    public boolean isHidden() {
        return file.isHidden();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public boolean doesExist() {
        return file.exists();
    }

    @Override
    public boolean isReadable() {
        return file.canRead();
    }

    public boolean isWritable() {
        if(this.userCC.user.authorize(new WriteRequest(this.getAbsolutePath())) == null) {
            return false;
        } else {
            if(this.file.exists()) {
                return this.file.canWrite();
            } else {
                return true;
            }
        }
    }

    public boolean isRemovable() {
        return isWritable();
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
        return this.file.isDirectory()?3:1;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public boolean setLastModified(long l) {
        return file.setLastModified(l);
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public boolean mkdir() {
        boolean retVal = false;
        if(this.isWritable()) {
            retVal = this.file.mkdir();
        }
        return retVal;
    }

    @Override
    public boolean delete() {
        boolean retVal = false;
        if(this.isRemovable()) {
            retVal = this.file.delete();
        }

        return retVal;
    }

    @Override
    public boolean move(FtpFile dest) {
        boolean retVal = false;
        if(dest.isWritable() && this.isReadable()) {
            if (((FtpFileCC)dest).getType() == "File") {
                File destFile = ((FileFtpFile) dest).file;
                if (destFile.exists()) {
                    retVal = false;
                } else {
                    retVal = this.file.renameTo(destFile);
                }
            }else retVal = false;
        }

        return retVal;
    }

    @Override
    public List<FtpFile> listFiles() {
        if(!this.file.isDirectory()) {
            return null;
        } else {
            File[] files = this.file.listFiles();
            if(files == null) {
                return null;
            } else {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                });
                String virtualFileStr = this.getAbsolutePath();
                if (virtualFileStr.charAt(virtualFileStr.length() - 1) != 47) {
                    virtualFileStr = virtualFileStr + '/';
                }


                FtpFile[] virtualFiles = new FtpFile[files.length];

                for(int i = 0; i < files.length; ++i) {
                    File fileObj = files[i];
                    String fileName = virtualFileStr + fileObj.getName();
                    virtualFiles[i] = new FileFtpFile(this.userCC,fileName, fileObj);
                }

                return Collections.unmodifiableList(Arrays.asList(virtualFiles));
            }
        }
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        if(!this.isWritable()) {
            throw new IOException("No write permission : " + this.file.getName());
        } else {
            final RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
            raf.setLength(offset);
            raf.seek(offset);
            return new FileOutputStream(raf.getFD()) {
                public void close() throws IOException {
                    super.close();
                    raf.close();
                }
            };
        }
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        if(!this.isReadable()) {
            throw new IOException("No read permission : " + this.file.getName());
        } else {
            final RandomAccessFile raf = new RandomAccessFile(this.file, "r");
            raf.seek(offset);
            return new FileInputStream(raf.getFD()) {
                public void close() throws IOException {
                    super.close();
                    raf.close();
                }
            };
        }
    }

    @Override
    public String getRealPath() {
        return file.getPath();
    }

    @Override
    public String getType() {
        return "File";
    }
}
