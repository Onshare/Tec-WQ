package com.wq.tec;

import com.baidu.mobstat.StatService;
import com.jazz.libs.controller.BaseActivity;
import com.jazz.libs.controller.BasePresent;

public abstract class WQActivity<T extends BasePresent> extends BaseActivity<T> {

    protected String TAG = getClass().getName();

    public void log(Object object){
        android.util.Log.e(TAG, "" + object);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}
