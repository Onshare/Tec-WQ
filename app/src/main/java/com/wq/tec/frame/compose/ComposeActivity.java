package com.wq.tec.frame.compose;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
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
    ImageView comp_comp;


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

        comp_comp = (ImageView) findViewById(R.id.comp_comp);
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
                compImage.setDrawingCacheEnabled(true);
                compImage.buildDrawingCache();
                Bitmap result = compImage.getDrawingCache();
                if(result != null && ComposeActivity.mClipBitmap != null){
                    Bitmap overyLay = Bitmap.createBitmap(result.getWidth(), result.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(overyLay);
                    Paint mPaint = new Paint();
                    mPaint.setAntiAlias(true);
                    canvas.drawBitmap(result, 0, 0, mPaint);
                    canvas.drawBitmap(ComposeActivity.mClipBitmap
                            , new Rect(0, 0, ComposeActivity.mClipBitmap.getWidth(), ComposeActivity.mClipBitmap.getHeight())
                    , new RectF(comp_comp.getTranslationX(), comp_comp.getTranslationY(), result.getWidth() + comp_comp.getTranslationX(), result.getHeight() + comp_comp.getTranslationY()), mPaint);
                    canvas.save();
                    ComposeActivity.mClipBitmap = overyLay;
                }
                setResult(COMPOSE_RESULT);
                this.finish();
                break;
        }
    }

    float[] point = new float[2];
    float[] translation = new float[2];

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(ComposeActivity.mClipBitmap != null && comp_comp.getVisibility() == View.VISIBLE){
            if(MotionEvent.ACTION_DOWN == event.getAction()){
                if(point[0] == point[1] && point[0] == 0){
                    point[0] = (int) event.getX();
                    point[1] = (int) event.getY();
                }
                translation[0] = comp_comp.getTranslationX();
                translation[1] = comp_comp.getTranslationY();
            }else if(MotionEvent.ACTION_MOVE == event.getAction()){
                comp_comp.setTranslationX(event.getX() - point[0]+translation[0]);
                comp_comp.setTranslationY(event.getY() - point[1]+translation[1]);
            }else if(MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()){
                point[0] = 0;
                point[1] = 0;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageManager.get().displayImage(cover[2], compImage, mOptions);
        ImageManager.get().displayImage(cover[1], compOri, mOptions);
        if(ComposeActivity.mClipBitmap != null){
            comp_comp.setVisibility(View.VISIBLE);
            comp_comp.setImageBitmap(ComposeActivity.mClipBitmap);
        }else{
            comp_comp.setVisibility(View.GONE);
        }
    }
}
