package com.wq.tec.util;

/**
 * Created by N on 2017/3/2.
 */

public class DataUtil {

    /* base64生成字符串 */
    public static String getBase64(byte[] byteContent) {
        String s = null;
        if (byteContent != null) {
            s = new String(android.util.Base64.encode(byteContent, android.util.Base64.DEFAULT));
        }
        return s;
    }

    /* base64生成byte[] */
    public static byte[] getFromBase64(String s) {
        byte[] byteContent = null;
        if (s != null) {
            try {
                byteContent = android.util.Base64.decode(s.getBytes(), android.util.Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byteContent;
    }
}
