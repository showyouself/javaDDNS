package com.zengbingo.ddns.schedule;

import com.aliyuncs.utils.StringUtils;
import com.zengbingo.ddns.util.AliUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class DDNS {

    private String oldIp = "-1";

    @Scheduled(cron = "* */10 * * * ?")
    public void run(){
        String ip = AliUtil.getNowIp();
        String recordId = AliUtil.queryRecordId();
        log.info("params is ip :{}, recordId :{}", ip, recordId);

        if (!oldIp.equals("-1") && oldIp.equals(ip)) {
            log.info("ip eq {}", ip);
            return;
        }

        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(recordId)){
            log.info("params is empty");
            return;
        }

        log.info("update ip {}", ip);
        if (AliUtil.updateRecordIp(recordId, ip)){
            oldIp = ip;
        }
    }
}
