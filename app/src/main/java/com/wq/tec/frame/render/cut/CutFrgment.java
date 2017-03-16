package com.wq.tec.frame.render.cut;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jazz.libs.util.BitmapUtil;
import com.wq.tec.R;
import com.wq.tec.frame.render.RenderBaseFragment;
import com.wq.tec.frame.render.RenderFragment;
import com.wq.tec.util.FileCacheUtil;

/**
 * Created by N on 2017/3/15.
 */

public class CutFrgment extends RenderBaseFragment {

    static final String CUT_PATH = FileCacheUtil.getCachePath()+"wq_cut_temp.png";
    public static final String IMAGE_UNSPECIFIED = "image/*";

    @Override
    protected void initParams() {
        BitmapUtil.saveToFile(getRenderActivity().getDstBitmap(), CUT_PATH);
    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cut, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startPhotoZoom(Uri.parse("file://"+CUT_PATH));
    }

    //缩放图片
    public void startPhotoZoom(Uri uri) {
        android.util.Log.e("CUT", uri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 10011);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10011 && data != null){
            doResult(data);
        }
    }

    private void doResult(@NonNull Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null && extras.getParcelable("data") != null) {
            Bitmap bitmapResult = extras.getParcelable("data");
            getRenderActivity().setBitmapResource(bitmapResult);
            getRenderActivity().replaceFragments(new RenderFragment(), getId(), false);
            getRenderActivity().invalidate(getRenderActivity().getDstBitmap());
        }

    }
}
