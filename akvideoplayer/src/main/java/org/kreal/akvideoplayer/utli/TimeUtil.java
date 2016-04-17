package org.kreal.akvideoplayer.utli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lthee on 2016/1/26.
 */
public class TimeUtil {
    public static final String getTimeIntToString(int time){
        DateFormat dateFormat=new SimpleDateFormat("mm:ss");
        String stime=dateFormat.format(new Date(time));
        if(time>3600*1000)
            stime=time/3600/1000+":"+stime;
        return stime;
    }

}
