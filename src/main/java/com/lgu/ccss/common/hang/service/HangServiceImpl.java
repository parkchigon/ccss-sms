package com.lgu.ccss.common.hang.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class HangServiceImpl implements HangService{
	private final Logger hangLogger = LoggerFactory.getLogger("HANGLogger");
	
	@Value("#{config['app.hang.id']}")
	private String appHangID;
	
	
	public void doTask() {
		hangLogger.info(appHangID + " health check : alived");
	}
}
