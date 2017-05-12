package org.kreal.ftpserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


public class ShowFTPserverQR extends Activity {
    private  View mView = null;
    private WindowManager.LayoutParams wmParams;
    static private String QRServer = "QRSERVERADDRESS";
    static public void startQRActivity(Context context,String txt){
        context.startActivity(startQRActivityIntent(context, txt));
    }
    static public Intent startQRActivityIntent(Context context,String txt){
        Intent intent = new Intent(context,org.kreal.ftpserver.ShowFTPserverQR.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(QRServer,txt);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ShowFTPserverQR", "onCreate");
        Intent intent = getIntent();
        if (intent!=null)
            mView = setView(getApplicationContext(),intent.getStringExtra(QRServer));
        else mView = setView(getApplicationContext(),null);
        getWindowManager().addView(mView,wmParams);
    }
    public static Bitmap BitMatrixToBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < width; ++y) {
            for (int x = 0; x < height; ++x) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xffffffff; // black pixel
            }
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ShowFTPserverQR", "onPause");
        deleView();
        finish();
    }
    private void deleView(){
        if( mView != null){
            getWindowManager().removeViewImmediate(mView);
            mView = null;
        }
    }

    private View setView(Context context , String info){
        wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN  ,
                PixelFormat.TRANSPARENT);
        View view = FrameLayout.inflate(context,R.layout.activity_show_ftpserver_qr,null);

        if (info != null)
            try {
                ((ImageView)view.findViewById(R.id.QRimage)).setImageBitmap(BitMatrixToBitmap(new MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE, 800, 800)));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        view.findViewById(R.id.QRimage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("sdf", "onTouch");
                return true;
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                deleView();
                finish();
                return true;
            }
        });
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        deleView();
                        return true;
                    default:
                        return false;
                }
            }
        });
        return view;
    }
}
