package com.wq.tec.frame.save;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jazz.libs.util.BitmapUtil;
import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.frame.camera.CameraActivity;
import com.wq.tec.frame.home.HomeActivity;
import com.wq.tec.frame.render.RenderActivity;
import com.wq.tec.frame.render.RenderPresenter;
import com.wq.tec.util.FileCacheUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;

/**
 * Created by N on 2017/3/21.
 */

public class SaveActivity extends WQActivity implements View.OnClickListener{

    ImageView mSaveIcon , mSaveBK;
    TextView mSaveStat, mSaveGoCamera, mSaveGoNext;

    private Bitmap mSaveBitmap;
    private GPUImage mGpuImage ;

    private static Bitmap bitmap = null;

    public static void setBitmapResource(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            SaveActivity.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        }
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if(SaveActivity.bitmap != null){
            mSaveBitmap = SaveActivity.bitmap.copy(Bitmap.Config.ARGB_8888, false);
            SaveActivity.bitmap.recycle();
            mGpuImage = new GPUImage(this);
            mGpuImage.setImage(mSaveBitmap);
        }
        setContentView(R.layout.activity_save);
        initActionBar();
        mSaveIcon = (ImageView) findViewById(R.id.save_icon);
        mSaveBK = (ImageView) findViewById(R.id.save_bk);

        mSaveStat = (TextView) findViewById(R.id.save_stat);
        mSaveGoCamera = (TextView) findViewById(R.id.save_gocamera);
        mSaveGoNext = (TextView) findViewById(R.id.save_gonext);
        mSaveGoCamera.setOnClickListener(this);
        mSaveGoNext.setOnClickListener(this);

        inValidateFrame();
        new SaveTask().execute();
    }

    void initActionBar(){
        ((TextView)findViewById(R.id.actiontitle)).setText("保存");
        findViewById(R.id.actionsure).setVisibility(View.GONE);
        findViewById(R.id.actionsure_text).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.actionsure_text)).setText("首页");
        findViewById(R.id.actionsure_text).setOnClickListener(this);
        findViewById(R.id.actionback).setOnClickListener(this);
        ((View)findViewById(R.id.actiontitle).getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        action(v);
        goAction(v);
    }

    private void action(View view){
        switch (view.getId()){
            case R.id.actionback:
                this.finish();
                break;
            case R.id.actionsure_text:
                Intent mIntent = new Intent(this, HomeActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
                this.finish();
                break;
        }
    }

    private void goAction(View view){
        switch (view.getId()){
            case R.id.save_gocamera:
                startActivity(new Intent(this, CameraActivity.class));
                this.finish();
                break;
            case R.id.save_gonext:
                goPicture(this);
                break;
        }
    }

    void inValidateFrame(){
        if(this.mSaveBitmap != null){
            mSaveIcon.setImageBitmap(mSaveBitmap);
            mGpuImage.setFilter(new GPUImageGaussianBlurFilter());
            mSaveBK.setImageBitmap(mGpuImage.getBitmapWithFilterApplied());
        }
    }


    class SaveTask extends AsyncTask<Void, Void, String>{

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            SaveActivity.this.mSaveStat.setText("保存中");
        }

        @Override
        protected String doInBackground(Void... params) {
            BitmapUtil.saveToFile(SaveActivity.this.mSaveBitmap, FileCacheUtil.getSaveURL());
            File mFile = new File(FileCacheUtil.getSaveURL());
            if(mFile.exists()){
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), FileCacheUtil.getSaveURL(), FileCacheUtil.getSaveName(), "握趣印象");
                } catch (FileNotFoundException e) {
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + FileCacheUtil.getSaveURL())));
            }
            return "保存成功";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SaveActivity.this.mSaveStat.setText("保存成功");
        }
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
                this.finish();
            } catch (IOException e) {
            }
        }
    }
}
