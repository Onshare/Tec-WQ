package com.wq.tec.open;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;

/**
 * Created by N on 2017/3/13.
 */

public class CanvasView extends ImageView {

    public CanvasView(Context context) {
        super(context);
        initCanvas();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCanvas();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCanvas();
    }

    public interface Model{
        public static final String ADD = "CANVAS_ADD", DEL = "CANVAS_DEL", STOP = "CANVAS_STOP";
    }

    public void delLast(){
        Path path = arraysPath.size() > 0 ? arraysPath.removeLast() : null;
        int color = arraysColor.size() > 0 ? arraysColor.removeLast() : -1;
//        log(path + " "+color);
        if(path != null){
            delPath.add(path);
            delColor.add(color);
            invalidate();
        }
    }

    public void addDelLast(){
        Path path = delPath.size() > 0 ? delPath.removeLast() : null;
        Integer color = delColor.size() > 0 ? delColor.removeLast() : -1;
//        log(path + " "+color);
        if(path != null && color != null){
            arraysPath.add(path);
            arraysColor.add(color);
            invalidate();
        }
    }

    public void setModel(String model){
        this.model = model;
    }

    public void reSet(){
        arraysPath.clear();
        arraysColor.clear();
        delPath.clear();
        delColor.clear();
        invalidate();
        mPath = null;
        model = null;
    }

    private String model;

    private Path mPath ;
    private LinkedList<Path> arraysPath = new LinkedList<>();
    private LinkedList<Integer> arraysColor = new LinkedList<>();

    private LinkedList<Path> delPath = new LinkedList<>();
    private LinkedList<Integer> delColor = new LinkedList<>();

    private Xfermode[] xfer = new Xfermode[]{new PorterDuffXfermode(PorterDuff.Mode.SRC_IN), new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)};
    private int dip = 3;

    private Paint mPaint = null;

    private void initCanvas(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getResources().getDisplayMetrics().density * 12 + 0.5F);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        dip = (int)(getResources().getDisplayMetrics().density + 0.5);
//        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    public Bitmap createBitmap(){
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap cache = getDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Paint bp = new Paint(mPaint);
        Canvas canvas = new Canvas(bitmap);
        float[] colorMax = new float[]{
                1.0F, 0, 0, 0, 0,
                0, 1.0F, 0, 0, 0,
                0, 0, 1.0F, 0, 0,
                0, 0, 0, 255.0F, 0,
        };
        bp.setColorFilter(new ColorMatrixColorFilter(colorMax));
        canvas.drawBitmap(cache, null, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), bp);
        canvas.save();

        Bitmap bitmap2 = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap2);

        if(arraysPath.size() > 0 && arraysPath.size() == arraysColor.size()){
            for(int i = 0; i < arraysPath.size(); i++){
                Path path = arraysPath.get(i);
                int color = arraysColor.get(i);
                mPaint.setXfermode(color == Color.TRANSPARENT ? xfer[0] : xfer[1]);
                mPaint.setColor(color == Color.TRANSPARENT ? Color.TRANSPARENT : Color.argb(0xFF, color >>> 16 & 0xFF, color >>> 8 & 0xFF, color & 0xFF));
                canvas2.drawPath(path, mPaint);
            }
        }

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int color = getPathColor();
        for(int i = 0; i < arraysPath.size(); i++){
            Path path = arraysPath.get(i);
            color = arraysColor.get(i);
            mPaint.setXfermode(color == Color.TRANSPARENT ? xfer[0] : xfer[1]);
            mPaint.setColor(color);
            canvas.drawPath(path, mPaint);
        }
        if(mPath != null){
            mPaint.setXfermode(color == Color.TRANSPARENT ? xfer[0] : xfer[1]);
            mPaint.setColor(getPathColor());
            canvas.drawPath(mPath, mPaint);
        }
    }

    Point prevPoint = null;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isLoop = !super.onTouchEvent(event) && (Model.ADD.equals(model) || Model.DEL.equals(model));
        if(isLoop){
            int action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    Point point = new Point((int)event.getX(), (int)event.getY());
                    if(prevPoint == null || sumDistance(prevPoint, point) > dip){
                        if(mPath == null){
                            mPath = new Path();
                            mPath.moveTo(point.x, point.y);
                        }else{
                            mPath.lineTo(point.x, point.y);
                        }
                        invalidate();
                        prevPoint = point;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if(mPath != null){
                        arraysPath.add(mPath);
                        arraysColor.add(getPathColor());
                    }
                    mPath = null;
                    prevPoint = null;
                    invalidate();
                    break;
            }
        }
        return isLoop ;
    }

    private int getPathColor(){
        return Model.ADD.equals(this.model) ? Color.parseColor("#9A4876FF") : Color.TRANSPARENT;
    }

    private int sumDistance(Point prevPoint, Point nextPoint){//
        if(prevPoint != null && nextPoint != null){
            int absx = absNumber(prevPoint.x , nextPoint.x);
            int absy = absNumber(prevPoint.y, nextPoint.y);
            return (int)Math.sqrt(absx + absy);
        }
        return 0;
    }

    private int absNumber(int i1, int i2){
        return Math.abs(i1 - i2);
    }
}
