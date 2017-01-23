package org.kreal.ftpserver.virtualfilesystem;

import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by lthee on 2016/10/17.
 */
public class FtpFileHelper {
    public static FtpFileCC create(UserCC user,String ftppath){
        List<String> homelist = user.homeDirMap.getKeyList();
        String trueHome = null;
        for (String home : homelist){
            if (ftppath.startsWith(home)){
                trueHome = home;
                break;
            }
        }
        if (trueHome == null) {
            return null;
        }
        String paths = normalizedPath( File.separator +ftppath.replaceFirst(trueHome,user.homeDirMap.get(trueHome)));
        if (ftppath.startsWith("/SD"))
            return new TreeFtpFile2(user,ftppath,new File(paths),user.context.getContentResolver().getPersistedUriPermissions().get(0).getUri());
        else return new FileFtpFile(user,ftppath,new File(paths));
    }

    public static boolean isDirectory(UserCC user,String filename){
        if (filename == "/")
            return true;
        FtpFileCC ftpFileCC = create(user,filename);
        if (ftpFileCC == null)
            return false;
        return ftpFileCC.isDirectory();
    }

    public static String normalizedPath(String path){
        int len = path.length();
        int i,p,q;
        i=p=q=0;
        Stack<String> stack = new Stack<>();
        while (i<len){
            while (path.charAt(i) != 47){
                i++;
                if (i>=len)
                    break;
            }
            q=i;
            if ((q-p)<=1){
                p=q;
            }
            else {
                String temp = path.substring(p+1,q);
                if (temp.length()==1&&temp.charAt(0)=='.');
                else if (temp.length()==2&&temp.charAt(0)=='.'&&temp.charAt(1)=='.'){
                    if (!stack.isEmpty()){
                        stack.pop();
                    }
                }
                else stack.push(temp);
                p=q;
            }
            i++;
        }
        String result = "";
        for (String tmp:stack){
            result=result+"/"+tmp;
        }
        if (result.length() == 0)
            result = "/";
        return result;
    }

    public static List<String> getpath(String path) {
        List<String> paths = new ArrayList<>();
        int len = path.length();
        int i, p, q;
        for (i = p = q = 0; i < len; i++) {
            while (i < len) {
                if (path.charAt(i) == 47)
                    break;
                i++;
            }
            q = i;
            if (!(q - p <= 1))
                paths.add(path.substring(p + 1, q));
            p = q;

        }
        return paths;
    }

}
