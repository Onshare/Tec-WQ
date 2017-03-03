package com.wq.tec.tech.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.Surface;

import java.io.File;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by N on 2017/2/5.
 */
public class CameraLoader {

    private CameraHelper mCameraHelper ;
    private CameraFrame mFrame;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private GPUImage gpu;
    private GPUImageFilter mFilter;
    private GLSurfaceView mView;

    CameraLoader(@NonNull CameraFrame frame, @NonNull CameraHelper helper){
        this.mCameraHelper = helper;
        gpu = new GPUImage(frame.getActivity());
        mFrame = frame;
    }

    void relativeGL(@NonNull GLSurfaceView mView){
        gpu.setGLSurfaceView(CameraLoader.this.mView = mView);
    }

    void onResume(){
        if(mCameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)){
            showCameraPreView(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    void onPause(){
        mCameraHelper.release();
    }

    void takePic(final TakePicCallBack takePicCallBack){
        if(!mCameraHelper.isInit()){
            return ;
        }
        mCameraHelper.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    Bitmap result = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if(CameraLoader.this.mFilter != null){
                        if(CameraLoader.this.mView != null){
                            CameraLoader.this.mView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        }
                        result = gpu.getBitmapWithFilterApplied(result);
                    }
                    if(takePicCallBack != null){
                        takePicCallBack.takePic(result);
                    }
                    stopCamera();
                    mFrame.showResult(result);
                } catch (Exception e) {
                    System.gc();
                }
            }
        });
    }

    private File createCameraSaveFile() throws IOException{
        return new File(Environment.getExternalStorageDirectory()+"/IME_Temp.jpg");
    }

    void switchCamera(int cameraId){
        if(mCameraHelper.openCamera(cameraId)){
            showCameraPreView(cameraId);
        }
    }

    void takeFlash(boolean isOpen){
        if(mCameraHelper.isInit()){
            mCameraHelper.takeFlash(isOpen);
        }
    }

    void stopCamera(){
        mCameraHelper.release();
    }

    void setFilter(@NonNull GPUImageFilter mFilter){
        gpu.setFilter(this.mFilter = mFilter);
    }

    /*level <= 5*/
    int olddist;

    void zoom(MotionEvent event, ZoomLevelCallBack mCallBack){
        if(mCameraHelper.isInit()){
            int[] mScreentSize = new int[]{mFrame.getResources().getDisplayMetrics().widthPixels, mFrame.getResources().getDisplayMetrics().heightPixels};
            int maxLevel = ((int)Math.sqrt(mScreentSize[0] * mScreentSize[0] + mScreentSize[1] * mScreentSize[1])) / 2;
            if(event != null && event.getPointerCount() == 2){
                int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_POINTER_DOWN:
                        int dx = (int)event.getX(0) - (int)event.getX(1);
                        int dy = (int)event.getY(0) - (int)event.getY(1);
                        olddist = (int)Math.sqrt(dx * dx + dy * dy);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int sx = (int)event.getX(0) - (int)event.getX(1);
                        int sy = (int)event.getY(0) - (int)event.getY(1);
                        int dist = (int)Math.sqrt(sx * sx + sy * sy);
                        int currentLevel = dist - olddist;
                        if(Math.abs(currentLevel) > 2){
                            olddist = dist;
                            double result = mCameraHelper.zoom(currentLevel, maxLevel);
                            android.util.Log.e("zoom", result+"");
                            if(mCallBack != null){
                                mCallBack.zoom(result);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_POINTER_UP:
                        olddist = 0;
                        break;
                }
            }
        }
    }

    void focus(int x, int y){
        if(mCameraHelper.isInit()){
            Rect rect = calculateTapArea(x, y, 1.0F);
            mCameraHelper.focus(rect, null);
            mFrame.upDataFocusView(x, y);
        }
    }

    void autoFocus(){
        if(mCameraHelper.isInit()){
            mCameraHelper.switchFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int focusAreaSize = (int)(mFrame.getResources().getDisplayMetrics().density * 50F + 0.5F);
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = -1000 + (int)(2000 * x / mFrame.getResources().getDisplayMetrics().widthPixels);
        int centerY = -1000 + (int)(2000 * y / mFrame.getResources().getDisplayMetrics().heightPixels);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(centerX + areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(centerY + areaSize / 2, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void showCameraPreView(int cameraId){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId = cameraId, info);

        int degrees = getDisPlayRotation();
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        gpu.setUpCamera(mCameraHelper.getCamera(), result, info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT, false);
    }

    private int getDisPlayRotation(){
        int rotation = mFrame.getActivity().getWindowManager().getDefaultDisplay().getRotation();
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

    void release(){
        mCameraHelper = null;
        mFrame = null;
        mFilter = null;
        mView = null;
    }

    public interface ZoomLevelCallBack{
        public void zoom(double level);
    }

    public interface TakePicCallBack{
        public void takePic(Bitmap bitmap);
    }
}
