package org.kreal.ndktest;

/**
 * Created by lthee on 2017/3/17.
 */

public class test1 {
    static {
        System.loadLibrary("test");
    }
    public native int test();
}
