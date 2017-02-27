package com.wq.tec.tech.grabcut;

import android.graphics.Bitmap;

/**
 * Created by N on 2017/2/8.
 */
public final class GrabCutController {

    static GrabCutLoader mLoader;

    static void setGrabCutLoader(GrabCutLoader loader){
        GrabCutController.mLoader = loader;
    }

    public static void showImage(Bitmap bitmap){
        if(mLoader != null){
            mLoader.showImage(bitmap);
        }
    }

    public static void setGrabCutModel(GrabCutLoader.GrabCutModel model){
        if(mLoader != null){
            mLoader.setModel(model);
        }
    }

    public static void grabCutImage(GrabCutLoader.GrabCutCallBack callBack){
        if(mLoader != null){
            mLoader.grabCut(callBack);
        }
    }

    public static void releaseImage(){
        if(mLoader != null){
            mLoader.release();
        }
    }
}
