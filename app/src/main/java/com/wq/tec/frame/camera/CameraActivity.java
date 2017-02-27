package com.wq.tec.frame.camera;

import android.os.Bundle;

import com.wq.tec.R;
import com.wq.tec.WQActivity;

/**
 * Created by NoName on 2017/1/19.
 */

public class CameraActivity extends WQActivity {

    @Override
    protected boolean isSupportCheckPermission() {
        return true;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        addCameraFrame();
    }

    void addCameraFrame(){
        addFragment(new com.wq.tec.tech.camera.CameraFrame(), R.id.camera_frame);
    }
}
