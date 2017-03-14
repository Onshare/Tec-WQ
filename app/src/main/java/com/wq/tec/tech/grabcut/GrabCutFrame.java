package com.wq.tec.tech.grabcut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jazz.libs.controller.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2017/2/8.
 */
public class GrabCutFrame extends BaseFragment implements View.OnTouchListener{

    private GrubCutImageView mImageView;
    private GrabCutHelper mHelper;
    private GrabCutLoader mLoader;

    void showImage(@NonNull Bitmap bitmap){
        this.mImageView.setImageBitmap(bitmap);
    }

    @Override
    protected void initParams() {
        mHelper = new GrabCutHelper(getActivity());
        mLoader = new GrabCutLoader(this, mHelper);
        GrabCutController.setGrabCutLoader(mLoader);
    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mImageView = new GrubCutImageView(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mLoader.canvasTouchModel(event);
        return mLoader.getModel() != null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GrabCutController.setGrabCutLoader(null);
        mHelper = null;
        mLoader.release();
        mLoader = null;
    }

    int getPointRadius(){
        return (int)(getResources().getDisplayMetrics().density * 8 + 0.5F);
    }

    void drawPointList(@NonNull Point srcPoint, @NonNull GrabCutLoader.GrabCutModel model, int color){
        mImageView.drawPointList(srcPoint, model, color);
    }

    void reSet(int index){
        mImageView.reset(index);
    }

    private class GrubCutImageView extends ImageView{

        public GrubCutImageView(Context context) {
            super(context);
            initPaint();
        }

        public GrubCutImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initPaint();
        }

        public GrubCutImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initPaint();
        }

        private void initPaint(){
            setScaleType(ScaleType.CENTER_CROP);
            mLowPaint.setStyle(Paint.Style.STROKE);
            mLowPaint.setStrokeWidth(getPointRadius());

            mHighPaint.setStyle(Paint.Style.STROKE);
            mHighPaint.setStrokeWidth(getPointRadius());
        }

        private List<Point> mLowPointList = new ArrayList<>();
        private List<Point> mHighPointList = new ArrayList<>();

        private Path mLowPath = new Path();
        private Path mHighPath = new Path();

        private Paint mLowPaint = new Paint();
        private Paint mHighPaint = new Paint();

        GrabCutLoader.GrabCutModel model;

        public void drawPointList(Point srcPoint, GrabCutLoader.GrabCutModel model, int color) {

            if(model == GrabCutLoader.GrabCutModel.LOW_PRECISION){
                if(mLowPointList.size() == 0){
                    mLowPath.moveTo(srcPoint.x, srcPoint.y);
                }else{
                    mLowPath.lineTo(srcPoint.x, srcPoint.y);
                }
                mLowPointList.add(srcPoint);
                mLowPaint.setColor(color);
            }else if(model == GrabCutLoader.GrabCutModel.HIGH_PRECISION){
                if(mHighPointList.size() == 0){
                    mHighPath.moveTo(srcPoint.x, srcPoint.y);
                }else{
                    mHighPath.lineTo(srcPoint.x, srcPoint.y);
                }
                mHighPointList.add(srcPoint);
                mHighPaint.setColor(color);
            }
            this.model = model;
            this.invalidate();
        }

        private void reset(int index){
            if(index == 0){
                this.mLowPointList.clear();
                this.mHighPointList.clear();
            }else if(index == 1){
                this.mLowPath.reset();
                this.mHighPath.reset();
            }
            this.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(mLowPath, mLowPaint);
            canvas.drawPath(mHighPath, mHighPaint);
        }
    }


}
