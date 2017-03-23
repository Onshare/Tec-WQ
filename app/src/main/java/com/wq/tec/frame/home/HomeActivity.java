package com.wq.tec.frame.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.termination.TerminationTask;
import com.jazz.libs.util.DensityUtils;
import com.jazz.libs.util.ThreadUtils;
import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.frame.camera.CameraActivity;
import com.wq.tec.frame.guid.GuidActivity;
import com.wq.tec.frame.render.RenderActivity;
import com.wq.tec.frame.render.RenderPresenter;
import com.wq.tec.frame.web.WebAcitivity;
import com.wq.tec.open.gallery.FancyCoverFlow;
import com.wq.tec.open.gallery.FancyCoverFlowAdapter;
import com.wq.tec.util.FileCacheUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoName on 2017/2/22.
 */

public class HomeActivity extends WQActivity implements View.OnClickListener{

    View mHomePay, mHomeCamera, mHomePlay;
    ImageView mHomeShow;
    com.wq.tec.open.gallery.FancyCoverFlow mHomePager;

    @Override
    protected boolean isSupportCheckPermission() {
        return true;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {//
        setContentView(R.layout.activity_home);
        mHomePay = findViewById(R.id.home_pay);
        mHomeCamera = findViewById(R.id.home_camera);
        mHomePlay = findViewById(R.id.home_play);
        mHomeShow = (ImageView) findViewById(R.id.home_show);
        mHomePager = (com.wq.tec.open.gallery.FancyCoverFlow) findViewById(R.id.home_pager);
        mHomePager.setAdapter(new HomeAdapter(this, getPagerShowImage()));
        mHomePlay.setOnClickListener(this);
        mHomeCamera.setOnClickListener(this);
        mHomePay.setOnClickListener(this);
        mHomeShow.setOnClickListener(this);
    }

    List<String> getPagerShowImage(){
        List<String> homeList = new ArrayList<>();
        homeList.add("assets://home/home_1.png");
        homeList.add("assets://home/home_2.png");
        homeList.add("assets://home/home_3.png");
        homeList.add("assets://home/home_4.png");
        return homeList;
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if(R.id.home_play == resId){
            startActivity(new Intent(this, GuidActivity.class));
        }else if(R.id.home_camera == resId){
            startActivity(new Intent(this, CameraActivity.class));
        }else if(R.id.home_pay == resId){
            Intent mIntent = new Intent(this, WebAcitivity.class);
            mIntent.putExtra("webUrl", "https://www.tmall.com/");
            startActivity(mIntent);
        } else if(R.id.home_show == resId){
            goPicture(this);
        }
    }

    class HomeAdapter extends FancyCoverFlowAdapter{

        private List<String> data;
        private LayoutInflater mInflater;
        private int[] imgSize = new int[2];
        private int[] resource = new int[]{R.mipmap.home_1, R.mipmap.home_2, R.mipmap.home_3, R.mipmap.home_4};

        public HomeAdapter(@NonNull Context ctx, List<String> data) {
            this.data = new ArrayList<>(data);
            mInflater = LayoutInflater.from(ctx);
            imgSize[0] = (ctx.getResources().getDisplayMetrics().widthPixels - DensityUtils.dp2px(ctx, 17) * 2) * 2 / 3;
            imgSize[1] = imgSize[0] * 1136 / 639;
        }

        @Override
        public int getCount() {
            return this.data.size();
        }

        @Override
        public String getItem(int position) {
            return this.data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getCoverFlowItem(int position, View reusableView, ViewGroup parent) {
            if(reusableView == null){
                reusableView = mInflater.inflate(R.layout.item_home, null);
            }
            ImageView mHomeImage = (ImageView) reusableView.findViewById(R.id.home_img);
            mHomeImage.setImageResource(resource[position]);
            reusableView.setLayoutParams(new FancyCoverFlow.LayoutParams(imgSize[0], imgSize[1]));
            return reusableView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursorLoadPic();
        mHomePager.setSelection(1);
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
                ImageManager.get().displayImage("file://"+imgPath, mHomeShow);
            }
        });
    }

    public static void goPicture(@NonNull android.app.Activity context){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        context.startActivityForResult(intent, 10001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001 && data != null && data.getData() != null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                RenderPresenter.setBitmapResource(bitmap);
                Intent mIntent = new Intent();
                mIntent.setClass(this, RenderActivity.class);
                startActivity(mIntent);
            } catch (IOException e) {
            }
        }
    }
}
