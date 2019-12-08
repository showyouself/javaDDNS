package com.zengbingo.ddns.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class AliUtil {

    private static IAcsClient client = null;

    @Value("${com.zengbingo.aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${com.zengbingo.aliyun.accessKeySecret}")
    private String accessKeySecret;

    @PostConstruct
    public void con(){
        IClientProfile profile = DefaultProfile.getProfile("c-shenzhen", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
    }

    public static String queryRecordId(){
        CommonRequest request = new CommonRequest();
        request.setSysDomain("alidns.aliyuncs.com");
        request.setSysAction("DescribeDomainRecords");
        request.setSysVersion("2015-01-09");
        request.putQueryParameter("DomainName", "zengbingo.com");
        CommonResponse response;
        String RecordId = "";
        try {
            response = client.getCommonResponse(request);
            String result = response.getData();
            JSONObject jsonObject = JSONObject.parseObject(result);
            for (Object o : jsonObject.getJSONObject("DomainRecords").getJSONArray("Record")) {
                JSONObject Record = (JSONObject) JSON.toJSON(o);
                if (("blog".equals(Record.getString("RR")))){
                    return Record.getString("RecordId");
                }
            }
            System.out.println(RecordId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getNowIp() {
        String url = "https://ip.cn/";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "curl/7.64.1")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            return jsonObject.getString("ip");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Boolean updateRecordIp(String recordId, String Value){
        CommonRequest request = new CommonRequest();
        request.setSysDomain("alidns.aliyuncs.com");
        request.setSysAction("UpdateDomainRecord");
        request.setSysVersion("2015-01-09");
        request.putQueryParameter("RecordId", recordId);
        request.putQueryParameter("RR", "blog");
        request.putQueryParameter("Type", "A");
        request.putQueryParameter("Value", Value);
        try {
            client.getCommonResponse(request);
            return true;
        }
        catch (ClientException e){
            if (e.getErrCode().equals("DomainRecordDuplicate")){
                //已重复更新
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
