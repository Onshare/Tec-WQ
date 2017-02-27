package com.wq.tec.tech.grabcut;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by N on 2017/2/8.
 */
public class GrabCutLoader{

    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private boolean isRunning = false;
    private Handler osHandler = new Handler();

    public static enum GrabCutModel{
        HIGH_PRECISION, LOW_PRECISION;
    }

    GrabCutFrame mFrame;
    GrabCutHelper mHelper;
    GrabCutModel model = GrabCutModel.LOW_PRECISION;


    private Bitmap bitmap;
    private List<Point> forePoint, backgroundPoint;

    GrabCutLoader(@NonNull GrabCutFrame frame, @NonNull GrabCutHelper helper){
        this.mFrame = frame;
        this.mHelper = helper;
        this.forePoint = new LinkedList<>();
        this.backgroundPoint = new LinkedList<>();
    }

    public void showImage(@NonNull Bitmap bitmap){
        mFrame.showImage(bitmap);
        this.bitmap =  bitmap;
        mHelper.init(bitmap);
    }

    public void setModel(GrabCutModel model){
        if(!isRunning){
            this.model = model;
            forePoint.clear();
            backgroundPoint.clear();
            mFrame.reSet(0);
            mFrame.reSet(1);
        }
    }

    @TargetApi(19)
    void canvasTouchModel(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            Point mPoint = new Point((int)event.getX(), (int)event.getY());
            if(model == GrabCutModel.HIGH_PRECISION){
                getBackgroundPoint(mPoint);
                mFrame.drawPointList(mPoint, model, Color.GRAY);
            }else if(model == GrabCutModel.LOW_PRECISION){
                getForePoint(mPoint);
                mFrame.drawPointList(mPoint, model, Color.GREEN);
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            if(model == GrabCutModel.HIGH_PRECISION){
                model = GrabCutModel.LOW_PRECISION;
            }else if(model == GrabCutModel.LOW_PRECISION){
                model = null;
            }
        }
    }

    public void grabCut(final GrabCutCallBack callBack){
        if(mHelper.isInit() && model == null){
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    isRunning = true;
                    if(mHelper.isInit()){
                        Rect rect = campImageRect(forePoint);
                        mHelper.controMaskScalar(rect);
                        mHelper.setBackGroundPoint(backgroundPoint, mFrame.getPointRadius());
                        mHelper.setForePoint(forePoint, mFrame.getPointRadius());
                        android.util.Log.e("GrabCut loader : ", "grabcut start .... ");
                        final Bitmap bitmap = mHelper.grabcut(rect);
                        android.util.Log.e("GrabCut loader : ", "grabcut was done .... ");
                        android.util.Log.e("GrabCut loader : ", "Rect = "+rect+" 用时： "+ SystemClock.currentThreadTimeMillis()+"ms");
                        osHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(callBack != null){
                                    callBack.done(bitmap);
                                }
                                mFrame.reSet(0);
                                mFrame.reSet(1);
                            }
                        });
                    }
                    isRunning = false;
                }
            });
        }
    }

    public void release(){
        mHelper.release();
        mFrame = null;
    }

    private void getForePoint(Point point){
        if(point != null){
            this.forePoint.add(point);
        }
    }

    public void getBackgroundPoint(Point point){
        if(point != null){
            this.backgroundPoint.add(point);
        }
    }

    private Rect campImageRect(@NonNull List<Point> mPointList){
        int[] m4Point = new int[4];
        for(Point mPoint : mPointList){
            if(m4Point[0] == 0 || m4Point[1] == 0){
                m4Point[0] = mPoint.x;
                m4Point[1] = mPoint.y;
            }
            if(m4Point[0] > mPoint.x){
                m4Point[0] = mPoint.x;
            }
            if(m4Point[1] > mPoint.y){
                m4Point[1] = mPoint.y;
            }
            if(m4Point[2] < mPoint.x){
                m4Point[2] = mPoint.x;
            }
            if(m4Point[3] < mPoint.y){
                m4Point[3] = mPoint.y;
            }
        }
        return new Rect(m4Point[0], m4Point[1], m4Point[2], m4Point[3]);
    }

    public interface GrabCutCallBack{
        public void done(@NonNull Bitmap result);
    }
}
