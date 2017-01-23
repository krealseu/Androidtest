package org.kreal.ftpserver.virtualfilesystem;

import android.net.Uri;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.usermanager.impl.WriteRequest;
import org.kreal.ftpserver.Util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
public class TreeFtpFile2 implements FtpFileCC {
    private static final String TAG = FileFtpFile.class.getSimpleName();
    private UserCC userCC = null;
    private File file = null;
    private String ftppath = null;
    private Uri treeRoot = null;

    public TreeFtpFile2(UserCC user, String ftppath, java.io.File file, Uri treeRoot) {
        this.userCC = user;
        this.ftppath = ftppath;
        this.file = file;
        this.treeRoot = treeRoot;
    }

    @Override
    public String getAbsolutePath() {
        String fullName = this.ftppath;
        int filelen = fullName.length();
        if (filelen != 1 && fullName.charAt(filelen - 1) == 47) {
            fullName = fullName.substring(0, filelen - 1);
        }
        return fullName;
    }

    @Override
    public String getName() {
        if (this.ftppath.equals("/")) {
            return "/";
        } else {
            String shortName = this.ftppath;
            int filelen = this.ftppath.length();
            if (shortName.charAt(filelen - 1) == 47) {
                shortName = shortName.substring(0, filelen - 1);
            }

            int slashIndex = shortName.lastIndexOf(47);
            if (slashIndex != -1) {
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
        if (this.userCC.user.authorize(new WriteRequest(this.getAbsolutePath())) == null) {
            return false;
        } else {
            if (this.file.exists()) {
//                return this.file.canWrite();
                return true;
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
        return this.file.isDirectory() ? 3 : 1;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public boolean setLastModified(long l) {
        return false;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public boolean mkdir() {
        String filepaths = this.file.getAbsolutePath();
        String treehome = FileUtil.getFullPathFromTreeUri(this.treeRoot, this.userCC.context);
        filepaths = filepaths.replace(treehome, "/");
        if (filepaths == "/")
            return true;
        List<String> paths = FtpFileHelper.getpath(filepaths);
        DocumentFile document = DocumentFile.fromTreeUri(this.userCC.context, this.treeRoot);
        int i, len;
        len = paths.size();
        for (i = 0; i < len; i++) {
            DocumentFile childtree = document.findFile(paths.get(i));
            if (childtree == null) {
                childtree = document.createDirectory(paths.get(i));
                if (childtree == null)
                    return false;
            }
            document = childtree;
        }
        return true;
    }

    @Override
    public boolean delete() {
        boolean retVal = false;
        if (this.isRemovable()) {
            String filepaths = this.file.getAbsolutePath();
            String treehome = FileUtil.getFullPathFromTreeUri(this.treeRoot, this.userCC.context);
            filepaths = filepaths.replace(treehome, "/");
            if (filepaths == "/")
                return true;
            List<String> paths = FtpFileHelper.getpath(filepaths);
            DocumentFile document = DocumentFile.fromTreeUri(this.userCC.context, this.treeRoot);
            int i, len;
            len = paths.size();
            for (i = 0; i < len; i++) {
                DocumentFile childtree = document.findFile(paths.get(i));
                if (childtree == null)
                    return false;
                document = childtree;
            }
            retVal = document.delete();
        }

        return retVal;
    }

    @Override
    public boolean move(FtpFile dest) {
        boolean retVal = false;
        if (dest.isWritable() && this.isReadable()) {
            if (((FtpFileCC) dest).getType() == "Tree2") {
                String destpath = ((FtpFileCC) dest).getRealPath();
                String sorpath = this.getRealPath();
                if (sorpath.substring(0, sorpath.lastIndexOf('/')).equals(destpath.substring(0, destpath.lastIndexOf('/')))) {
                    if (dest.doesExist())
                        retVal = false;
                    else {
                        String filepaths = this.file.getAbsolutePath();
                        String treehome = FileUtil.getFullPathFromTreeUri(this.treeRoot, this.userCC.context);
                        filepaths = filepaths.replace(treehome, "/");
                        if (filepaths == "/")
                            return true;
                        List<String> paths = FtpFileHelper.getpath(filepaths);
                        DocumentFile document = DocumentFile.fromTreeUri(this.userCC.context, this.treeRoot);
                        int i, len;
                        len = paths.size();
                        for (i = 0; i < len; i++) {
                            DocumentFile childtree = document.findFile(paths.get(i));
                            if (childtree == null)
                                return false;
                            document = childtree;
                        }
                        retVal = document.renameTo(dest.getName());
                    }
                }
            } else retVal = false;
        }

        return retVal;
    }

    @Override
    public List<FtpFile> listFiles() {
        if (!this.file.isDirectory()) {
            return null;
        } else {
            File[] files = this.file.listFiles();
            if (files == null) {
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

                for (int i = 0; i < files.length; ++i) {
                    File fileObj = files[i];
                    String fileName = virtualFileStr + fileObj.getName();
                    virtualFiles[i] = new TreeFtpFile2(this.userCC, fileName, fileObj, this.treeRoot);
                }

                return Collections.unmodifiableList(Arrays.asList(virtualFiles));
            }
        }
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        if (!this.isWritable()) {
            throw new IOException("No write permission : " + this.file.getName());
        } else {
            String filepaths = this.file.getAbsolutePath();
            String treehome = FileUtil.getFullPathFromTreeUri(this.treeRoot, this.userCC.context);
            filepaths = filepaths.replace(treehome, "/");
            if (filepaths == "/")
                return null;
            List<String> paths = FtpFileHelper.getpath(filepaths);
            DocumentFile document = DocumentFile.fromTreeUri(this.userCC.context, this.treeRoot);
            int i, len;
            len = paths.size();
            for (i = 0; i < len; i++) {
                DocumentFile childtree = document.findFile(paths.get(i));
                if (childtree == null) {
                    if (i == len - 1) {
//                        Log.i(TAG, "createfile:"+getName());
                        childtree = document.createFile("", this.getName());
                        if (childtree == null)
                            return null;
                    } else {
                        childtree = document.createDirectory(paths.get(i));
                        if (childtree == null)
                            return null;
                    }
                }
                document = childtree;
            }

            FileOutputStream out = new FileOutputStream(userCC.context.getContentResolver().openFileDescriptor(document.getUri(), "rw").getFileDescriptor());
            out.getChannel().position(offset);
            return out;
        }
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        if (!this.isReadable()) {
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
        return "Tree2";
    }
}