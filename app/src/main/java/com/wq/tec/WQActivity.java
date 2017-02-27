package com.wq.tec;

import com.jazz.libs.controller.BaseActivity;

public abstract class WQActivity extends BaseActivity {

    protected String TAG = getClass().getName();

    protected void log(Object object){
        android.util.Log.e(TAG, "" + object);
    }
}
