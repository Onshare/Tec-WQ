package com.wq.tec.frame.render;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wq.tec.R;
import com.wq.tec.frame.render.beauty.BeautyFragment;
import com.wq.tec.frame.render.cut.CutFrgment;
import com.wq.tec.frame.render.filter.FilterFragment;
import com.wq.tec.frame.render.lbeauty.LBeautyFragment;
import com.wq.tec.frame.render.slr.SLRFragment;

/**
 * Created by N on 2017/3/15.
 */

public class RenderFragment extends RenderBaseFragment implements View.OnClickListener{

    View slr, filter, beauty, mbeauty, cut;

    @Override
    protected void initParams() {

    }

    @NonNull
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_render, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar(view);

        slr = view.findViewById(R.id.render_slr);
        filter = view.findViewById(R.id.render_filter);
        beauty = view.findViewById(R.id.render_beauty);
        mbeauty = view.findViewById(R.id.render_mbeauty);
        cut = view.findViewById(R.id.render_cut);

        slr.setOnClickListener(this);
        filter.setOnClickListener(this);
        beauty.setOnClickListener(this);
        mbeauty.setOnClickListener(this);
        cut.setOnClickListener(this);
    }

    void initActionBar(View view){
        ((TextView)view.findViewById(R.id.actiontitle)).setText("渲染");
        ((TextView)view.findViewById(R.id.actionsure_text)).setText("保存");
        view.findViewById(R.id.actionsure).setVisibility(View.GONE);
        view.findViewById(R.id.actionsure_text).setVisibility(View.VISIBLE);

        view.findViewById(R.id.actionsure_text).setOnClickListener(this);
        view.findViewById(R.id.actionback).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onAction(v);
        onControl(v);
    }

    private void onAction(View v){
        switch (v.getId()){
            case R.id.actionback:
                this.getActivity().finish();
                break;
            case R.id.actionsure_text://todo 保存图像
                break;
        }
    }

    private void onControl(View v){
        switch (v.getId()){
            case R.id.render_slr:
                getRenderActivity().replaceFragments(new SLRFragment(), getId(), false);
                break;
            case R.id.render_filter:
                getRenderActivity().replaceFragments(new FilterFragment(), getId(), false);
                break;
            case R.id.render_beauty:
                getRenderActivity().replaceFragments(new BeautyFragment(), getId(), false);
                break;
            case R.id.render_mbeauty:
                getRenderActivity().replaceFragments(new LBeautyFragment(), getId(), false);
                break;
            case R.id.render_cut:
                getRenderActivity().replaceFragments(new CutFrgment(), getId(), false);
                break;
        }
    }

}
