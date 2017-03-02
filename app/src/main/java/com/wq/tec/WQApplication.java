package com.wq.tec;

import com.jazz.libs.controller.BaseApplication;
import com.jazz.libs.server.Net;
import com.wq.tec.server.NetFile;
import com.wq.tec.util.FileCacheUtil;

/**
 * Created by NoName on 2017/1/16.
 */

public class WQApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Net.registUrlSwticher(new NetFile());
        FileCacheUtil.init();
    }


}
