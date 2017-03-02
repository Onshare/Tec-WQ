package com.wq.tec.frame.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wq.tec.R;
import com.wq.tec.WQActivity;
import com.wq.tec.frame.home.HomeActivity;

/**
 * Created by NoName on 2017/1/19.
 */

public class StartActivity extends WQActivity implements Handler.Callback{

    private Handler mHandler = new Handler(this);

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        mHandler.sendEmptyMessageDelayed(-1, 1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        startActivity(new Intent(this, HomeActivity.class));
        this.finish();
        return true;
    }


}
