package com.wq.tec.tech.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.wq.tec.filter.ImageSkinWhitenFilter;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static com.wq.tec.tech.camera.CameraController.stopCamera;

/**
 * Created by N on 2017/2/5.
 */
public class CameraHelper implements CameraInterface{

    private int[] mScreenSize = new int[2];

    CameraHelper(@NonNull Context mContext){
        mScreenSize[0] = mContext.getResources().getDisplayMetrics().widthPixels;
        mScreenSize[1] = mContext.getResources().getDisplayMetrics().heightPixels;
        mCamerId = Camera.CameraInfo.CAMERA_FACING_BACK;
        gpu = new GPUImage(mContext);
    }

    public static class CameraInfo{
        public int cameraId;
        public int orientation;
    }

    private Camera mCamera;
    private int mCamerId;

    private boolean isInit = false;

    private GPUImage gpu;
    private GPUImageFilter mFilter;

    @Override
    public void release(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            isInit = false;
            mCamerId = Camera.CameraInfo.CAMERA_FACING_BACK;
            mFilter = null;
        }
    }

    @Override
    public void relativeGL(@NonNull GLSurfaceView mView){
        gpu.setGLSurfaceView(mView);
    }

    @Override
    public boolean isInit(){
        return isInit;
    }

    @Override
    public boolean openCamera(){
        return openCamera(mCamerId);
    }

    @Override
    public boolean openCamera(int cameraId){
        release();
        if(mCamera == null){
            try {
                mCamera = Camera.open(cameraId);
            } catch (Exception e) {
                return false;
            }
            mCamerId = cameraId;
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

    @Override
    public void showCameraPreView(@NonNull Activity mActivity){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(getCameraId(), info);

        int degrees = getDisPlayRotation(mActivity);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        gpu.setUpCamera(getCamera(), result, info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT, false);
    }

    private int getDisPlayRotation(@NonNull Activity mActivity){
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    private Camera.Size getFixedCameraSize(@NonNull List<Camera.Size> mSize){
        double beRatio = (double)mScreenSize[0] / mScreenSize[1];
        double[] result = new double[mSize.size()];
        for(int i = 0; i < mSize.size(); i++){
            Camera.Size s = mSize.get(i);
            result[i] = Math.abs(- beRatio + (double)s.height / s.width) ;
            if(s.width > mScreenSize[1]){
                result[i] = Integer.MAX_VALUE;
            }
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

    @Override
    public  Camera getCamera(){
        return mCamera;
    }

    @Override
    public int getCameraId() {
        return mCamerId;
    }

    @Override
    public void setFilter(@NonNull CameraFilter mFilter){
        switch (mFilter){
            case WITHEN:
                this.mFilter = new ImageSkinWhitenFilter(mFilter.getLevel() == 0 ? new int[]{11, 19} : mScreenSize);
                break;
            default:
                this.mFilter = null;
                break;
        }
        try {
            gpu.setFilter(this.mFilter);
        } catch (Exception e) {
        }
    }

    @Override
    public void switchFocusMode(@NonNull String mode){
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

    @Override
    public void focus(Rect rect, CameraFocusCallBack mFocusCallBack){
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
            mCamera.autoFocus(null);
        } catch (Exception e) {
        }
    }

    @Override
    public double zoom(int level, int max){
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

    @Override
    public void takePicture(CameraInterface.CameraTakePictureCallBack takePictureCallBack){
        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setRotation(mCamerId == Camera.CameraInfo.CAMERA_FACING_BACK ? 90 : 90);
        mCamera.setParameters(mParameters);
        mCamera.takePicture(null, null, new OriTakePic(takePictureCallBack));
    }

    @Override
    public void takeFlash(boolean isOpen){
        try {
            Camera.Parameters mParameters = mCamera.getParameters();
            mParameters.setFlashMode(isOpen ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
        }
    }

    private class  OriTakePic implements Camera.PictureCallback {

        CameraInterface.CameraTakePictureCallBack cameraTakePictureCallBack;

        OriTakePic(CameraInterface.CameraTakePictureCallBack cameraTakePictureCallBack){
            this.cameraTakePictureCallBack = cameraTakePictureCallBack;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap result = BitmapFactory.decodeByteArray(data, 0, data.length);
                if(cameraTakePictureCallBack != null && result != null){
                    cameraTakePictureCallBack.readyCreateBitmap();
                    result = gpu.getBitmapWithFilterApplied(result);
                    stopCamera();
                    cameraTakePictureCallBack.getCameraPicture(result);
                }
            } catch (Exception e) {
                System.gc();
            }
        }
    };
}
