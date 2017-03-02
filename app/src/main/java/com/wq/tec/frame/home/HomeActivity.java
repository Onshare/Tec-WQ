package com.wq.tec.frame.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.util.DensityUtils;
import com.jazz.libs.util.ThreadUtils;
import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.frame.guid.GuidActivity;
import com.wq.tec.open.gallery.FancyCoverFlow;
import com.wq.tec.open.gallery.FancyCoverFlowAdapter;

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
        mHomePlay = findViewById(R.id.home_pay);
        mHomeCamera = findViewById(R.id.home_camera);
        mHomePlay = findViewById(R.id.home_play);
        mHomeShow = (ImageView) findViewById(R.id.home_show);
        mHomePager = (com.wq.tec.open.gallery.FancyCoverFlow) findViewById(R.id.home_pager);
        mHomePager.setAdapter(new HomeAdapter(this, getPagerShowImage()));
        mHomePlay.setOnClickListener(this);
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
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                String sdcardPath = Environment.getExternalStorageDirectory().getPath();
                Cursor mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                        MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列

                if(mCursor != null ){
                    List<String> mediaPath = new ArrayList<>();
                    while (mCursor.moveToNext()) {
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (path != null && path.contains(sdcardPath + "/DCIM/")) {
                            mediaPath.add(path);
                        }
                    }
                    mCursor.close();
                    if(mediaPath.size() > 0){
                        showMediaImageIcon(mediaPath.get(0));
                    }
                }
            }
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
}
