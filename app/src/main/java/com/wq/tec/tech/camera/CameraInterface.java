package com.wq.tec.tech.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;

/**
 * Created by NoName on 2017/4/5.
 */

public interface CameraInterface {

    /**
     * 初始化camera
     * @param cameraId 当前的camera
     */
    boolean openCamera(int cameraId);

    boolean openCamera();/**重载方法*/

    /**
     * 回显是否初始化
     * @return
     */
    boolean isInit();

    void relativeGL(@NonNull GLSurfaceView mView);

    /**
     * 生成预览画面
     */
    void showCameraPreView(@NonNull Activity mActivity);

    /**
     * 获取camera
     */
    Camera getCamera();

    int getCameraId();

    /**
     * 约定美颜效果
     * @param mFilter
     */
    void setFilter(CameraFilter mFilter);

    /**
     * 缩放倍数
     * @param progress
     */
    double zoom(int progress, int maxLevel);

    /**
     * 拍照
     */
    void takePicture(CameraInterface.CameraTakePictureCallBack takePictureCallBack);

    /**
     * 打开闪光灯
     */
    void takeFlash(boolean isOpen);

    void focus(Rect rect, CameraFocusCallBack mFocusCallBack);

    void switchFocusMode(@NonNull String mode);

    void release();

    public interface CameraTakePictureCallBack{
        public void readyCreateBitmap();
        public void getCameraPicture(Bitmap mBitmap);
    }

    public interface CameraFocusCallBack{
        void onFocus(boolean success, Camera camera);
    }
}
