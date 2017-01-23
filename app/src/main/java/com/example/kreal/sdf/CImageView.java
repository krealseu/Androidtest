package com.example.kreal.sdf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lthee on 2016/9/4.
 */
public class CImageView extends ImageView {
    public CImageView(Context context) {
        super(context);
    }

    public CImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    Matrix matrix = new Matrix();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        matrix.setRotate(89);
        setImageMatrix(matrix);
    }
}
