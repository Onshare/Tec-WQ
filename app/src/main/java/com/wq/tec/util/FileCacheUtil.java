package com.wq.tec.util;

import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.jazz.libs.termination.TerminationTask;
import com.jazz.libs.util.FileUtils;
import com.jazz.libs.util.ThreadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2017/3/2.
 */

public final class FileCacheUtil {

    private static final String URL_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + ".WQ/";

    public static final String WQ_SAVE_NAME = "WQ_SAVE_RESULT";

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

    public static String getSaveName() {
        return WQ_SAVE_NAME;
    }

    public static String getSaveURL(){
        return URL_PATH + WQ_SAVE_NAME + ".png";
    }

    public static boolean clearCache(){
        return FileUtils.clearFile(URL_PATH);
    }

    public static void mCursorPhotoAtFirst(@NonNull final android.content.Context context, @NonNull final TerminationTask<String> terminationTask){
        ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                String sdcardPath = Environment.getExternalStorageDirectory().getPath();
                Cursor mCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                        MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列

                if(mCursor != null ){
                    List<String> mediaPath = new ArrayList<>();
                    while (mCursor.moveToNext()) {
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (path != null && path.contains(sdcardPath + "/DCIM/")) {
                            mediaPath.add(path);
                        }
                    }
                    mCursor.close();
                    if(mediaPath.size() > 0){
                        terminationTask.onTermination("Cursor", mediaPath.get(0));
                    }
                }
            }
        });
    }

}
