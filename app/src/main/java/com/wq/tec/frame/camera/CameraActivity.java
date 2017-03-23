package com.wq.tec.frame.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jazz.libs.ImageLoader.ImageManager;
import com.jazz.libs.termination.TerminationTask;
import com.jazz.libs.util.BitmapUtil;
import com.jazz.libs.util.ThreadUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.filter.ImageBeautyFilter;
import com.wq.tec.frame.clip.ClipActivity;
import com.wq.tec.frame.clip.ClipPresenter;
import com.wq.tec.frame.compose.ComposeActivity;
import com.wq.tec.frame.guid.GuidActivity;
import com.wq.tec.frame.render.RenderActivity;
import com.wq.tec.frame.render.RenderPresenter;
import com.wq.tec.frame.save.SaveActivity;
import com.wq.tec.tech.camera.CameraController;
import com.wq.tec.tech.camera.CameraLoader;
import com.wq.tec.util.FileCacheUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;


/**
 * Created by NoName on 2017/1/19.
 */

public class CameraActivity extends WQActivity {

    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisk(true).build();

    @Override
    protected boolean isSupportCheckPermission() {
        return true;
    }

    private int cameraId = 0;
    private boolean isOpenFlash = false;

    private ImageView mCameraShow;
    private ImageView mCameraCover;
    private String[] covers = new String[3];//极限智拍有背景
    private int mCoverModel = -1;

    private GPUImageFilter mFilter ;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        mFilter = new ImageBeautyFilter(new int[]{getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels});
        addCameraFrame();
        mCameraShow = (ImageView) findViewById(R.id.camera_show);
        mCameraCover = (ImageView) findViewById(R.id.camera_cover);
        covers[0] = getIntent().getStringExtra("URL_CLIP");
        covers[1] = getIntent().getStringExtra("URL_ORIGIN");
        covers[2] = getIntent().getStringExtra("URL_BACKGROUND");
        if(TextUtils.isEmpty(covers[0]) || TextUtils.isEmpty(covers[1])){
            mCoverModel = -1;
        }else{
            mCoverModel = 1;
        }
    }

    void addCameraFrame(){
        addFragment(new com.wq.tec.tech.camera.CameraFrame(), R.id.camera_frame);
    }


    public void doAction(View v){
        switch (v.getId()){
            case R.id.cameraback:
                this.finish();
                break;
            case R.id.cameraswitch:
                CameraController.switchCamera( (++cameraId) % 2);
                break;
            case R.id.cameralight:
                CameraController.openFlash(isOpenFlash = !isOpenFlash);
                findViewById(R.id.cameralight).setSelected(isOpenFlash);
                break;
        }
    }

    public void doTackePic(View v){
        if(R.id.camera_take == v.getId()){
            CameraController.takePic(new CameraLoader.TakePicCallBack() {
                @Override
                public void takePic(@NonNull Bitmap bitmap) {
                    mCameraShow.setImageBitmap(bitmap);
                    new SaveTask().execute(bitmap);
                }
            });
        }
    }

    public void doControl(View v){
        if(R.id.camera_filter == v.getId()){
            v.setSelected(!v.isSelected());
            if(v.isSelected()){
                CameraController.setFrameFilter(new ImageBeautyFilter(new int[]{11, 19}));
                Toast.makeText(this, "关闭滤镜", Toast.LENGTH_SHORT).show();
            }else{
                CameraController.setFrameFilter(mFilter);
                Toast.makeText(this, "打开滤镜", Toast.LENGTH_SHORT).show();
            }
        }else if(R.id.camera_guid == v.getId()){
            startActivity(new Intent(this, GuidActivity.class));
            this.finish();
        }else if(R.id.camera_show == v.getId()){
            goPicture(this);
        }
    }

    public void doCover(View v){
        if(R.id.camera_conver_mode == v.getId()){
            mCoverModel++;
            mCoverModel = mCoverModel > 3 ? 1 : mCoverModel;
        }else if(R.id.camera_cover_close == v.getId()){//关闭
            mCoverModel = 0;
        }
        setMode(mCoverModel);
        FadeInBitmapDisplayer.animate(mCameraCover, 500);
    }

    private void setMode(int mode){
        if(mode == 0 || mode == -1){
            mCoverModel = 3;
            findViewById(R.id.camera_cover_close).setVisibility(View.GONE);
            findViewById(R.id.camera_conver_mode).setVisibility(mode == -1 ? View.GONE : View.VISIBLE);
            setMode(mCoverModel);
        }else if(mode == 1 && covers[0] != null){
            ImageManager.get().displayImage(covers[0], mCameraCover, mOptions);
            ((TextView)findViewById(R.id.camera_conver_mode)).setText("原图");
        }else if(mode == 2 && covers[1] != null){
            ImageManager.get().displayImage(covers[1], mCameraCover, mOptions);
            ((TextView)findViewById(R.id.camera_conver_mode)).setText("隐藏");
        }else if(mode == 3 && covers[2] != null){
            mCameraCover.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            ((TextView)findViewById(R.id.camera_conver_mode)).setText("指导");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraId = 0;
        isOpenFlash = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMode(mCoverModel);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraController.setFrameFilter(mFilter);
            }
        }, 1000);
        mCursorLoadPic();
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
                ImageManager.get().displayImage("file://"+imgPath, mCameraShow);
            }
        });
    }

    void goToRender(@NonNull Bitmap bitmap){
        Bitmap result = scaleBitmap(bitmap, 500);
        RenderPresenter.setBitmapResource(result);
        Intent mIntent = new Intent();
        mIntent.setClass(this, RenderActivity.class);
        startActivity(mIntent);
        this.finish();
    }

    void goToComPose(@NonNull Bitmap bitmap){
        ComposeActivity.setCompImage(bitmap);
        Intent mIntent = new Intent(this, ComposeActivity.class);
        mIntent.putExtra("Guid_Path", covers);
        startActivityForResult(mIntent, ComposeActivity.COMPOSE_RESULT);
    }

    void goToClip(@NonNull Bitmap bitmap){
        Bitmap result = scaleBitmap(bitmap, 500);
        ClipPresenter.setBitmapResource(result);
        startActivityForResult(new Intent(this, ClipActivity.class), ClipActivity.CLIP_RESULT);
    }


    private Bitmap scaleBitmap(@NonNull Bitmap bitmap, int scaleSize){//单位KB
        int length = bitmap.getByteCount() / 1024;
        double be = (double) length / scaleSize;
        if(be > 1){
            int[] mScreentSize = new int[]{getResources().getDisplayMetrics().widthPixels / 2, getResources().getDisplayMetrics().heightPixels / 2};
            return ThumbnailUtils.extractThumbnail(bitmap, mScreentSize[0], mScreentSize[1]);
        }
        return bitmap;
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
                goToRender(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
            } catch (IOException e) {
            }
        }else if(requestCode == resultCode && resultCode == ClipActivity.CLIP_RESULT){
            goToComPose(ClipPresenter.getBitmap());
            ClipPresenter.setBitmapResource(null);
        }else if(requestCode == resultCode && resultCode == ComposeActivity.COMPOSE_RESULT && ComposeActivity.getCompImage() != null){
            goToRender(ComposeActivity.getCompImage());
        }
    }

    class SaveTask extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            String urlPath = FileCacheUtil.getCachePath()+"WQ_CAMERA.png";
            BitmapUtil.saveToFile(params[0], urlPath);
            File mFile = new File(urlPath);
            if(mFile.exists()){
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), urlPath, "WQ_CAMERA.png", "握趣印象");
                } catch (FileNotFoundException e) {
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + urlPath)));
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(!TextUtils.isEmpty(covers[2])){
                goToClip(bitmap);
            } else {
                CameraController.startCamera();
            }
        }
    }
}
