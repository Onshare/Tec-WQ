package com.wq.tec.frame.clip;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.open.BlackGroundView;
import com.wq.tec.open.CanvasView;
import com.wq.tec.tech.grabcut.GrabCutController;
import com.wq.tec.tech.grabcut.GrabCutFrame;
import com.wq.tec.tech.grabcut.GrabCutLoader;
import com.wq.tec.util.Loader;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;

/**
 * Created by N on 2017/3/6.
 */

public class ClipActivity extends WQActivity<ClipPresenter>{

    public static final int CLIP_RESULT = 1002;

    BlackGroundView mGround;
    CanvasView mClipImage ;//展示前景的

    private boolean isIntercept = false;

    private List<Path> mSelectPathList = new ArrayList<>();
    private List<Path> mCancelPathList = new ArrayList<>();

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clip);

        addFragment(new GrabCutFrame(), R.id.clip_frame);
        mClipImage = (CanvasView) findViewById(R.id.clip_image);
        mGround = (BlackGroundView)findViewById(R.id.clip_ground);
        initActionBar();
    }

    void initActionBar(){
        ((TextView)findViewById(R.id.actiontitle)).setText("修饰");
        findViewById(R.id.actionsure).setOnClickListener(actionClick);
        findViewById(R.id.actionback).setOnClickListener(actionClick);
    }

    private View.OnClickListener actionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int resId = v.getId();
            switch (resId){
                case R.id.actionback:
                    finish();
                    break;
                case R.id.actionsure:
                    Bitmap result = mClipImage.createBitmap();
                    if(result != null && ClipPresenter.getBitmap() != null){
                        Bitmap overyLay = mPresent.resetDstBitmap();
                        Canvas canvas = new Canvas(overyLay);
                        Paint mPaint = new Paint();
                        mPaint.setAntiAlias(true);
                        canvas.drawBitmap(result, null, new Rect(0, 0, overyLay.getWidth(), overyLay.getHeight()), mPaint);
                        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(ClipPresenter.getBitmap(), null, new Rect(0, 0, overyLay.getWidth(), overyLay.getHeight()), mPaint);
                        canvas.save();

                        ClipPresenter.setBitmapResource(overyLay);
                        setResult(CLIP_RESULT);
                        result.recycle();
                        ClipActivity.this.finish();
                    }
                    mClipImage.reSet();
                    break;
            }
        }
    };

    public void doControl(View view){
        int resId = view.getId();
        switch (resId){
            case R.id.clip_sel:
                switchViewSelect(resId);
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
                mClipImage.delLast();
                break;
            case R.id.clip_undo:
                mClipImage.addDelLast();
                break;
        }
    }

    public void startClip(View v){
        if(R.id.clip_clip == v.getId()){
            findViewById(R.id.clip_clip).setVisibility(View.GONE);
            GrabCutController.grabCutImage(new GrabCutLoader.GrabCutCallBack() {
                @Override
                public void done(@NonNull Bitmap result) {
                    Loader.stopLoading(100);
                    findViewById(R.id.clip_sel).setSelected(false);
                    showBitmapColor(result);
                }
            });
            Loader.makeLoding(this, 100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GrabCutController.releaseImage();
        mSelectPathList.clear();
        mCancelPathList.clear();
    }

    void switchViewSelect(int resId){
        findViewById(R.id.clip_sel).setSelected(resId == R.id.clip_sel);
        findViewById(R.id.clip_add).setSelected(resId == R.id.clip_add);
        findViewById(R.id.clip_move).setSelected(resId == R.id.clip_move);
        findViewById(R.id.clip_del).setSelected(resId == R.id.clip_del);
        findViewById(R.id.clip_clip).setVisibility(resId == R.id.clip_sel ? View.VISIBLE : View.GONE);
        GrabCutController.setGrabCutModel(resId == R.id.clip_sel ? GrabCutLoader.GrabCutModel.LOW_PRECISION : null);
        if(resId == R.id.clip_add ){
            mClipImage.setModel(CanvasView.Model.ADD);
        }else if(resId == R.id.clip_del){
            mClipImage.setModel(CanvasView.Model.DEL);
        }else{
            mClipImage.setModel(CanvasView.Model.STOP);
        }

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

    //合成
    void showBitmapColor(Bitmap bitmap){
        Canvas dstCanvas = mPresent.getDstCanvas();
        Paint dstPaint = mPresent.getClipPaint();
        dstCanvas.drawBitmap(bitmap, null, new Rect(0, 0, dstCanvas.getWidth(), dstCanvas.getHeight()), dstPaint);
        dstCanvas.save();

        Bitmap overlay = Bitmap.createBitmap(dstCanvas.getWidth(), dstCanvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(overlay);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        cv.drawBitmap(mPresent.getDstBitmap(), null, new Rect(0, 0, cv.getWidth(), cv.getHeight()) , mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setColor(mPresent.getColor());
        cv.drawRect(new Rect(0, 0, cv.getWidth(), cv.getHeight()), mPaint);

        mClipImage.setImageBitmap(overlay);
    }


    int[] point = new int[2];

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(findViewById(R.id.clip_move).isSelected()){
            if(MotionEvent.ACTION_DOWN == event.getAction()){
                point[0] = (int) event.getX();
                point[1] = (int) event.getY();
            }else if(MotionEvent.ACTION_MOVE == event.getAction()){
                findViewById(R.id.clip_body).setTranslationX(event.getX() - point[0]);
                findViewById(R.id.clip_body).setTranslationY(event.getY() - point[1]);
            }else if(MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()){
                point[0] = 0;
                point[1] = 0;
            }
        }
        return super.onTouchEvent(event) && findViewById(R.id.clip_move).isSelected();
    }
}
