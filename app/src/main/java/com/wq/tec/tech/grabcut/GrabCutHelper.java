package com.wq.tec.tech.grabcut;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by N on 2017/2/8.
 */
public class GrabCutHelper {

    private android.content.Context mContext;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mContext) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i("OpenCV", "OpenCV loaded successfully");
                break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    private static final String TAG = " grabCut cut : ";

    Mat src = null;
    Mat mast = null;

    private boolean isInit = false;

    GrabCutHelper(@NonNull android.content.Context context){
        mContext = context;
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, mContext, mLoaderCallback);
        } else {
            Log.e("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    boolean isInit(){
        return isInit;
    }

    void init(@NonNull Bitmap bitmap){
        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        mast = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, src);
        isInit = true;
    }

    void controMaskScalar(@NonNull Rect rect){
        if(mast != null){
            try {
                org.opencv.core.Rect rect1= rectSwitch(mast, rect);
                mast.submat(rect1).setTo(new Scalar(Imgproc.GC_PR_FGD));
            } catch (Exception e) {
            }
        }
    }

    void setForePoint(@NonNull List<Point> mPointList, int radius){
        int row = mast.rows();//行
        int col = mast.cols();
        for(Point mPoint : mPointList){
            if(mPoint.x <= row && mPoint.y <= col){
                Imgproc.circle(mast, new org.opencv.core.Point(mPoint.x, mPoint.y), radius < 1 ? 1 : radius, Scalar.all(Imgproc.GC_FGD));
            }
        }
    }

    void setBackGroundPoint(@NonNull List<Point> mPointList, int radius){
        int row = mast.rows();//行
        int col = mast.cols();
        for(Point mPoint : mPointList){
            if(mPoint.x <= row && mPoint.y <= col){
                Imgproc.circle(mast, new org.opencv.core.Point(mPoint.x, mPoint.y), radius < 1 ? 1 : radius, Scalar.all(Imgproc.GC_BGD));
            }
        }
    }

    private Mat switchRGB2ARGB(Mat resource){
        Imgproc.cvtColor(resource, resource, Imgproc.COLOR_BGR2BGRA);
        Mat result= new Mat(src.size(), CvType.CV_8UC4, new Scalar(0));
        resource.copyTo(result, mast);
        return result;
    }

    Bitmap grabcut(@NonNull Rect rect){
        if(!isInit()){
            Log.e(TAG, "this GrabCut must be init");
            return null;
        }
        Mat image = new Mat(src.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(src, image, Imgproc.COLOR_BGRA2BGR);

        Imgproc.grabCut(image, mast, rectSwitch(mast, rect), new Mat(), new Mat(), 1, Imgproc.GC_INIT_WITH_RECT);//

        Core.compare(mast, Scalar.all(Imgproc.GC_PR_FGD), mast,Core.CMP_EQ);

        Mat foreground= new Mat(image.size(), CvType.CV_8UC3, new Scalar(0));
        image.copyTo(foreground, mast);
//
        Bitmap result = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(switchRGB2ARGB(foreground), result, true);
        return result ;
    }

    void release(){
        releaseMat(src);
        releaseMat(mast);
        isInit = false;
        mContext = null;
    }

    org.opencv.core.Rect rectSwitch(@NonNull Mat mat, @NonNull Rect rect){
        int row = mat.rows();//行
        int col = mat.cols();//lie
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        int left =  rect.left * col / width;
        int top = rect.top * row / height;
        int rw = rect.width() * col / width;
        int rh = rect.height() * row / height;
        left = left < 0 ? 0 : left > col ? col : left;
        top = top < 0 ? 0 : top > row ? row : top;
        rw = left + rw > col ? col - left : rw;
        rh = top + rh > row ? row - top : rh;
        return new org.opencv.core.Rect(left, top, rw, rh);
    }

    private void releaseMat(Mat mat){
        if(mat != null){
            mat.release();
            mat = null;
        }
    }
}
