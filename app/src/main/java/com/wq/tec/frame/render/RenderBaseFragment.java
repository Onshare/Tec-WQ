package com.wq.tec.frame.render;

import com.wq.tec.WQFragment;

/**
 * Created by N on 2017/3/15.
 */

public abstract class RenderBaseFragment extends WQFragment {

    protected final RenderActivity getRenderActivity(){
        return (RenderActivity) super.getActivity();
    }

}
