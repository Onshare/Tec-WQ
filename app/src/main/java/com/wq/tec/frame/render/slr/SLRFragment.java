package com.wq.tec.frame.render.slr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.wq.tec.R;
import com.wq.tec.frame.clip.ClipActivity;
import com.wq.tec.frame.clip.ClipPresenter;
import com.wq.tec.frame.render.RenderActivity;
import com.wq.tec.frame.render.RenderBaseFragment;
import com.wq.tec.frame.render.RenderFragment;
import com.wq.tec.frame.render.RenderPresenter;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;

/**
 * Created by N on 2017/3/15.
 */

public class SLRFragment extends RenderBaseFragment implements View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener{

    Bitmap slrBitmap;
    View mBgBlur, mHorBlur, mForBlur, mBgGray;
    SeekBar bar = null;  int prevProgress = 50;

    GPUImage gpuImage = null;
    GPUImageFilter mFilter = null;

    private Bitmap mResultBitmap = null;

    @Override
    protected void initParams() {
        gpuImage = new GPUImage(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(slrBitmap != null && !slrBitmap.isRecycled()){
            slrBitmap.recycle();
        }
    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slr, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bar = (SeekBar) view.findViewById(R.id.ver_progressBar);
        view.findViewById(R.id.actionback).setOnClickListener(this);
        view.findViewById(R.id.actionsure).setOnClickListener(this);
        (mBgBlur = view.findViewById(R.id.slr_bg_blur)).setOnClickListener(this);
        (mHorBlur = view.findViewById(R.id.slr_hor_blur)).setOnClickListener(this);
        (mForBlur = view.findViewById(R.id.slr_for_blur)).setOnClickListener(this);
        (mBgGray = view.findViewById(R.id.slr_bg_gray)).setOnClickListener(this);
        view.findViewById(R.id.slr_compare).setOnTouchListener(this);
        view.findViewById(R.id.slr_redo).setOnClickListener(this);

        bar.setProgress(prevProgress);
        bar.setVisibility(View.GONE);
        bar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        action(v);
        doClick(v);
    }

    private void action(View view){
        switch (view.getId()){
            case R.id.actionback:
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
                goRender();
                break;
            case R.id.actionsure:
                if(mResultBitmap != null){
                    getRenderActivity().setBitmapResource(mResultBitmap);
                }
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
                getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
                break;
        }
    }

    public void doClick(View v){
        switch (v.getId()){
            case R.id.slr_bg_blur:
                switchView(v.getId());
                bar.setVisibility(View.VISIBLE);
                break;
            case R.id.slr_hor_blur:
                switchView(v.getId());
                bar.setVisibility(View.VISIBLE);
                break;
            case R.id.slr_for_blur:
                switchView(v.getId());
                bar.setVisibility(View.VISIBLE);
                break;
            case R.id.slr_bg_gray:
                switchView(v.getId());
                bar.setVisibility(View.GONE);
                break;
            case R.id.slr_redo:
                goClip();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.slr_compare && mResultBitmap != null){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL
                    || event.getAction() == MotionEvent.ACTION_UP){
                getRenderActivity().invalidate(mResultBitmap);
            }
            return true;
        }
        return false;
    }

    void switchView(int resId){
        mBgBlur.setSelected(R.id.slr_bg_blur == resId);
        mHorBlur.setSelected(R.id.slr_hor_blur == resId);
        mForBlur.setSelected(R.id.slr_for_blur == resId);
        mBgGray.setSelected(R.id.slr_bg_gray == resId);
        if(slrBitmap != null){
            handFilter(bar.getProgress());
        }else{
            goClip();
        }
    }

    @Override
    protected boolean onBackPressed() {
        goRender();
        return true;
    }

    void goRender(){
        getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
    }

    void goClip(){
        ClipPresenter.setBitmapResource(getRenderActivity().getDstBitmap());
        this.startActivityForResult(new Intent(getRenderActivity(), ClipActivity.class), ClipActivity.CLIP_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == resultCode && resultCode == ClipActivity.CLIP_RESULT){
            slrBitmap = ClipPresenter.getBitmap();
            ClipPresenter.setBitmapResource(null);
            handFilter(bar.getProgress());
        }
    }

    void handFilter(int progress){
        if(slrBitmap != null){
            Bitmap bg = getRenderActivity().getDstBitmap().copy(Bitmap.Config.ARGB_8888, true);
            Bitmap fore = slrBitmap.copy(Bitmap.Config.ARGB_8888, true);
            getGPUFilter(bg, fore);
            if(mFilter != null){
                if(mFilter instanceof GPUImageGaussianBlurFilter){
                    ((GPUImageGaussianBlurFilter)mFilter).setBlurSize(range(progress, 0.0F, 1.0F));
                }else if(mFilter instanceof GPUImagePixelationFilter){
                    ((GPUImagePixelationFilter)mFilter).setPixel(range(progress, 1.0F, 10.0F));
                }
                Bitmap result = gpuImage.getBitmapWithFilterApplied();
                if(mBgBlur.isSelected()){
                    compResult(result, fore);
                }else if(mHorBlur.isSelected()){
                    compResult(result, fore);
                }else if(mForBlur.isSelected()){
                    compResult(bg, result);
                }else if(mBgGray.isSelected()){
                    compResult(result, fore);
                }
            }
            if(!bg.isRecycled()){
                bg.recycle();
            }
            if(!fore.isRecycled()){
                fore.recycle();
            }
        }
    }

    void compResult(Bitmap bg, Bitmap fore){
        Bitmap overLay = Bitmap.createBitmap(getRenderActivity().getDstBitmap().getWidth(), getRenderActivity().getDstBitmap().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(overLay);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        cv.drawBitmap(bg, null, new Rect(0, 0, overLay.getWidth(), overLay.getHeight()), mPaint);
        cv.drawBitmap(fore, null, new Rect(0, 0, overLay.getWidth(), overLay.getHeight()), mPaint);
        getRenderActivity().invalidate(mResultBitmap = overLay);
    }

    void getGPUFilter(@NonNull Bitmap bg, @NonNull Bitmap fore){
        if(mBgBlur.isSelected()){
            gpuImage.setFilter(mFilter = new GPUImageGaussianBlurFilter());//0-1
            gpuImage.setImage(bg);
        }else if(mHorBlur.isSelected()){
            gpuImage.setFilter(mFilter = new GPUImagePixelationFilter());//0-100
            gpuImage.setImage(bg);
        }else if(mForBlur.isSelected()){
            gpuImage.setFilter(mFilter = new GPUImageGaussianBlurFilter());
            gpuImage.setImage(fore);
        }else if(mBgGray.isSelected()){
            gpuImage.setFilter(mFilter = new GPUImageGrayscaleFilter());//none
            gpuImage.setImage(bg);
        }
    }

    protected float range(final int percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(Math.abs(seekBar.getProgress() - prevProgress) > 5){
            handFilter(prevProgress = bar.getProgress());
        }
    }
}
