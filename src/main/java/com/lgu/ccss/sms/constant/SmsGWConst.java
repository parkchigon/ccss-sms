package com.lgu.ccss.sms.constant;

import java.util.Map;

import com.lgu.ccss.common.collection.ExpiringMap;
import com.lgu.ccss.sms.model.SmsVO;

public class SmsGWConst
{
	public static int SMS_QUEUE_SIZE = 1000;
	public static ExpiringMap<Integer, Integer> retryCount = new ExpiringMap<Integer, Integer>(30);
	public static ExpiringMap<Integer, Integer> deliverCount = new ExpiringMap<Integer, Integer>();
	public static ExpiringMap<Integer, SmsVO> smsVo = new ExpiringMap<Integer, SmsVO>(15);
	public static ExpiringMap<String, Map<String, String>> tloMap = new ExpiringMap<String, Map<String, String>>(60*60*24*3);
}
