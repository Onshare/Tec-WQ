package com.wq.tec.open;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2017/3/10.
 */

public class ClipControllerView extends ImageView {

    public ClipControllerView(Context context) {
        super(context);
    }

    public ClipControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<Path> mSelectPathList = new ArrayList<>();
    private List<Path> mCancelPathList = new ArrayList<>();
    private Paint mPaint = null;

    public void invalidate(@NonNull List<Path> mList1, @NonNull List<Path> mList2, @NonNull Paint paint){
        mSelectPathList.clear();
        mCancelPathList.clear();
        this.mSelectPathList.addAll(mList1);
        this.mCancelPathList.addAll(mList2);
        this.mPaint = paint;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        for(Path mP : mSelectPathList){
//            if(mP != null && mPaint != null){
//                canvas.drawPath(mP, mPaint);
//            }
//        }
//        for(Path mP : mCancelPathList){
//            if(mP != null && mPaint != null){
//                canvas.drawPath(mP, mPaint);
//            }
//        }
    }
}
