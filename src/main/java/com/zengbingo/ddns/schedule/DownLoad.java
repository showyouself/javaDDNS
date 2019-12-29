package com.zengbingo.ddns.schedule;

import com.zengbingo.ddns.util.DownLoadUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DownLoad {

    /**
     * 获取预览页面的所有链接
     * @param url
     * @return
     */
    public List<String> getUrlList(String url){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        List<String> urls = new ArrayList<>();
        try {
            Response response = call.execute();
            //data-src="https://pervclips.com/tube/get_file/7/39f9dd0ff6b9e52496ac441fd1fee116/1063249000/1063249109/1063249109_preview.mp4/"

            String result = response.body().string();
            int start = 0;
            int end = 0;
            while (result.indexOf("https://www.pervclips.com/tube/video") > 0){
                start = result.indexOf("https://www.pervclips.com/tube/video");
                end = start + result.substring(start).indexOf("\"");
                urls.add(result.substring(start, end));
                result = result.substring(end);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * 获取单个页面的下载链接
     * @param pageUrl
     * @return
     */
    public String getDownloadUrl(String pageUrl){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(pageUrl)
                .build();
        final Call call = okHttpClient.newCall(request);
        String result = "";
        try {
            Response response = call.execute();
            //data-src="https://pervclips.com/tube/get_file/7/39f9dd0ff6b9e52496ac441fd1fee116/1063249000/1063249109/1063249109_preview.mp4/"
            result = response.body().string();

            int start = result.indexOf("https://pervclips.com/tube/get_file/");
            int end = start + result.substring(start).indexOf("mp4") + 3;
            result = result.substring(start, end);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 开始下载
     * @param pageUrl
     * @throws Exception
     */
    @Async
    public void startDownLoad(String pageUrl, String localFilePath) throws Exception{
        String url = getDownloadUrl(pageUrl);
        Long size = DownLoadUtil.getFileLength(url);
        System.out.println("开始下载：" + url);
        System.out.println("完整大小" + DownLoadUtil.getNetFileSizeDescription(size));
        String fileName = url.split("/")[url.split("/").length - 1];
        DownLoadUtil.download(url, new RandomAccessFile(localFilePath + fileName, "rw"), size);
        System.out.println("下载完成：" + url);
    }
}
