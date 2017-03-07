package com.wq.tec.util;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.jazz.libs.util.DensityUtils;
import com.wq.tec.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by N on 2016/12/13.
 */
public final class Loader {

    private static Map<Integer, DialogFragment> loaders = new HashMap<>();

    public static void makeLoding(FragmentActivity fragmentActivity, int position){
        if(!loaders.containsKey(position)){
            loaders.put(position, new DefaultLoader());
        }else {
            if(loaders.get(position).getContext() != fragmentActivity){
                loaders.get(position).dismissAllowingStateLoss();
                loaders.put(position, new DefaultLoader());
            }
        }
        DialogFragment dialog = loaders.get(position);
        dialog.show(fragmentActivity.getSupportFragmentManager(), dialog.getClass().getName());
    }

    public static void stopLoading(int position){
        DialogFragment fragment = loaders.get(position);
        if(fragment != null){
            fragment.dismissAllowingStateLoss();
        }
    }


    public static class DefaultLoader extends DialogFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            View contentView = new View(getContext());
            contentView.setBackgroundResource(R.mipmap.app_load);
            return contentView;
        }

        @Override
        public void onResume() {
            super.onResume();
            getDialog().setCanceledOnTouchOutside(false);
            WindowManager.LayoutParams win = getDialog().getWindow().getAttributes();
            win.width = DensityUtils.dp2px(getContext(), 16);
            win.height = DensityUtils.dp2px(getContext(), 16);
            win.dimAmount = 0.15F;
            win.gravity = Gravity.CENTER;
            getDialog().getWindow().setAttributes(win);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if(getView() != null){
                ObjectAnimator anim = ObjectAnimator.ofFloat(getView(), "rotation", 0F, 360F);
                anim.setDuration(500);
                anim.setRepeatCount(-1);
                anim.setInterpolator(new LinearInterpolator());
                anim.start();
            }
        }
    }
}
