package com.wq.tec.frame.render.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.adapter.BaseRecyclerViewAdapter;
import com.jazz.libs.adapter.DividerItemDecoration;
import com.jazz.libs.util.ThreadUtils;
import com.wq.tec.R;
import com.wq.tec.frame.render.RenderBaseFragment;
import com.wq.tec.frame.render.RenderFragment;

import java.util.Arrays;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

/**
 * Created by N on 2017/3/15.
 */

public class FilterFragment extends RenderBaseFragment implements View.OnClickListener, View.OnTouchListener{

    GPUImage gpuImage = null;
    android.support.v7.widget.RecyclerView mPager ;
    Bitmap mFilterBitmap = null;
    int mPositionRecord = 0;

    String[] data = new String[]{
            "原图", "经典", "HDR", "碧波", "上野"
            , "优格", "彩虹瀑", "云端", "淡雅", "粉红佳人"
            , "复古", "候鸟", "一九OO", "古铜色", "哥特风"
            , "移轴", "黑白"
    };
    Bitmap[] mFilterImgs = new Bitmap[data.length];
    int[] dstSize = new int[2];

    @Override
    protected void initParams() {
        gpuImage = new GPUImage(getActivity());
        mFilterBitmap = getRenderActivity().getDstBitmap().copy(Bitmap.Config.ARGB_8888, false);
        gpuImage.setImage(getRenderActivity().getDstBitmap());

        dstSize[0] = (int)(getResources().getDisplayMetrics().density * 54 + 0.5F);
        dstSize[1] = (int)(getResources().getDisplayMetrics().density * 70 + 0.5F);

        getDstList();
    }

    void getDstList(){
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                int width = dstSize[0];
                int height = dstSize[0] * mFilterBitmap.getHeight() / mFilterBitmap.getWidth();
                Bitmap bitmap = android.media.ThumbnailUtils.extractThumbnail(mFilterBitmap, width, height);
                android.util.Log.e("DstList", bitmap.getWidth()+" "+bitmap.getHeight()+" "+mFilterBitmap.getWidth()+" "+mFilterBitmap.getHeight());
                gpuImage.setImage(bitmap);
                for(int i = 0; i < mFilterImgs.length; i++){
                    mFilterImgs[i] = getGPUImage(i);
                }
                invalidatePager();
            }
        });
    }

    private void invalidatePager(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private Bitmap getGPUImage(int position){
        switch (position){
            case 0://原图
                return getRenderActivity().getDstBitmap();
            case 1://经典
                gpuImage.setFilter(new GPUImageMonochromeFilter(0.88F, new float[]{0.6F, 0.45F, 0.3F, 1.0F}));
                break;
            case 2://HDR
                gpuImage.setFilter(new GPUImageBrightnessFilter());
                break;
            case 3://碧波
                gpuImage.setFilter(new GPUImageHazeFilter(0.15F, 0.2F));
                break;
            case 4://上野//unkown
                gpuImage.setFilter(new GPUImageHueFilter(10F));
                break;
            case 5://优格
                gpuImage.setFilter(new GPUImageHueFilter(95F));
                break;
            case 6://彩虹瀑
                gpuImage.setFilter(new GPUImageMonochromeFilter(0.95F, new float[]{0.6F, 0.45F, 0.3F, 1.0F}));
                break;
            case 7://云端
                gpuImage.setFilter(new GPUImageSepiaFilter(0.7F));
                break;
            case 8://淡雅
                GPUImageWhiteBalanceFilter mWhiteBalanceFilter8 = new GPUImageWhiteBalanceFilter();
                gpuImage.setFilter(mWhiteBalanceFilter8); //2000 - 8000
                mWhiteBalanceFilter8.setTemperature(3600F);
                break;
            case 9://粉红佳人
                gpuImage.setFilter(new GPUImageRGBFilter(1.0F, 0.72F, 0.75F));
                break;
            case 10://复古
                gpuImage.setFilter(new GPUImageMonochromeFilter(0.75F, new float[]{0.6F, 0.45F, 0.3F, 1.0F}));
                break;
            case 11://候鸟
                gpuImage.setFilter(new GPUImageMonochromeFilter(0.6F, new float[]{0.6F, 0.45F, 0.3F, 1.0F}));
                break;
            case 12://一九OO
                GPUImageWhiteBalanceFilter mWhiteBalanceFilter12 = new GPUImageWhiteBalanceFilter();
                gpuImage.setFilter(mWhiteBalanceFilter12); //2000 - 8000
                mWhiteBalanceFilter12.setTemperature(5000F);
                break;
            case 13://古铜色
                GPUImageWhiteBalanceFilter mWhiteBalanceFilter13 = new GPUImageWhiteBalanceFilter();
                gpuImage.setFilter(mWhiteBalanceFilter13); //2000 - 8000
                mWhiteBalanceFilter13.setTemperature(8000F);
                break;
            case 14://哥特风
                GPUImageColorBalanceFilter mColorBalanceFilter = new GPUImageColorBalanceFilter();
                gpuImage.setFilter(mColorBalanceFilter);//0 - 1 0-1 0-1
                mColorBalanceFilter.setMidtones(new float[]{0.65F, 0.33F, 0.22F});
                break;
            case 15://移轴
                gpuImage.setFilter(new GPUImageContrastFilter(1.6F));//0 - 2
                break;
            case 16://黑白
                gpuImage.setFilter(new GPUImageGrayscaleFilter());//
                break;
        }
        return gpuImage.getBitmapWithFilterApplied();
    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmeng_filter, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPager = (android.support.v7.widget.RecyclerView)view.findViewById(R.id.filter_pager);
        view.findViewById(R.id.filter_compare).setOnTouchListener(this);
        view.findViewById(R.id.actionback).setOnClickListener(this);
        view.findViewById(R.id.actionsure).setOnClickListener(this);
        mPager.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mPager.addItemDecoration(DividerItemDecoration.defaultOrientationDecoration(getActivity(), mPager, 30, DividerItemDecoration.HORIZONTAL_LIST));
        mPager.setAdapter(new FilterAdapter(getActivity()));
    }

    @Override
    public void onClick(View v) {
        action(v);
        doClick(v);
    }

    private void action(View view){
        switch (view.getId()){
            case R.id.actionback:
                getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
                break;
            case R.id.actionsure:
                if(mPositionRecord > 0 && mPositionRecord < mFilterImgs.length){
                    gpuImage.deleteImage();
                    gpuImage.setImage(mFilterBitmap);
                    getRenderActivity().setBitmapResource(getGPUImage(mPositionRecord));
                }
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
                getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
                break;
        }
    }

    public void doClick(View v){
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(gpuImage != null){
            gpuImage.deleteImage();
        }
        for(int i = 0; i < mFilterImgs.length && mFilterImgs[i] != null && !mFilterImgs[i].isRecycled(); i++){
            mFilterImgs[i].recycle();
        }
        if(mFilterBitmap != null && !mFilterBitmap.isRecycled()){//todo 待测试
            mFilterBitmap.recycle();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.filter_compare && mFilterBitmap != null){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL
                    || event.getAction() == MotionEvent.ACTION_UP){
                gpuImage.deleteImage();
                gpuImage.setImage(mFilterBitmap);
                getRenderActivity().invalidate(getGPUImage(mPositionRecord));
            }
            return true;
        }
        return false;
    }

    private class FilterAdapter extends BaseRecyclerViewAdapter<String>{


        public FilterAdapter(Context context) {
            super(context);
            setData(Arrays.asList(FilterFragment.this.data), false);
        }

        @Override
        public int getItemLayerId() {
            return R.layout.item_filter;
        }

        @Override
        public void onBindViewHolder(String d, BaseRecyHolder holder, final int position) {
            final ImageView mFilterImage = (ImageView) holder.getViewbyId(R.id.filter_thumb);
            TextView mFilterDisc = (TextView) holder.getViewbyId(R.id.filter_disc);
            mFilterDisc.setText(FilterFragment.this.data[position]);
            if(position < mFilterImgs.length && mFilterImgs[position] != null){
                mFilterImage.setImageBitmap(mFilterImgs[position]);
            }
            holder.getHolderView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPositionRecord = position;
                    gpuImage.deleteImage();
                    gpuImage.setImage(mFilterBitmap);
                    getRenderActivity().invalidate(getGPUImage(position));
                }
            });
        }
    }
}
