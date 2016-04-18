package org.kreal.akvideoplayer.decoder;

import java.nio.ByteBuffer;

/**
 * Created by lthee on 2016/3/23.
 */
public class CCMediaDecoder {
    private int Duration;
    private int VideoWidth;
    private int VideoHeight;
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ccmediadecoder");
        ByteBuffer byteBuffer;
        native_init();
    }
    public native static void native_init();
    private native int loadfile(String string);
    public native ByteBuffer getByteBuffer();
    public native int decoderOne();
    private native int native_getVideoWidth();
    private native int native_getVideoHeight();
    private native int native_getDuration();
    private native int native_getCurrentPosition();
    private native void native_seekTo(int i);

    public int setPath(String path) {
        int result = loadfile(path);
        VideoHeight = native_getVideoHeight();
        VideoWidth = native_getVideoWidth();
        Duration = native_getDuration();
        return result;
    }

    public int getVideoWidth(){
        return VideoWidth;
    };
    public int getVideoHeight(){
        return VideoHeight;
    };
    public int getDuration() {
        return Duration;
    }
    public int getCurrentPosition() {
        return native_getCurrentPosition();
    }
    public void seekTo(int i) {
        native_seekTo(i);
    }
}
