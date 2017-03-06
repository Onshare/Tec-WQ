package com.wq.tec.tech.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jazz.libs.controller.BaseFragment;

/**
 * Created by N on 2017/1/20.
 */
public class CameraFrame extends BaseFragment{
    private static String TAG = CameraFrame.class.getClass().getSimpleName();


    private FrameLayout mFrame = null;
    private View mFocusView = null;
    private GLSurfaceView mSurface;
    private ImageView mShowResultView;

    private CameraLoader mLoader;
    private CameraHelper mHelper ;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mFocusView != null){
                mFocusView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void initParams() {
        mHelper = new CameraHelper(getContext());
        mLoader = new CameraLoader(this, mHelper);
    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFrame = new FrameLayout(getContext());
        mFrame.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        return mFrame;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFrame.addView(mSurface = new GLSurfaceView(getContext()), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mFrame.setBackgroundColor(Color.BLACK);
        mLoader.relativeGL(mSurface);
        mFrame.addView(mFocusView = getFocusView(getContext()));
        mFrame.addView(mShowResultView = new ImageView(getContext()), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mShowResultView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mFocusView.setVisibility(View.GONE);
        this.mShowResultView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.onResume();
        CameraController.setCameraLoader(mLoader);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLoader.onPause();
        CameraController.setCameraLoader(null);
        this.mShowResultView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoader.release();
        mLoader = null;
        mHelper = null;
        mSurface = null;
    }

    private @NonNull View getFocusView(@NonNull final Context context){
        return new View(context){

            Paint mPaint = null;
            Rect rect = null;

            {
                rect = new Rect(0, 0, 0, 0);
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setColor(Color.GREEN);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(6);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(
                        (int)(getResources().getDisplayMetrics().density * 50F + 0.5F),
                        (int)(getResources().getDisplayMetrics().density * 50F + 0.5F)
                );
                rect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(rect, mPaint);
            }
        };
    }

    void upDataFocusView(int x, int y){
        mFocusView.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams frameLayout = (FrameLayout.LayoutParams) mFocusView.getLayoutParams();
        frameLayout.topMargin = y - mFocusView.getHeight() ;
        frameLayout.leftMargin = x - mFocusView.getWidth() / 2;
        mFocusView.setLayoutParams(frameLayout);
        mHandler.sendEmptyMessageDelayed(1, 4000);
    }

    void showResult(@NonNull Bitmap bitmap){
        this.mShowResultView.setVisibility(View.VISIBLE);
        this.mShowResultView.setImageBitmap(bitmap);
    }

}
