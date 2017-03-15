package com.wq.tec.frame.render;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.wq.tec.R;
import com.wq.tec.WQActivity;

/**
 * Created by N on 2017/3/15.
 */

public class RenderActivity extends WQActivity<RenderPresenter> {


    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_render);
        addFragment(new RenderFragment(), R.id.render_frame);
        ((ImageView)findViewById(R.id.render_img)).setImageBitmap(mPresent.getBitmap());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void invalidate(@NonNull Bitmap bitmap){
        ((ImageView)findViewById(R.id.render_img)).setImageBitmap(bitmap);
    }

    public Bitmap getDstBitmap(){
        return mPresent.getBitmap();
    }

    public void setBitmapResource(@NonNull Bitmap bitmap){
        mPresent.setBitmap(bitmap);
    }
}
