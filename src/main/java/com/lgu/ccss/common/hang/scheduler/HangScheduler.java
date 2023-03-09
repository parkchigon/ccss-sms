package com.lgu.ccss.common.hang.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lgu.ccss.common.hang.service.HangService;

@Service
public class HangScheduler {
	private final Logger logger = LoggerFactory.getLogger(HangScheduler.class);

	@Autowired
	private HangService hangService;
	
	//@Scheduled(fixedRate=300000)
	
	//0 0/5 14,18 * * ?
	@Scheduled(fixedDelay=60000)
	public void startWork() {

		try {
			logger.info("###### START HANG DAEMON #####");
				
			hangService.doTask();

		} catch (Exception e) {
			logger.error("{}", e);

		} finally {
			logger.info("###### END HANG DAEMON #####");
		}

		// logger.info("$$$$$$$" +
		// CommMessageUtil.getMessage("result_success"));
	}
	
	
}
