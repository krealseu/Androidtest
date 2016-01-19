package org.kreal.viewtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by lthee on 2016/1/4.
 */
public class ViewGestureListener extends GestureDetector.SimpleOnGestureListener{
    private Handler handler=null;
    private static int ViedoControlCode=0x0E;
    private static String DoubleTap="DOUBLETAP";
    private static String SingleTap="SINGLETAP";
    private static String Scroll="scroll";
    private static String Event="Event";

    public ViewGestureListener() {
    }
    public ViewGestureListener(Handler h) {
        this.handler=h;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(handler!=null) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString(Event, SingleTap);
            msg.setData(bundle);
            msg.what=ViedoControlCode;
            handler.sendMessage(msg);
            return true;
        }
        else return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if(e.getAction()==MotionEvent.ACTION_UP && handler!=null) {
            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString(Event,DoubleTap);
            msg.setData(bundle);
            msg.what=ViedoControlCode;
            handler.sendMessage(msg);
            return true;
        }
        else return false;
    }

}
