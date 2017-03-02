package com.wq.tec.frame.guid;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.jazz.libs.controller.BasePresent;
import com.jazz.libs.event.ActionDisPatcher;
import com.jazz.libs.event.ActionResponse;
import com.jazz.libs.server.HttpMethod;
import com.jazz.libs.server.Net;
import com.jazz.libs.server.request.StringRequire;
import com.jazz.libs.termination.TerminationTask;
import com.wq.tec.server.ServerConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2017/3/2.
 */

public class GuidPresenter extends BasePresent<GuidActivity> {

    final List<GuidRecord> meRecord = new ArrayList<>();
    final List<GuidRecord> humRecord = new ArrayList<>();
    final List<GuidRecord> sportRecord = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState, GuidActivity activity) {
        super.onCreate(savedInstanceState, activity);
        ActionDisPatcher.get().registEvent(ServerConstant.URL_SERVER_METHOD_GUID_PICS, new ActionResponse(guidTermination));
        Net.callServer(new StringRequire(ServerConstant.URL_SERVER_METHOD_GUID_PICS, HttpMethod.GET, null));
    }

    private TerminationTask<String> guidTermination = new TerminationTask<String>() {
        @Override
        public void onTermination(String tag, String object) {
            if(ServerConstant.URL_SERVER_METHOD_GUID_PICS.equals(tag)){
                parseGuid(object);
            }
        }

        @Override
        public void onTerminationfailure(String tag, byte[] content, Throwable e) {
            getActivity().log("网络连接错误");
        }
    };

    private void parseGuid(String result){
        try {
            JSONObject json = new JSONObject(result);
            if("0".equals(json.getString("result"))){
                JSONObject jsonData = json.getJSONObject("msg");
                if(jsonData.has("person")){
                    humRecord.clear();
                    humRecord.addAll(setGuidList(jsonData.getJSONArray("person")));
                }
                if(jsonData.has("selfie")){
                    meRecord.clear();
                    meRecord.addAll(setGuidList(jsonData.getJSONArray("selfie")));
                }
                if(jsonData.has("clever")){
                    sportRecord.clear();
                    sportRecord.addAll(setGuidList(jsonData.getJSONArray("clever")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<GuidRecord> setGuidList(@NonNull JSONArray jarry) throws JSONException{
        List<GuidRecord> records = new ArrayList<>();
        for(int index = 0; index < jarry.length(); index++){
            records.add(new Gson().fromJson(jarry.getJSONObject(index).toString(), GuidRecord.class));
        }
        return records;
    };
}
