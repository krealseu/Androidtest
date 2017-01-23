package org.kreal.ftpserver.virtualfilesystem;

import android.content.Context;

import org.apache.ftpserver.ftplet.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lthee on 2016/10/12.
 */
public class UserCC {
    public User user = null;
    public DoubleList<String,String> homeDirMap = new DoubleList<>();
    public Context context = null;

}
