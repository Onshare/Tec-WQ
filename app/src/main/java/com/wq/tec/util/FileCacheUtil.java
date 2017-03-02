package com.wq.tec.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by N on 2017/3/2.
 */

public final class FileCacheUtil {

    private static final String URL_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + ".WQ/";

    public static String getCachePath(){
        return URL_PATH;
    }

    public static void init(){
        File mFile = new File(URL_PATH);
        if(!mFile.exists()){
            mFile.mkdirs();
        }
    }

    public static boolean checkExist(String mFileName){
        if(mFileName != null){
            File mFile = new File(URL_PATH + mFileName);
            return mFile.exists();
        }
        return false;
    }

}
