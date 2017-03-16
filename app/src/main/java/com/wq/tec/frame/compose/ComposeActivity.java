package com.wq.tec.frame.compose;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.jazz.libs.ImageLoader.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wq.tec.R;
import com.wq.tec.WQActivity;

/**
 * Created by N on 2017/3/16.
 */

public class ComposeActivity extends WQActivity<ComposePresenter> implements View.OnClickListener{

    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisk(true).build();

    final String[] cover = new String[3];
    ImageView compImage ,compOri;


    public static Bitmap mClipBitmap = null;
    public static final int COMPOSE_RESULT = 10023;

    public static void setCompImage(Bitmap compImage) {
        ComposeActivity.mClipBitmap = compImage;
    }

    public static Bitmap getCompImage() {
        return mClipBitmap;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        String[] path = getIntent().getStringArrayExtra("Guid_Path");
        checkPath(path);
        cover[0] = path[0];
        cover[1] = path[1];
        cover[2] = path[2];
        setContentView(R.layout.activity_compose);
        findViewById(R.id.actionback).setOnClickListener(this);
        findViewById(R.id.actionsure).setOnClickListener(this);

        compImage = (ImageView) findViewById(R.id.comp_image);
        compOri = (ImageView) findViewById(R.id.comp_ori);
    }

    void checkPath(String[] str){
        if(str == null || str.length < 3 || TextUtils.isEmpty(str[0]) || TextUtils.isEmpty(str[1]) || TextUtils.isEmpty(str[2])){
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        action(v);
    }

    private void action(View view){
        switch (view.getId()){
            case R.id.actionback:
                this.finish();
                break;
            case R.id.actionsure:
                setResult(COMPOSE_RESULT);
                this.finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageManager.get().displayImage(cover[2], compImage, mOptions);
        ImageManager.get().displayImage(cover[0], compOri, mOptions);
    }
}
