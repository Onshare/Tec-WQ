package com.wq.tec.frame.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.termination.TerminationTask;
import com.jazz.libs.util.ThreadUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.frame.guid.GuidActivity;
import com.wq.tec.tech.camera.CameraController;
import com.wq.tec.tech.camera.CameraLoader;
import com.wq.tec.util.FileCacheUtil;

/**
 * Created by NoName on 2017/1/19.
 */

public class CameraActivity extends WQActivity {

    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisk(true).build();

    @Override
    protected boolean isSupportCheckPermission() {
        return true;
    }

    private int cameraId = 0;
    private boolean isOpenFlash = false;

    private ImageView mCameraShow;
    private ImageView mCameraCover;
    private String[] covers = new String[3];
    private int mCoverModel = -1;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        addCameraFrame();
        mCameraShow = (ImageView) findViewById(R.id.camera_show);
        mCameraCover = (ImageView) findViewById(R.id.camera_cover);
        covers[0] = getIntent().getStringExtra("URL_CLIP");
        covers[1] = getIntent().getStringExtra("URL_ORIGIN");
        covers[2] = getIntent().getStringExtra("URL_BACKGROUND");
        if(TextUtils.isEmpty(covers[0]) || TextUtils.isEmpty(covers[1])){
            mCoverModel = -1;
        }else{
            mCoverModel = 1;
        }
    }

    void addCameraFrame(){
        addFragment(new com.wq.tec.tech.camera.CameraFrame(), R.id.camera_frame);
    }


    public void doAction(View v){
        switch (v.getId()){
            case R.id.cameraback:
                this.finish();
                break;
            case R.id.cameraswitch:
                CameraController.switchCamera( (++cameraId) % 2);
                break;
            case R.id.cameralight:
                CameraController.openFlash(isOpenFlash = !isOpenFlash);
                findViewById(R.id.cameralight).setSelected(isOpenFlash);
                break;
        }
    }

    public void doTackePic(View v){
        if(R.id.camera_take == v.getId()){
            CameraController.takePic(new CameraLoader.TakePicCallBack() {
                @Override
                public void takePic(@NonNull Bitmap bitmap) {
                    mCameraShow.setImageBitmap(bitmap);
                }
            });
        }
    }

    public void doControl(View v){
        if(R.id.camera_filter == v.getId()){

        }else if(R.id.camera_guid == v.getId()){
            startActivity(new Intent(this, GuidActivity.class));
            this.finish();
        }
    }

    public void doCover(View v){
        if(R.id.camera_conver_mode == v.getId()){
            mCoverModel++;
            mCoverModel = mCoverModel > 3 ? 1 : mCoverModel;
        }else if(R.id.camera_cover_close == v.getId()){//关闭
            mCoverModel = 0;
        }
        setMode(mCoverModel);
        FadeInBitmapDisplayer.animate(mCameraCover, 500);
    }

    private void setMode(int mode){
        if(mode == 0 || mode == -1){
            mCoverModel = 3;
            findViewById(R.id.camera_cover_close).setVisibility(View.GONE);
            findViewById(R.id.camera_conver_mode).setVisibility(mode == -1 ? View.GONE : View.VISIBLE);
            setMode(mCoverModel);
        }else if(mode == 1 && covers[0] != null){
            ImageManager.get().displayImage(covers[0], mCameraCover, mOptions);
            ((TextView)findViewById(R.id.camera_conver_mode)).setText("原图");
        }else if(mode == 2 && covers[1] != null){
            ImageManager.get().displayImage(covers[1], mCameraCover, mOptions);
            ((TextView)findViewById(R.id.camera_conver_mode)).setText("隐藏");
        }else if(mode == 3 && covers[2] != null){
            mCameraCover.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            ((TextView)findViewById(R.id.camera_conver_mode)).setText("指导");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraId = 0;
        isOpenFlash = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMode(mCoverModel);
        mCursorLoadPic();
    }

    private void mCursorLoadPic(){
        FileCacheUtil.mCursorPhotoAtFirst(this, new TerminationTask<String>() {
            @Override
            public void onTermination(String tag, String object) {
                showMediaImageIcon(object);
            }

            @Override
            public void onTerminationfailure(String tag, byte[] content, Throwable e) {}
        });
    }

    private void showMediaImageIcon(final String imgPath){
        ThreadUtils.ruuOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageManager.get().displayImage("file://"+imgPath, mCameraShow);
            }
        });
    }
}
