package com.lgu.ccss.sms.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lgu.ccss.sms.constant.SmsGWConst;
import com.lgu.ccss.sms.service.sms.SmsService;
import com.lgu.ccss.sms.service.status.StatusInitService;

@Service
public class SmsScheduler {
	private final Logger logger = LoggerFactory.getLogger(SmsScheduler.class);

	@Autowired
	private SmsService smsService;
	@Autowired
	private StatusInitService statusInitService;
	
	boolean isFirst = true;
	//0 0/5 14,18 * * ?
	@Scheduled(fixedRateString  = "${delay.time}")
	public void startWork() {
		try {

			logger.info("###### START SMS DAEMON #####");
			if(isFirst) {
				logger.debug("FirstTime!! SMS Data Status Init!");
				statusInitService.doTask();
				logger.debug("SMS Message Data Init! Success!!");
				SmsGWConst.retryCount.getExpirer().startExpiring();
				SmsGWConst.deliverCount.getExpirer().startExpiring();
				SmsGWConst.smsVo.getExpirer().startExpiring();
				SmsGWConst.tloMap.getExpirer().startExpiring();
				
				isFirst = false;
			}
			smsService.doTask();

		} catch (Exception e) {
			logger.error("{}", e);

		} finally {
			logger.info("###### END SMS DAEMON #####");
		}
	}
}
