package com.wq.tec.frame.clip;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.open.BlackGroundView;
import com.wq.tec.tech.grabcut.GrabCutController;
import com.wq.tec.tech.grabcut.GrabCutFrame;
import com.wq.tec.tech.grabcut.GrabCutLoader;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;

/**
 * Created by N on 2017/3/6.
 */

public class ClipActivity extends WQActivity<ClipPresenter> {

    public static final int CLIP_RESULT = 1002;

    BlackGroundView mGround;
    ImageView mClipImage ;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clip);

        addFragment(new GrabCutFrame(), R.id.clip_frame);
        mClipImage = (ImageView) findViewById(R.id.clip_image);
        mGround = (BlackGroundView)findViewById(R.id.clip_ground);
    }

    public void action(View v){
        int resId = v.getId();
        switch (resId){
            case R.id.actionback:
                finish();
                break;
            case R.id.actionsure:
                break;
        }
    }

    public void doControl(View view){
        int resId = view.getId();
        switch (resId){
            case R.id.clip_sel:
                switchViewSelect(resId);
                GrabCutController.setGrabCutModel(GrabCutLoader.GrabCutModel.LOW_PRECISION);
                findViewById(R.id.clip_clip).setVisibility(View.VISIBLE);
                break;
            case R.id.clip_add:
                switchViewSelect(resId);
                break;
            case R.id.clip_move:
                switchViewSelect(resId);
                break;
            case R.id.clip_del:
                switchViewSelect(resId);
                break;
            case R.id.clip_redo:
                break;
            case R.id.clip_undo:
                break;
        }
    }

    public void startClip(View v){
        if(R.id.clip_clip == v.getId()){
            findViewById(R.id.clip_clip).setVisibility(View.GONE);
            GrabCutController.grabCutImage(new GrabCutLoader.GrabCutCallBack() {
                @Override
                public void done(@NonNull Bitmap result) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GrabCutController.releaseImage();
    }

    void switchViewSelect(int resId){
        findViewById(R.id.clip_sel).setSelected(resId == R.id.clip_sel);
        findViewById(R.id.clip_add).setSelected(resId == R.id.clip_add);
        findViewById(R.id.clip_move).setSelected(resId == R.id.clip_move);
        findViewById(R.id.clip_del).setSelected(resId == R.id.clip_del);
    }

    void showImage(@NonNull  Bitmap bitmap){
        GrabCutController.showImage(bitmap);
    }

    void showGround(@NonNull Bitmap bitmap){
        GPUImage gpuImage = new GPUImage(this);
        gpuImage.setFilter(new GPUImageGaussianBlurFilter(5F));
        Bitmap overBitmap = gpuImage.getBitmapWithFilterApplied(bitmap);
        mGround.setImageBitmap(overBitmap);
    }
}
