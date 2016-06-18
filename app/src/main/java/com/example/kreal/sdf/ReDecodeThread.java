package com.example.kreal.sdf;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

/**
 * Created by lthee on 2016/6/17.
 */
public class ReDecodeThread {

    public static void encode(final Bitmap bitmap, final ReDecodeThreadResult listener) {

        if (listener == null) {
            return;
        }

        if (bitmap == null) {
            listener.onReDecodeResult(null);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    MultiFormatReader multiFormatReader = new MultiFormatReader();
                    BitmapLuminanceSource source = new BitmapLuminanceSource(bitmap);
                    BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                    Result result1 = multiFormatReader.decode(bitmap1);
                    listener.onReDecodeResult(result1.getText());
                    return;
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
                listener.onReDecodeResult(null);
            }
        }.start();

    }

    public interface ReDecodeThreadResult {
        void onReDecodeResult(String url);
    }
}