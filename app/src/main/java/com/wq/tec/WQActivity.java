package com.wq.tec;

import com.baidu.mobstat.StatService;
import com.jazz.libs.controller.BaseActivity;

public abstract class WQActivity extends BaseActivity {

    protected String TAG = getClass().getName();

    protected void log(Object object){
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
