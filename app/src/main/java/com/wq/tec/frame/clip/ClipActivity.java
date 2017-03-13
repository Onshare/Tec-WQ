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
import android.widget.ImageView;

import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.open.BlackGroundView;
import com.wq.tec.open.ClipControllerView;
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

public class ClipActivity extends WQActivity<ClipPresenter> implements View.OnTouchListener{

    public static final int CLIP_RESULT = 1002;

    BlackGroundView mGround;
    ClipControllerView mClipImage ;//展示前景的

    private boolean isIntercept = false;

    private List<Path> mSelectPathList = new ArrayList<>();
    private List<Path> mCancelPathList = new ArrayList<>();

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clip);

        addFragment(new GrabCutFrame(), R.id.clip_frame);
        mClipImage = (ClipControllerView) findViewById(R.id.clip_image);
        mGround = (BlackGroundView)findViewById(R.id.clip_ground);
        mClipImage.setOnTouchListener(this);
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
        isIntercept = resId == R.id.clip_add || resId == R.id.clip_del;
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
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        mPaint.setColor(mPresent.getColor());
        cv.drawRect(new Rect(0, 0, cv.getWidth(), cv.getHeight()), mPaint);

        mClipImage.setImageBitmap(overlay);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(R.id.clip_image == v.getId()){
            int action = event.getAction();
            float scalX = event.getX();
            float scalY = event.getY();
            switch (action){
                case MotionEvent.ACTION_MOVE:
                    if(mPath == null){
                        mPath = new Path();
                        mPath.moveTo(scalX, scalY);
                    }else {
                        mPath.lineTo(scalX, scalY);
                    }
                    canvas();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(0 == getControlId()){
                        mSelectPathList.add(mPath);
                    }else if(1 == getControlId()){
                        mCancelPathList.add(mPath);
                    }
                    mPath = null;
                    break;
            }
        }
        return isIntercept;
    }

    private Path mPath ;

    void canvas(){
        if(getControlId() == 0 || getControlId() == 1){
            Paint mPaint = mPresent.getClipPaint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(30);
            mPaint.setStyle(Paint.Style.STROKE);

            if(1 == getControlId()){
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                mPaint.setColor(Color.TRANSPARENT);
            }else {
                mPaint.setColor(mPresent.getColor());
            }
            mClipImage.invalidate(mSelectPathList, mCancelPathList, mPaint);
//            cv.drawPath(mPath, mPaint);
//            mPresent.submitCanvas(mClipImage);
            mPaint.reset();
        }
    }

    private int getControlId(){
        if(findViewById(R.id.clip_add).isSelected()){
            return 0;
        }else if(findViewById(R.id.clip_del).isSelected()){
            return 1;
        }
        return -1;
    }
}
