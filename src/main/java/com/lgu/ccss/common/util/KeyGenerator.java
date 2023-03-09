package com.lgu.ccss.common.util;

import java.util.Random;

public class KeyGenerator {
	public final static int SMS_KEY_MIN_VALUE = 100000;
	public final static int SMS_KEY_MAX_VALUE = 900000;
	
	public static String createSmsMagId(String svrId,String instanceId ,int minValue,int maxValue) throws InterruptedException{
		String msgId;
		Random rn = new Random();
		Thread.sleep(100);
		rn.setSeed(System.currentTimeMillis());
		int randomKey = Math.abs(rn.nextInt(maxValue)+minValue);
		msgId= svrId+instanceId + String.valueOf(randomKey);
		return msgId;
	}
}
