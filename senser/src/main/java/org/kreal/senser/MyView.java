package org.kreal.senser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Kreal on 2015/9/27.
 */
public class MyView extends SurfaceView {
    public MyView(Context context) {
        super(context);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLUE);
        Rect rec=new Rect(1,1,3,4);
//        canvas.clipRect(rec);
//        invalidate();
    }

}
