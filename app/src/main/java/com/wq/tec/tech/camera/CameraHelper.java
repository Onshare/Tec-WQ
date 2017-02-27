package com.wq.tec.tech.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2017/2/5.
 */
public class CameraHelper {

    private int[] mScreenSize = new int[2];

    CameraHelper(@NonNull Context mContext){
        mScreenSize[0] = mContext.getResources().getDisplayMetrics().widthPixels;
        mScreenSize[1] = mContext.getResources().getDisplayMetrics().heightPixels;
    }

    public static class CameraInfo{
        public int cameraId;
        public int orientation;
    }

    private Camera mCamera;

    private boolean isInit = false;

    void release(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            isInit = false;
        }
    }

    boolean isInit(){
        return isInit;
    }

    boolean openCamera(){
        return openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    boolean openCamera(int cameraId){
        release();
        if(mCamera == null){
            try {
                mCamera = Camera.open(cameraId);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        Camera.Parameters mParameters = mCamera.getParameters();
        if(mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        List<Camera.Size> mPreViewSizeList = mParameters.getSupportedPreviewSizes();
        Camera.Size mPreViewSize = getFixedCameraSize(mPreViewSizeList);
        mParameters.setPreviewSize(mPreViewSize.width, mPreViewSize.height);
        android.util.Log.e("camera preview size :", mPreViewSize.width + " x " + mPreViewSize.height);

        List<Camera.Size> mPicSizeList = mParameters.getSupportedPictureSizes();
        Camera.Size mPicSize = getFixedCameraSize(mPicSizeList);
        mParameters.setPictureSize(mPicSize.width, mPicSize.height);
        android.util.Log.e("camera pic size :", mPicSize.width + " x " + mPicSize.height);
        mParameters.setJpegQuality(100);
        mCamera.setParameters(mParameters);
        return isInit = true;
    }


    Camera.Size getFixedCameraSize(@NonNull List<Camera.Size> mSize){
        double beRatio = (double)mScreenSize[0] / mScreenSize[1];
        double[] result = new double[mSize.size()];
        for(int i = 0; i < mSize.size(); i++){
            Camera.Size s = mSize.get(i);
            result[i] = Math.abs(- beRatio + (double)s.height / s.width) ;
        }
        int index = 0;
        double be = result[index];
        for(int j = 0; j < result.length; j++){
            if(result[j] < be){
                index = j;
                be = result[index];
            }
        }
        return mSize.get(index);
    }

    Camera getCamera(){
        return mCamera;
    }

    void switchFocusMode(@NonNull String mode){
        Camera.Parameters mParameters =  mCamera.getParameters();
        if(isInit && mParameters.getSupportedFocusModes().contains(mode)){
            mCamera.cancelAutoFocus();
            mParameters.setFocusMode(mode);
            mCamera.setParameters(mParameters);
        }
    }

    void cancelAutoFocus(){
        mCamera.cancelAutoFocus();
    }

    void focus(Rect rect, Camera.AutoFocusCallback callback){
        try {
            mCamera.cancelAutoFocus();
            Camera.Parameters mParameters =  mCamera.getParameters();
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            if (mParameters.getMaxNumFocusAreas() > 0 && rect != null) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(rect, 1000));
                mParameters.setFocusAreas(focusAreas);
            }
            mCamera.setParameters(mParameters);
            mCamera.autoFocus(callback);
        } catch (Exception e) {
        }
    }

    double zoom(int level, int max){
        Camera.Parameters mParameters = mCamera.getParameters();
        if(mParameters.isZoomSupported()){
            int maxZoomLevel = mParameters.getMaxZoom();
            if(level <= max){
                int param = level > 0 ? 1 : -1;
                int total = mParameters.getZoom() + param;
                if(total > maxZoomLevel){
                    total = maxZoomLevel;
                }else if(total <= 0){
                    total = 0;
                }
                mParameters.setZoom(total);
                mCamera.setParameters(mParameters);
                return (double) total / maxZoomLevel;
            }
        }
        return 0;
    }

    void takePicture(Camera.PictureCallback jpegCallBack){
        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setRotation(90);
        mCamera.setParameters(mParameters);
        mCamera.takePicture(null, null, jpegCallBack);
    }
}
