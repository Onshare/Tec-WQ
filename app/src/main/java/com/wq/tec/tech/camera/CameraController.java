package com.wq.tec.tech.camera;

import android.view.MotionEvent;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by N on 2017/2/6.
 */
public final class CameraController {

    private static CameraLoader mLoader;

    static void setCameraLoader(CameraLoader loader){
        CameraController.mLoader = loader;
    }

    public static void takePic(CameraLoader.TakePicCallBack takePicCallBack){
        if(CameraController.mLoader != null){
            CameraController.mLoader.takePic(takePicCallBack);
        }
    }

    public static void switchCamera(int cameraId){
        if(CameraController.mLoader != null){
            CameraController.mLoader.switchCamera(cameraId);
        }
    }

    public static void stopCamera(){
        if(CameraController.mLoader != null){
            CameraController.mLoader.stopCamera();
        }
    }

    public static void setFrameFilter(GPUImageFilter mFilter){
        if(CameraController.mLoader != null){
            CameraController.mLoader.setFilter(mFilter);
        }
    }

    public static void zoom(MotionEvent event, CameraLoader.ZoomLevelCallBack callBack){
        if(CameraController.mLoader != null){
            CameraController.mLoader.zoom(event, callBack);
        }
    }

    public static void focus(int x, int y){
        if(CameraController.mLoader != null){
            CameraController.mLoader.focus(x, y);
        }
    }

    public static void autoFocus(){
        if(CameraController.mLoader != null){
            CameraController.mLoader.autoFocus();
        }
    }
}
