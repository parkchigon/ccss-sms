package com.lgu.ccss.sms.service.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lgu.ccss.sms.mapper.SmsDao;
import com.lgu.common.tlo.TloWriter;

public class SmsSession {

	private final Logger logger = LoggerFactory.getLogger(SmsSession.class);
	SmsRCVThread smsRcvThread = null;
	
	SmsGWConnection smsGWConnection;
	SmsDao smsDao;
	TloWriter tloWriter;
	
	public SmsSession(SmsGWConnection smsGWConnection,SmsDao smsDao,TloWriter tloWriter) {
		// TODO Auto-generated constructor stub
		this.smsGWConnection = smsGWConnection;
		this.smsDao= smsDao;
		this.tloWriter=tloWriter;
	}

	public int start() {
		int count = 0;
		try	{
			if(smsRcvThread == null) {
				smsRcvThread = new SmsRCVThread(smsGWConnection,smsDao,tloWriter);
			}
			int connectCode = smsRcvThread.open(); 
			if(connectCode == 1)	{
//				count++;
				smsRcvThread.start();
			}
//			else if(connectCode == 2) {
//				count++;
//			}
			return connectCode;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.info("-[SMSGWSession] start Exception = ", e);
			return count;
		}
	}
	
	public synchronized boolean send(byte[] data){
		try	{	
			smsGWConnection.send(data);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
//			SmsGWConst.isConnected = false;
			return false;
		}
	}
	
}
