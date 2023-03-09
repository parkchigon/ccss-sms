package com.lgu.ccss.sms.service.worker;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lgu.ccss.common.tlo.TloConst;
import com.lgu.ccss.common.tlo.TloData;
import com.lgu.ccss.common.util.ByteSmsgwUtil;
import com.lgu.ccss.common.util.KeyGenerator;
import com.lgu.ccss.sms.constant.SmsGWConst;
import com.lgu.ccss.sms.constant.SmsStatusConst;
import com.lgu.ccss.sms.mapper.SmsDao;
import com.lgu.ccss.sms.message.SMSGWMessageConst;
import com.lgu.ccss.sms.model.SmsVO;
import com.lgu.ccss.sms.service.sms.SmsServiceImpl;
import com.lgu.common.tlo.TloWriter;

public class SmsWorkerThread {
	//Bug patch: logger Name is missing
	private final static Logger logger = LoggerFactory.getLogger(SmsWorkerThread.class);
	
	public int threadID;
	static SmsDao smsDao;
	private RealWorker	worker;
	static int tid;
	String svrId;
	TloWriter tloWriter;
	
	
	public SmsWorkerThread (int threadid,SmsDao smsDao,int tid,String svrId,TloWriter tloWriter){
		threadID = threadid;
		SmsWorkerThread.tid = tid;
		SmsWorkerThread.smsDao = smsDao;
		this.svrId = svrId;
		this.tloWriter=tloWriter;
		worker	= new RealWorker();
		worker.start();
	}

	private class RealWorker extends Thread{
		public RealWorker(){
			setDaemon(true);
			setName("SMSGWWorkerThread-"+(threadID+100));
		}

		public void run(){
			try{
				while(true){
					SmsVO smsgwData= null;
					synchronized(SmsServiceImpl.msgVector){
						if(SmsServiceImpl.msgVector.size() > 0){
							smsgwData=(SmsVO)SmsServiceImpl.msgVector.get(0);
							SmsServiceImpl.msgVector.remove(0);
						}
					}
					
					if(smsgwData==null){
						sleep(300);
						continue;
					}
					logger.info("CALL run ======================> CALL RUN ");
//					if(smsDao.smsUpdateStatus(smsgwData,  SmsStatusConst.SMS_STATUS_CODE_SEND_ING)) {
//						logger.debug("SMS Status Update Fail");
//					}
					
					int threadId = (int)Thread.currentThread().getId()%100;
					String smsMsgId = KeyGenerator.createSmsMagId(svrId, Integer.toString(threadId), 100000, 900000);
					int msgId = Integer.parseInt(smsMsgId);
					SmsGWConst.smsVo.put(msgId, smsgwData);
					SmsGWConst.retryCount.put(msgId,smsgwData.getSendTryCnt());
					SmsGWConst.deliverCount.put(msgId, 1);
					//Bug patch: msgId is missing
					smsgwData.setMsgId(msgId+"");
					setTloData(smsgwData);
					
//					SmsGWConst.smsVo.getExpirer().
//					logger.debug("##################################");
//					logger.debug("retryCnt Mapping put id     : "+smsgwData.getMsgId());
//					logger.debug("retryCnt Mapping put value  : "+smsgwData.getSendTryCnt());
//					logger.debug("retryCnt Mapping containKey : "+SmsGWConst.retryCount.containsKey(smsgwData.getMsgId()));
//					
					sendEachSMS(msgId,smsgwData);
					logger.info("CALL run ======================> RUN FINISH");
					sleep(50);
					continue;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public static void setTloData(SmsVO smsgwData) {
		Map<String, String> tlo = new HashMap<String, String>();
		tlo.put(TloData.LOG_TYPE, "SVC");
		tlo.put(TloData.SID, smsgwData.getOrgNo());
		tlo.put(TloData.REQ_TIME, smsgwData.getRegDt());
		tlo.put(TloData.CARRIER_TYPE, TloConst.CARRIER_TYPE_ETC);
		tlo.put(TloData.SMS_SVC_CLASS, TloConst.SM01);
		tlo.put(TloData.SMS_REQ_TIME, TloData.getNowDate());
		
		SmsGWConst.tloMap.put(smsgwData.getMsgId(), tlo);
		
	}
	
	/*private void setTloData(SmsVO smsgwData,String resultCode) {
		
		if (smsgwData == null) {
			logger.debug("############# PUSH DATA VO NUll !!!!");
			return;
		}
		Map<String, String> tlo = (Map<String, String>) SmsGWConst.tloMap.get(smsgwData.getMsgId());
		String statusCode;
		
		if(resultCode.equals(SmsStatusConst.SMS_STATUS_CODE_SEND_SUCCESS)) {
			statusCode = SmsStatusConst.SMS_RESULT_CODE_SEND_SUCCESS;
		}else {
			statusCode = SmsStatusConst.SMS_RESULT_CODE_SEND_FAIL;
		}
		
		tlo.put(TloData.PUSH_RES_TIME, TloData.getNowDate());
		tlo.put(TloData.RESULT_CODE, statusCode);
		tlo.put(TloData.RSP_TIME, TloData.getNowDate());
		tlo.put(TloData.PUSH_RESULT_CODE, resultCode);
		
		logger.debug("tlo : "+SmsGWConst.tloMap.get(smsgwData.getMsgId())+"tlocheck : "+SmsGWConst.tloMap.containsKey(smsgwData.getMsgId())+"TloData.getNowDate() : "+TloData.getNowDate() + "| statusCode : "+statusCode + " | TloData.getNowDate() : "+TloData.getNowDate() + " | resultCode : "+resultCode );
	
		TloUtil.setTloData(tlo);
		tloWriter.write(tlo);
		
		SmsGWConst.tloMap.remove(smsgwData.getMsgId());
		logger.debug("############# TLO WRITE  DONE !!!!");
	}*/
	public static void sendSMSGW(int smsMsgId,SmsVO smsVo)
	{
		try
		{
			int deliverCnt = (int) SmsGWConst.deliverCount.get(smsMsgId);
			if(deliverCnt < 3) {
				SmsGWConst.deliverCount.put(smsMsgId, deliverCnt+1);
				sendEachSMS(smsMsgId,smsVo);
			}else {
				smsDao.smsProcessFinish(smsVo, SmsStatusConst.SMS_STATUS_CODE_SEND_FAIL);
				SmsGWConst.smsVo.remove(smsMsgId);
				SmsGWConst.retryCount.remove(smsMsgId);
				SmsGWConst.deliverCount.remove(smsMsgId);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Exception",e);
		}
	}
	
	public static void sendEachSMS(int smsMsgId,SmsVO smsVo){
		logger.info( "sendSMSGW Deliver Message Start");
		try	{
			if(logger.isDebugEnabled())
				logger.debug("Test : "+smsVo.toString());
			
			logger.debug("########## smsVo.getMsgCont().getBytes() : " +smsVo.getMsgCont().getBytes());
			byte packet[] = new byte[SMSGWMessageConst.HEADER_LENGTH + SMSGWMessageConst.SMSGW_DELIVER_BODYLENGTH];
			int offset =0;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.DELIVER);
			offset+=SMSGWMessageConst.SMSGW_TYPE_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.SMSGW_DELIVER_BODYLENGTH);
			offset+=SMSGWMessageConst.SMSGW_LENGTH_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, tid);
			offset+=SMSGWMessageConst.TID_LENGTH;
			ByteSmsgwUtil.makeBytesMsg(packet, offset, smsVo.getOrgNo().getBytes(), SMSGWMessageConst.ORGADDR_LENGTH);
			offset+=SMSGWMessageConst.ORGADDR_LENGTH;
			ByteSmsgwUtil.makeBytesMsg(packet, offset, smsVo.getRecvPhoneNo().getBytes(), SMSGWMessageConst.DSTADDR_LENGTH);
			offset+=SMSGWMessageConst.DSTADDR_LENGTH;
			ByteSmsgwUtil.makeBytesMsg(packet, offset, smsVo.getCallbackNo().getBytes(), SMSGWMessageConst.CALLBACK_LENGTH);
			offset+=SMSGWMessageConst.CALLBACK_LENGTH;
			ByteSmsgwUtil.makeBytesMsg(packet, offset, smsVo.getMsgCont().getBytes("euc-kr"), SMSGWMessageConst.TEXT_LENGTH);
			offset+=SMSGWMessageConst.TEXT_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, smsMsgId);
			offset+=SMSGWMessageConst.SN_LENGTH;

			logger.debug("########## SendMsg ID : " +smsVo.getMsgId());
			logger.debug("================ HEX String =====================");
			logger.debug(ByteSmsgwUtil.writeHexaLog(packet));
			
			if(!SmsServiceImpl.smsSession.send(packet))	{
				logger.info("Deliver MSG send FAIL: \n"+ smsVo.toString());
				sendSMSGW(smsMsgId,smsVo);
			}else{
				logger.info("Deliver MSG send Success: \n"+ smsVo.toString());
				SmsGWConst.deliverCount.remove(smsMsgId);
			}
			
		}
		catch(Exception e){
			logger.error("sendEachSMS Exception",e);
		}
	}
}