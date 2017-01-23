package com.example.kreal.sdf;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;

public class ServiceIbinderTest extends Service {
    static String TAG = ServiceIbinderTest.class.getSimpleName();
    private mBinder mBind= new mBinder();
    public ServiceIbinderTest() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBind;
        //throw new UnsupportedOperationException("Not yet implemented");
    }


    class mBinder extends Binder{
        public String getnum(){
            return "dsf1";
        }
    }
}
