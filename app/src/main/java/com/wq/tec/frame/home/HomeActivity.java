package com.wq.tec.frame.home;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.adapter.BasePagerAdapter;
import com.jazz.libs.util.ThreadUtils;
import com.wq.tec.R;
import com.wq.tec.WQActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoName on 2017/2/22.
 */

public class HomeActivity extends WQActivity {

    View mHomePay, mHomeCamera, mHomePlay;
    ImageView mHomeShow;
    android.support.v4.view.ViewPager mHomePager;

    @Override
    protected boolean isSupportCheckPermission() {
        return true;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        mHomePlay = findViewById(R.id.home_pay);
        mHomeCamera = findViewById(R.id.home_camera);
        mHomePlay = findViewById(R.id.home_play);
        mHomeShow = (ImageView) findViewById(R.id.home_show);
        mHomePager = (android.support.v4.view.ViewPager) findViewById(R.id.home_pager);
        mHomePager.setOffscreenPageLimit(3);
        mHomePager.setAdapter(new HomeAdapter(this, getPagerShowImage()));
        mHomePager.setCurrentItem(1);
    }

    List<String> getPagerShowImage(){
        List<String> homeList = new ArrayList<>();
        homeList.add("assets://home/home_1.png");
        homeList.add("assets://home/home_2.png");
        homeList.add("assets://home/home_3.png");
        homeList.add("assets://home/home_4.png");
        return homeList;
    }

    class HomeAdapter extends BasePagerAdapter<ImageView> {

        List<String> url = new ArrayList<>();

        public HomeAdapter(Context context, List<String> data) {
            super(context);
            for(String d : data){
                ImageView imgView = new ImageView(context);
                imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                this.data.add(imgView);
            }
            this.url.addAll(data);
        }


        @Override
        protected void configChildView(ImageView view, int position) {
            ImageView img = view;
            ImageManager.get().displayImage(this.url.get(position), img);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursorLoadPic();
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
//                        long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
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
