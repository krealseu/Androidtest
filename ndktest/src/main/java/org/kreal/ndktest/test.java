package org.kreal.ndktest;

import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by lthee on 2017/3/17.
 */

public class test {
        static {
            System.loadLibrary("png");
            System.loadLibrary("qweqwe");
        }
    ByteBuffer byteBuffer =ByteBuffer.allocate(100);

    public test(int i) {
        ddd(byteBuffer);
        byte[] bytes = new byte[50];
        //byteBuffer.get(bytes,0,byteBuffer.position());
        Log.i("ad",""+byteBuffer.position());
        byteBuffer.put("fdssfd".getBytes());
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.i("sdss","sdsd");
//        ddd(byteBuffer);
    }
public native void ddd(ByteBuffer eBuffer);
    public native String testq1();
    public native String testq12(int i);
    public native ByteBuffer getss(int i);
}
