package com.wq.tec.frame.clip;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.jazz.libs.controller.BasePresent;

/**
 * Created by N on 2017/3/6.
 */

public class ClipPresenter extends BasePresent<ClipActivity> {

    static Bitmap bitmap = null;
    private Bitmap dstBitmap = null;

    private Canvas mCanvas ;
    private int mCavansColor = Color.parseColor("#9A4876FF");
    private Paint mPaint = new Paint();

    public static void setBitmapResource(Bitmap bitmap){
        ClipPresenter.bitmap = bitmap;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, ClipActivity activity) {
        super.onCreate(savedInstanceState, activity);
//        if(bitmap != null){
//            dstBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        } else {
            dstBitmap = Bitmap.createBitmap(mActivity.getResources().getDisplayMetrics().widthPixels, mActivity.getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
//        }
        mCanvas = new Canvas(dstBitmap);
    }

    Bitmap getDstBitmap(){
        return dstBitmap;
    }

    Canvas getDstCanvas(){
        return mCanvas;
    }

    int getColor(){
        return mCavansColor;
    }

    Paint getClipPaint(){
        return mPaint;
    }

    void submitCanvas(@NonNull ImageView imageView){
        imageView.setImageBitmap(dstBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bitmap != null){
            mActivity.showImage(bitmap);
            mActivity.showGround(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
        bitmap = null;
    }
}
