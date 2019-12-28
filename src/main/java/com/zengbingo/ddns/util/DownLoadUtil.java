package com.zengbingo.ddns.util;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

public class DownLoadUtil {
    /**
     * 获取网络文件大小
     */
    public static Long getFileLength(String downloadUrl) throws IOException {
        if (downloadUrl == null || "".equals(downloadUrl)) {
            return 0L;
        }
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows 7; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 YNoteCef/5.8.0.1 (Windows)");
            return (long) conn.getContentLength();
        } catch (IOException e) {
            return 0L;
        } finally {
            conn.disconnect();
        }
    }

    public static String getNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        }
        else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            }
            else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    /**
     * 通过视频的URL下载该视频并存入本地
     *
     * @param ur     视频的URL
     * @param destFile 视频存入的文件夹
     * @throws IOException
     */
    public static void download(String ur, RandomAccessFile destFile, Long size) throws IOException {
        URL url = new URL(ur);
        Long startIndex = destFile.length();
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        httpConnection.setRequestProperty("Content-Range","bytes "+startIndex+"-"+ (size - 1) + "/" + size);
        InputStream input = httpConnection.getInputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        long startTime = System.currentTimeMillis();
        long totalKB = 0L;
        while ((-1) != (len = input.read(buffer))) {
            totalKB++;
            if (totalKB%100 == 0) {
                long endTime = System.currentTimeMillis();
                long diff = (endTime - startTime)/1000L;
                if ( diff > 0 ){
//                    System.out.println(100/diff + "KB/s");
                    startTime = endTime;
                }
            }

            destFile.write(buffer, 0, len);
        }
        destFile.close();

        if (null != input) {
            input.close();
        }
    }

    /**
     * 链接url 返回字节流
     *
     * @param url
     * @return
     * @throws IOException
     * @throws ProtocolException
     * @throws UnsupportedEncodingException
     */
    public static BufferedReader connectURL(URL url)
            throws IOException, ProtocolException, UnsupportedEncodingException {
        // 这里的代理服务器端口号 需要自己配置
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7959));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
        // 若遇到反爬机制则使用该方法将程序伪装为浏览器进行访问
        conn.setRequestMethod("GET");
        conn.setRequestProperty("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        return br;
    }
}
