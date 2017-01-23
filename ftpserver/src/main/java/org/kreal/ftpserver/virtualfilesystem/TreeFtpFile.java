package org.kreal.ftpserver.virtualfilesystem;

import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lthee on 2016/10/20.
 */
public class TreeFtpFile implements FtpFileCC {
    private static final String TAG = TreeFtpFile.class.getSimpleName();
    private UserCC userCC = null;
    private DocumentFile file = null;
    private String ftppath = null;
    private boolean newfile = false;

    public TreeFtpFile(UserCC userCC ,String ftppath ,DocumentFile file,boolean newfile) {
        this.ftppath = ftppath;
        this.userCC = userCC;
        this.file = file;
        this.newfile = newfile;
    }

    @Override
    public String getRealPath() {
        String path = file.getUri().getPath();
        if (this.newfile)
            path = path + "/" +this.getName();
        return path;
    }

    @Override
    public String getType() {
        return "Tree";
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
        return !doesExist();
    }

    @Override
    public boolean isDirectory() {
        boolean retVal = false ;
        if (!newfile)
            retVal = this.file.isDirectory();
        else retVal = false;
        return retVal;
    }

    @Override
    public boolean isFile() {
        boolean retVal = false ;
        if (!newfile)
            retVal = this.file.isFile();
        else retVal = false;
        return retVal;
    }

    @Override
    public boolean doesExist() {
        if (newfile)
            return false;
        else return this.file.exists();
    }

    @Override
    public boolean isReadable() {
        if (newfile)
            return true;
        else return this.file.canRead();
    }

    @Override
    public boolean isWritable() {
        if(this.userCC.user.authorize(new WriteRequest(this.getAbsolutePath())) == null) {
            return false;
        } else {
            if (this.newfile)
                return true;
            else if(this.file.exists()) {
                return this.file.canWrite();
            } else {
                return true;
            }
        }
    }

    @Override
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
        return isDirectory()?3:1;
    }

    @Override
    public long getLastModified() {
        if (newfile)
            return 0;
        else return this.file.lastModified();
    }

    @Override
    public boolean setLastModified(long l) {
        return false;
    }

    @Override
    public long getSize() {
        if (newfile)
            return 0;
        else return this.file.length();
    }

    @Override
    public boolean mkdir() {
        boolean retVal = false;
        String dirname = this.getName();
        if (newfile){
            retVal = true;
            for (DocumentFile doc : this.file.listFiles()) {
                if (dirname.equals(doc.getName())) {
                    if (doc.isDirectory()) {
                        retVal = false;
                    }
                }
            }
            if (retVal) {
                DocumentFile tmp = this.file.createDirectory(dirname);
                if (tmp == null)
                    retVal = false;
                else {
                    this.file = tmp;
                    this.newfile = false;
                    retVal = true;
                }
            }
        }
        else retVal = false;
        return retVal;
    }

    @Override
    public boolean delete() {
        if (newfile)
            return true;
        else return this.file.delete();
    }

    @Override
    public boolean move(FtpFile dest) {
        if (newfile)
            return false;
        boolean retVal = false;
        if(dest.isWritable() && this.isReadable()) {
            if (((FtpFileCC)dest).getType() == "Tree") {
                String destpath = ((FtpFileCC)dest).getRealPath();
                String sorpath = this.getRealPath();
                if (sorpath.substring(0,sorpath.lastIndexOf('/')).equals(destpath.substring(0,destpath.lastIndexOf('/')))){
                    if (dest.doesExist())
                        retVal = false;
                    else {
                        retVal = this.file.renameTo(dest.getName());
                    }
                } else {
                    Log.i(TAG,destpath + sorpath);
//                    try {
//                        FileChannel sorchannel = this.getChannel().position(0);
//                        FileChannel destchannel = ((TreeFtpFile) dest).getChannel().position(0);
////                        destchannel.write(ByteBuffer.wrap("sdfads".getBytes()));
//                        Log.i(TAG,"move fail12");
//                        ByteBuffer buffer = ByteBuffer.allocate(4096);
//                        while (sorchannel.read(buffer) != -1) {
//                            buffer.flip();
//                            destchannel.write(buffer);
//                            Log.i(TAG,"move faifffl12");
//                            buffer.clear();
//                        }
////                        destchannel.transferFrom(sorchannel, 0, sorchannel.size());
//                        Log.i(TAG,"move fail12");
////                        sorchannel.transferTo(0,sorchannel.size(),destchannel);
//                        Log.i(TAG,"move fail13");
//                        sorchannel.close();
//                        destchannel.close();
//                        if (dest.getSize() == this.getSize())
//                            this.delete();
//                        else dest.delete();
//                        retVal = true;
//                        Log.i(TAG,"move fail1");
//                    }catch (IOException e){
//                        Log.i(TAG,"move fail");
//                        retVal = false;
//                        e.printStackTrace();
//                    }
                    final FtpFile ddd= dest;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InputStream inputStream = createInputStream(0);
                                OutputStream outputStream = ddd.createOutputStream(0);
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                                byte[] bytes = new byte[1024*1024*5];
                                int byteread;
                                while ((byteread =bufferedInputStream.read(bytes)) != -1){
                                    bufferedOutputStream.write(bytes,0,byteread);
                                }
                                bufferedOutputStream.flush();
                                if (ddd.getSize() == getSize())
                                    delete();
                                else ddd.delete();
//                                retVal = true;
                            } catch (IOException e) {
//                                retVal = false;
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    retVal = true;
//                    try {
//                        InputStream inputStream = this.createInputStream(0);
//                        OutputStream outputStream = dest.createOutputStream(0);
//                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
//                        byte[] bytes = new byte[1024*1024*5];
//                        int byteread;
//                        while ((byteread =bufferedInputStream.read(bytes)) != -1){
//                            bufferedOutputStream.write(bytes,0,byteread);
//                        }
//                        bufferedOutputStream.flush();
//                        if (dest.getSize() == this.getSize())
//                            this.delete();
//                        else dest.delete();
//                        retVal = true;
//                    } catch (IOException e) {
//                        retVal = false;
//                        e.printStackTrace();
//                    }

                }
            }
        }

        return retVal;
    }

    @Override
    public List<FtpFile> listFiles() {
        if (newfile)
            return null;
        if(!this.file.isDirectory()) {
            return null;
        } else {
//            Log.i(TAG,"list1");
            DocumentFile[] files = this.file.listFiles();
            if(files == null) {
                return null;
            } else {
//                Log.i(TAG,"list2");
                Arrays.sort(files, new Comparator<DocumentFile>() {
                    @Override
                    public int compare(DocumentFile f1, DocumentFile f2) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                });
                String virtualFileStr = this.getAbsolutePath();
                if (virtualFileStr.charAt(virtualFileStr.length() - 1) != 47) {
                    virtualFileStr = virtualFileStr + '/';
                }
//                Log.i(TAG,"list3");
                FtpFile[] virtualFiles = new FtpFile[files.length];

                for(int i = 0; i < files.length; ++i) {
                    DocumentFile fileObj = files[i];
                    String fileName = virtualFileStr + fileObj.getName();
                    virtualFiles[i] = new TreeFtpFile(this.userCC,fileName, fileObj ,false);
                }
//                Log.i(TAG,"list4");

                return Collections.unmodifiableList(Arrays.asList(virtualFiles));
            }
        }
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        if (newfile){
            DocumentFile tmp =this.file.createFile("",this.getName());
            if (tmp !=null){
                this.file = tmp;
                newfile = false;
            }
        }
        if(!this.isWritable()) {
            throw new IOException("No write permission : " + this.file.getName());
        } else {
            Log.i(TAG,"canmoveaa"+offset);
            OutputStream outputStream = userCC.context.getContentResolver().openOutputStream(this.file.getUri());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            InputStream inputStream = userCC.context.getContentResolver().openInputStream(this.file.getUri());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int len = 1024*1024;
            byte[] bytes = new byte[len];
            while (offset>len){
                int ll = bufferedInputStream.read(bytes,0,len);
                if (ll==-1) {
                    continue;
                }
                offset = offset - ll;
                bufferedOutputStream.write(bytes, 0, ll);
            }
            while (offset > 0){
                int ll = bufferedInputStream.read(bytes,0,(int)offset);
                if (ll ==-1) {
                    continue;
                }
                offset = offset - ll;
                 bufferedOutputStream.write(bytes, 0, ll);
            }
            bufferedOutputStream.flush();
            bufferedInputStream.close();
            Log.i(TAG,"canmove ok"+offset);
            return outputStream;
        }
    }

    @Override
    public InputStream createInputStream(long l) throws IOException {
        if(!this.isReadable()) {
            throw new IOException("No read permission : " + this.file.getName());
        } else {
            InputStream inputStream = userCC.context.getContentResolver().openInputStream(this.file.getUri());
            inputStream.skip(l);
            return inputStream;
        }
    }

    private FileChannel getChannel() throws FileNotFoundException {
        if (newfile){
            DocumentFile tmp =this.file.createFile("",this.getName());
            if (tmp !=null){
                this.file = tmp;
                newfile = false;
            }
        }
        Log.i(TAG,this.file.getUri().getPath()+this.file.canWrite());
        FileInputStream inputStream = new FileInputStream(userCC.context.getContentResolver().openFileDescriptor(this.file.getUri(), "rw").getFileDescriptor());
        return inputStream.getChannel();
    }
}
