package com.zengbingo.ddns.controller;

import com.zengbingo.ddns.schedule.DownLoad;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("movies/")
@RestController
public class Movies {

    @Resource
    private DownLoad downLoad;

    @GetMapping("startDownload")
    public String run(String url) throws Exception{
//        String url ="https://www.pervclips.com/tube/categories/japanese/?is_hd=1";

        String localFilePath = "/home/ftp/movies/";
//        String localFilePath = "";
        List<String> urls = downLoad.getUrlList(url);
        for (String s : urls) {
            downLoad.startDownLoad(s, localFilePath);
        }
        return urls.toString();
    }
}
