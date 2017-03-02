package com.wq.tec.server;

import com.jazz.libs.server.url.UrlSwtich;

import java.util.Map;

/**
 * Created by NoName on 2017/1/16.
 */

public class NetFile extends UrlSwtich{

    @Override
    public String getUrlHost() {
        return "";
    }

    @Override
    public void registUrlPath(Map<String, String> urlDevice) {
        urlDevice.put(ServerConstant.URL_SERVER_METHOD_GUID_PICS, "http://www.kkping.com/PCAPI/getGuidePic");
    }
}
