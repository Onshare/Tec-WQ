package com.wq.tec.tech.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

    private CameraInterface mCameraHelper ;
    private CameraFrame mFrame;
    private GLSurfaceView mView;

    CameraLoader(@NonNull CameraFrame frame, @NonNull CameraInterface helper){
        this.mCameraHelper = helper;
        mFrame = frame;
    }

    void relativeGL(@NonNull GLSurfaceView mView){
        mCameraHelper.relativeGL(CameraLoader.this.mView = mView);
    }

    void onResume(){
        if(mCameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)){
            showCameraPreView();
        }
    }

    void onPause(){
        mCameraHelper.release();
    }

    void startCamera(){
        switchCamera(mCameraHelper.getCameraId());
        mFrame.showResult(null);
    }

    void takePic(final TakePicCallBack takePicCallBack){
        if(!mCameraHelper.isInit()){
            return ;
        }
        mCameraHelper.takePicture(new CameraInterface.CameraTakePictureCallBack() {
            @Override
            public void readyCreateBitmap() {
                if(CameraLoader.this.mView != null){
                    CameraLoader.this.mView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                }
            }

            @Override
            public void getCameraPicture(Bitmap mBitmap) {
                mFrame.showResult(mBitmap);
                if(takePicCallBack != null){
                    takePicCallBack.takePic(mBitmap);
                }
            }
        });
    }

    private File createCameraSaveFile() throws IOException{
        return new File(Environment.getExternalStorageDirectory()+"/IME_Temp.jpg");
    }

    void switchCamera(int cameraId){
        if(mCameraHelper.openCamera(cameraId)){
            showCameraPreView();
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

    void setFilter(@NonNull CameraFilter mFilter){
        this.mCameraHelper.setFilter(mFilter);
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

    private void showCameraPreView(){
        mCameraHelper.showCameraPreView(mFrame.getActivity());
    }


    void release(){
        mCameraHelper = null;
        mFrame = null;
        mView = null;
    }

    public interface ZoomLevelCallBack{
        public void zoom(double level);
    }

    public interface TakePicCallBack{
        public void takePic(Bitmap bitmap);
    }
}
