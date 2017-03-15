package com.wq.tec.frame.render;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jazz.libs.controller.BasePresent;

/**
 * Created by N on 2017/3/15.
 */

public class RenderPresenter extends BasePresent<RenderActivity> {

    private static Bitmap bitmap = null;
    private Bitmap dstBitmap = null;

    public static void setBitmapResource(Bitmap bitmap){
        RenderPresenter.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return dstBitmap == null? Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565) : dstBitmap;
    }

    void setBitmap(@NonNull Bitmap resource){
        this.dstBitmap = resource.copy(Bitmap.Config.ARGB_8888, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, RenderActivity activity) {
        super.onCreate(savedInstanceState, activity);
        if(RenderPresenter.bitmap != null){
            dstBitmap = RenderPresenter.bitmap.copy(Bitmap.Config.ARGB_8888, false);
            RenderPresenter.bitmap = null;
        }
    }
}
