package com.wq.tec.frame.guid;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.util.BitmapUtil;
import com.jazz.libs.util.DensityUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.frame.camera.CameraActivity;
import com.wq.tec.open.gallery.FancyCoverFlow;
import com.wq.tec.open.gallery.FancyCoverFlowAdapter;
import com.wq.tec.tech.camera.CameraFrame;
import com.wq.tec.util.DataUtil;
import com.wq.tec.util.FileCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2017/3/2.
 */

public class GuidActivity extends WQActivity<GuidPresenter> implements AdapterView.OnItemSelectedListener{

    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisk(true).showImageOnFail(new ColorDrawable(Color.LTGRAY)).showImageOnLoading(new ColorDrawable(Color.LTGRAY)).showImageForEmptyUri(new ColorDrawable(Color.LTGRAY)).build();

    ImageView mCameraCover;
    com.wq.tec.open.gallery.FancyCoverFlow mPager = null;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guid);
        mCameraCover = (ImageView) findViewById(R.id.camera_cover);
        addFragment(new CameraFrame(), R.id.camera_frame);
        mPager = (com.wq.tec.open.gallery.FancyCoverFlow)findViewById(R.id.guid_pager);
        mPager.setOnItemSelectedListener(this);

        initActionBar();
    }

    void initActionBar(){
        ((TextView)findViewById(R.id.actiontitle)).setText("用户指导");
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
                    GuidRecord record = (GuidRecord) mPager.getSelectedItem();
                    Intent mIntent = new Intent(GuidActivity.this, CameraActivity.class);
                    if(record != null){
                        mIntent.putExtra("URL_CLIP", record.clip_url);
                        mIntent.putExtra("URL_ORIGIN", record.origin_url);
                        mIntent.putExtra("URL_BACKGROUND", record.background_url);
                    }
                    startActivity(mIntent);
                    GuidActivity.this.finish();
                    break;
            }
        }
    };

    public void select(View v){
        mPager.setVisibility(View.GONE);
        log(mPresent.humRecord.size()+"    "+mPresent.meRecord.size()+"    "+mPresent.sportRecord.size());
        int resId = v.getId();
        switch (resId){
            case R.id.guid_hum:
                if(mPresent.humRecord.size() != 0){
                    mPager.setVisibility(View.VISIBLE);
                    mPager.setAdapter(new GuidPagerAdapter(this, mPresent.humRecord));
                }
                break;
            case R.id.guid_me:
                if(mPresent.meRecord.size() != 0){
                    mPager.setVisibility(View.VISIBLE);
                    mPager.setAdapter(new GuidPagerAdapter(this, mPresent.meRecord));
                }
                break;
            case R.id.guid_sport:
                if(mPresent.sportRecord.size() != 0){
                    mPager.setVisibility(View.VISIBLE);
                    mPager.setAdapter(new GuidPagerAdapter(this, mPresent.sportRecord));
                }
                break;
        }
        selectorView(resId);
    }

    private void selectorView(int resId){
        findViewById(R.id.guid_hum).setSelected(resId == R.id.guid_hum);
        findViewById(R.id.guid_me).setSelected(resId == R.id.guid_me);
        findViewById(R.id.guid_sport).setSelected(resId == R.id.guid_sport);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == mPager){
            log(position);
            GuidRecord record = ((GuidPagerAdapter)mPager.getAdapter()).getItem(position);
            String mFileName = getUrlDecode(record.clip_url) + ".png";
            if(FileCacheUtil.checkExist(mFileName)){
                ImageManager.get().displayImage("file://"+FileCacheUtil.getCachePath()+mFileName, mCameraCover);
            }else{
                mCameraCover.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mCameraCover.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    class GuidPagerAdapter extends FancyCoverFlowAdapter{

        private List<GuidRecord> data;
        private LayoutInflater mInflater;
        private int[] imgSize = new int[2];

        public GuidPagerAdapter(@NonNull Context ctx, List<GuidRecord> data) {
            this.data = new ArrayList<>(data);
            mInflater = LayoutInflater.from(ctx);
            imgSize[0] = DensityUtils.dp2px(ctx, 69);
            imgSize[1] = DensityUtils.dp2px(ctx, 98);
        }

        @Override
        public int getCount() {
            return this.data.size();
        }

        @Override
        public GuidRecord getItem(int position) {
            return this.data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getCoverFlowItem(final int position, View reusableView, ViewGroup parent) {
            if(reusableView == null){
                reusableView = mInflater.inflate(R.layout.item_guid, null);
            }
            ImageView mImage = (ImageView) reusableView.findViewById(R.id.guid_img);
            reusableView.setLayoutParams(new FancyCoverFlow.LayoutParams(imgSize[0], imgSize[1]));
            ImageManager.get().displayImage(data.get(position).thumb_url, mImage, mOptions);

            final RelativeLayout loadOwner = (RelativeLayout) reusableView.findViewById(R.id.guid_download);
            String urlPath = getUrlDecode(data.get(position).clip_url)+".png";
            if(FileCacheUtil.checkExist(urlPath)){
                loadOwner.setVisibility(View.GONE);
            }else{
                loadOwner.setVisibility(View.VISIBLE);
            }
            loadOwner.findViewById(R.id.guid_download_down).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadImage(loadOwner, data.get(position).clip_url);
                }
            });
            return reusableView;
        }
    }

    private void downLoadImage(final ViewGroup loadOwner, final String mUrlPath){
        loadOwner.findViewById(R.id.guid_download_load).setVisibility(View.VISIBLE);
        loadOwner.findViewById(R.id.guid_download_down).setVisibility(View.GONE);
        ImageManager.get().loadImage(mUrlPath, new ImageManager.DefaultImageLoaderListener(){

            ObjectAnimator anim = ObjectAnimator.ofFloat(loadOwner.findViewById(R.id.guid_download_load), "rotation", 0F, 360F);

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                super.onLoadingComplete(s, view, bitmap);
                loadOwner.findViewById(R.id.guid_download_load).setVisibility(View.GONE);
                loadOwner.findViewById(R.id.guid_download_down).setVisibility(View.GONE);
                loadOwner.setVisibility(View.GONE);
                anim.cancel();
                if(bitmap != null){
                    BitmapUtil.saveToFile(bitmap, FileCacheUtil.getCachePath() + getUrlDecode(mUrlPath)+".png");//
                }
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                super.onLoadingFailed(s, view, failReason);
                loadOwner.findViewById(R.id.guid_download_load).setVisibility(View.GONE);
                loadOwner.findViewById(R.id.guid_download_down).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingStarted(String s, View view) {
                super.onLoadingStarted(s, view);
                anim.setDuration(500);
                anim.setRepeatCount(-1);
                anim.setInterpolator(new LinearInterpolator());
                anim.start();
            }
        });
    }

    private String getUrlDecode(String urlPath){
        return DataUtil.getBase64(urlPath.getBytes()).trim().replace("/", "").replace("\\", "");
    }
}
