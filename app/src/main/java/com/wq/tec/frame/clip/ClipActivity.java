package com.wq.tec.frame.clip;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.tech.grabcut.GrabCutController;
import com.wq.tec.tech.grabcut.GrabCutFrame;
import com.wq.tec.tech.grabcut.GrabCutLoader;

/**
 * Created by N on 2017/3/6.
 */

public class ClipActivity extends WQActivity<ClipPresenter> {

    public static final int CLIP_RESULT = 1002;

    ImageView mClipImage ;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clip);

        addFragment(new GrabCutFrame(), R.id.clip_frame);
        mClipImage = (ImageView) findViewById(R.id.clip_image);
    }

    public void action(View v){
        int resId = v.getId();
        switch (resId){
            case R.id.actionback:
                finish();
                break;
            case R.id.actionsure:
                GrabCutController.grabCutImage(new GrabCutLoader.GrabCutCallBack() {
                    @Override
                    public void done(@NonNull Bitmap result) {
                    }
                });
                break;
        }
    }

    public void doControl(View view){
        int resId = view.getId();
        switch (resId){
            case R.id.clip_sel:
                switchViewSelect(resId);
                GrabCutController.setGrabCutModel(GrabCutLoader.GrabCutModel.LOW_PRECISION);
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
}
