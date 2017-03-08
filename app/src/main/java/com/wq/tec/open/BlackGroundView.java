package com.wq.tec.open;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by N on 2017/3/7.
 */

public class BlackGroundView extends ImageView {

    public BlackGroundView(Context context) {
        super(context);
        initView();
    }

    public BlackGroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BlackGroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView(){
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#A6000000"));
    }
}
