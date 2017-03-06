package com.wq.tec.frame.clip;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.jazz.libs.controller.BasePresent;

/**
 * Created by N on 2017/3/6.
 */

public class ClipPresenter extends BasePresent<ClipActivity> {

    static Bitmap bitmap = null;

    public static void setBitmapResource(Bitmap bitmap){
        ClipPresenter.bitmap = bitmap;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, ClipActivity activity) {
        super.onCreate(savedInstanceState, activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bitmap != null){
            mActivity.showImage(bitmap);
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
