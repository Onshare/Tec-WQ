package com.wq.tec.frame.render.beauty;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.jazz.libs.util.ThreadUtils;
import com.wq.tec.R;
import com.wq.tec.filter.ImageBeautyFilter;
import com.wq.tec.filter.ImageRomanceFilter;
import com.wq.tec.filter.ImageSkinWhitenFilter;
import com.wq.tec.frame.render.RenderBaseFragment;
import com.wq.tec.frame.render.RenderFragment;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by N on 2017/3/15.
 */

public class BeautyFragment extends RenderBaseFragment implements View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener{

    ImageView[] imgs = new ImageView[3];
    Bitmap mBeautyBitmap = null;

    SeekBar bar = null;  int prevProgress = 50;

    GPUImage gpuImage = null;
    int[] dstSize = new int[2];

    String[] data = new String[]{
            "美白", "美肤", "粉嫩"
    };
    GPUImageFilter[] mImageFilter ;
    Bitmap[] mBeautyImgs = new Bitmap[data.length];
    boolean[] isFilter = new boolean[]{false, false, false};

    @Override
    protected void initParams() {
        mImageFilter = new GPUImageFilter[]{
                new ImageBeautyFilter(new int[]{getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels})
                , new ImageSkinWhitenFilter(new int[]{getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels})
                , new ImageRomanceFilter()

        };

        mBeautyBitmap = getRenderActivity().getDstBitmap().copy(Bitmap.Config.ARGB_8888, false);
        gpuImage = new GPUImage(getActivity());

        dstSize[0] = (int)(getResources().getDisplayMetrics().density * 54 + 0.5F);
        dstSize[1] = (int)(getResources().getDisplayMetrics().density * 70 + 0.5F);
        getDstList();

    }

    void getDstList(){
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                int width = dstSize[0];
                int height = dstSize[0] * mBeautyBitmap.getHeight() / mBeautyBitmap.getWidth();
                Bitmap bitmap = android.media.ThumbnailUtils.extractThumbnail(mBeautyBitmap, width, height);
                gpuImage.setImage(bitmap);
                for(int i = 0; i < mBeautyImgs.length; i++){
                    switch (i){
                        case 1:
                            gpuImage.setFilter(mImageFilter[0]);
                            break;
                        case 2:
                            gpuImage.setFilter(mImageFilter[1]);
                            break;
                        case 3:
                            gpuImage.setFilter(mImageFilter[2]);
                            break;
                    }
                    mBeautyImgs[i] = gpuImage.getBitmapWithFilterApplied();
                }
                invalidateInditor();
            }
        });
    }

    private void invalidateInditor(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgs[0].setImageBitmap(mBeautyImgs[0]);
                imgs[1].setImageBitmap(mBeautyImgs[1]);
                imgs[2].setImageBitmap(mBeautyImgs[2]);
            }
        });
    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmeng_beauty, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bar = (SeekBar) view.findViewById(R.id.ver_progressBar);
        (imgs[0] = (ImageView) view.findViewById(R.id.beauty_white_thumb)).setOnClickListener(this);
        (imgs[1] = (ImageView) view.findViewById(R.id.beauty_smooth_thumb)).setOnClickListener(this);
        (imgs[2] = (ImageView) view.findViewById(R.id.beauty_pink_thumb)).setOnClickListener(this);


        view.findViewById(R.id.beauty_compare).setOnTouchListener(this);
        view.findViewById(R.id.actionback).setOnClickListener(this);
        view.findViewById(R.id.actionsure).setOnClickListener(this);

        bar.setProgress(prevProgress);
        bar.setVisibility(View.GONE);
        bar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        action(v);
        selectView(v);
    }

    private void action(View view){
        switch (view.getId()){
            case R.id.actionback:
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
                getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
                break;
            case R.id.actionsure:
                if(mBeautyBitmap != null && mBeautyBitmap.getWidth() > 0 && mBeautyBitmap.getHeight() > 0){
                    gpuImage.setImage(mBeautyBitmap);
                    for(int index = 0; index < isFilter.length; index++){
                        if(isFilter[index]){
                            gpuImage.setFilter(mImageFilter[index]);
                        }
                    }
                    getRenderActivity().setBitmapResource(gpuImage.getBitmapWithFilterApplied());
                }
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
                getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
                break;
        }
    }


    private void selectView(View v){
        switch (v.getId()){
            case R.id.beauty_white_thumb:
                if(!isFilter[0]){
                    isFilter[0] = true;
                    isFilter[1] = false;
                    isFilter[2] = false;
                    gpuImage.setImage(mBeautyBitmap);
                    gpuImage.setFilter(mImageFilter[0]);
                    getRenderActivity().invalidate(gpuImage.getBitmapWithFilterApplied());
                }
                break;
            case R.id.beauty_smooth_thumb:
                if(!isFilter[1]){
                    isFilter[0] = false;
                    isFilter[1] = true;
                    isFilter[2] = false;
                    gpuImage.setImage(mBeautyBitmap);
                    gpuImage.setFilter(mImageFilter[1]);
                    getRenderActivity().invalidate(gpuImage.getBitmapWithFilterApplied());
                }
                break;
            case R.id.beauty_pink_thumb:
                if(!isFilter[2]){
                    isFilter[0] = false;
                    isFilter[1] = false;
                    isFilter[2] = true;
                    gpuImage.setImage(mBeautyBitmap);
                    gpuImage.setFilter(mImageFilter[2]);
                    getRenderActivity().invalidate(gpuImage.getBitmapWithFilterApplied());
                }
                break;
        }
        imgs[0].setSelected(R.id.beauty_white_thumb == v.getId());
        imgs[1].setSelected(R.id.beauty_smooth_thumb == v.getId());
        imgs[2].setSelected(R.id.beauty_pink_thumb == v.getId());
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.beauty_compare && mBeautyBitmap != null){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL
                    || event.getAction() == MotionEvent.ACTION_UP){
                gpuImage.setImage(mBeautyBitmap);
                for(int index = 0; index < isFilter.length; index++){
                    if(isFilter[index]){
                        gpuImage.setFilter(mImageFilter[index]);
                    }
                }
                getRenderActivity().invalidate(gpuImage.getBitmapWithFilterApplied());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
