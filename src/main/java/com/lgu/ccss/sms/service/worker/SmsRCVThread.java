package com.lgu.ccss.sms.service.worker;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lgu.ccss.common.exception.SmsGWConnectionException;
import com.lgu.ccss.common.tlo.TloData;
import com.lgu.ccss.common.tlo.TloUtil;
import com.lgu.ccss.common.util.ByteSmsgwUtil;
import com.lgu.ccss.sms.message.Report;
import com.lgu.ccss.sms.message.ReportAck;
import com.lgu.ccss.sms.constant.SmsGWConst;
import com.lgu.ccss.sms.constant.SmsStatusConst;
import com.lgu.ccss.sms.mapper.SmsDao;
import com.lgu.ccss.sms.message.DeliverAck;
import com.lgu.ccss.sms.message.SMSGWMessage;
import com.lgu.ccss.sms.message.SMSGWMessageConst;
import com.lgu.ccss.sms.model.SmsVO;
//import com.lgu.ccss.sms.service.session.SessionServiceImpl;
import com.lgu.ccss.sms.service.sms.SmsServiceImpl;
import com.lgu.common.tlo.TloWriter;

public class SmsRCVThread extends Thread{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	private SmsGWConnection conn = null;
	private boolean theadExitFlag = false;
	private SmsDao smsDao;
	TloWriter tloWriter;
	
	public SmsRCVThread (SmsGWConnection conn,SmsDao smsDao,TloWriter tloWriter){
		this.conn = conn;
		this.smsDao = smsDao;
		this.tloWriter=tloWriter;
	}
	
	public int open()
	{
		try
		{
			return conn.open();
		}
		catch(Exception e)
		{
			logger.error("[ReadThread.open()] Exception ",e);
			return 0;
		}
	}

	public void run()
	{
		while (true)
		{
			try
			{
				if(theadExitFlag)
					return;
				SMSGWMessage smsgwMsg = conn.read();
				if (smsgwMsg == null) 
				{
					logger.info("[ReadThread]run No message from SMSGW");
					Thread.sleep(100);
					continue;
				}
				int messgeType = smsgwMsg.getMessageType();
				SmsVO smsVo = new SmsVO();
				
				switch (messgeType) 
				{
					case SMSGWMessageConst.LINK_RECV:
						logger.debug("[ReadThread] messgeType=LINK_RECV\r\n");
						conn.setLink(false);
						break;
					case SMSGWMessageConst.BIND_ACK:
						logger.debug( "[ReadThread] messgeType=BIND_ACK\r\n"+smsgwMsg.toString());
						break;
					case SMSGWMessageConst.DELIVER_ACK:
						logger.debug("[ReadThread] messgeType=DELIVER_ACK\r\n"+smsgwMsg.toString());
						int smsMsgId = ((DeliverAck)smsgwMsg).getSn();
						smsVo = (SmsVO) SmsGWConst.smsVo.get(smsMsgId);
						int resultCode = ((DeliverAck)smsgwMsg).getResult();
						smsVo.setResultCode(String.valueOf(resultCode));
						if(resultCode==SMSGWMessageConst.E_SYSFAIL)
						{
							logger.debug("SYSFAIL");
							int retryCnt;
//							smsVo.setMsgId(((DeliverAck)smsgwMsg).getSn());
//							smsVo = smsDao.selectTargetMsg(smsVo);
							retryCnt = (int) SmsGWConst.retryCount.get(smsMsgId);
							if(retryCnt<3){
								logger.error("[ReadThread]DELIVER_ACK FAIL, RETRY. retryCnt:"+(retryCnt+1));
								SmsGWConst.retryCount.put(smsMsgId, retryCnt+1);
								SmsWorkerThread.sendEachSMS(smsMsgId,(SmsVO) SmsGWConst.smsVo.get(smsMsgId));
								smsDao.smsUpdateCount(smsVo);
							}else{
								logger.error("[ReadThread]DELIVER_ACK RETRY FAIL, RETRY COUNT Exceed. SN: "+((DeliverAck)smsgwMsg).getSn());
								logger.debug("############# TLO CHECK !!!!");
								SmsGWConst.smsVo.remove(smsMsgId);
								smsDao.smsProcessFinish(smsVo, SmsStatusConst.SMS_STATUS_CODE_SEND_FAIL);
								setTloData(smsVo);
								SmsGWConst.retryCount.remove(smsMsgId);
							}
						}else if(resultCode==SMSGWMessageConst.E_OK){
//							SmsGWConst.smsVo.remove(smsMsgId);
							smsDao.smsProcessFinish(smsVo, SmsStatusConst.SMS_STATUS_CODE_SEND_SUCCESS);
							setTloData(smsVo);
							SmsGWConst.retryCount.remove(smsMsgId);
						}else {
							logger.error("DELIVER_ACK Not OK");
							SmsGWConst.smsVo.remove(smsMsgId);
							smsDao.smsProcessFinish(smsVo, SmsStatusConst.SMS_STATUS_CODE_SEND_FAIL);
							setTloData(smsVo);
							SmsGWConst.retryCount.remove(smsMsgId);
						}
						
						break;
					case SMSGWMessageConst.REPORT:
						logger.debug("[ReadThread] messgeType=REPORT\r\n"+smsgwMsg.toString());
						
						Report rep = new Report();
						rep = (Report)smsgwMsg;
						int smsgwMsgId = rep.getSn();
						smsVo = (SmsVO) SmsGWConst.smsVo.get(smsgwMsgId);

						logger.debug("Map Check : "+SmsGWConst.smsVo.getTimeToLive());
						logger.debug("Map Check : "+SmsGWConst.smsVo.getExpirationInterval());
						logger.debug("Map Check : "+SmsGWConst.smsVo.size());
						logger.debug("Map Check : "+SmsGWConst.smsVo.get(rep.getSn()));
						logger.debug("REPORT");
						logger.debug(smsVo.toString());
//						smsVo = smsDao.selectTargetMsg(smsVo);
						logger.debug(smsVo.toString());
						sendReportAck(rep);
						logger.debug("################################################## remove");
						logger.debug(""+SmsGWConst.smsVo.containsKey((rep).getSn()));
						if( rep.getResult()==SMSGWMessageConst.E_SEND){
							logger.info("SMSGW Message Success - Receive Report");
						}else{
							logger.error("[ReadThread]REPORT Result FAIL:"+rep.getResult());
						}
						
						break;
					default:
						logger.error("[ReadThread] Undefined messageType");
						break;
				}
				Thread.sleep(10);
			}
			catch(SmsGWConnectionException smsgwe)
			{
				try
				{
					logger.error("[ReadThread] Connection Close, Retry] SMSGWConnectionException = ", smsgwe);
					if(conn!=null)
						conn.close();
					
					Thread.sleep(1000);
					SmsGWConnection.isBind=false;
					open();
					continue;
//					return;
				}
				catch(Exception e)
				{
					logger.error("[ReadThread] Connection Close] Exception = ", e);
					return;
				}
			}
			catch(IOException sce)
			{
				try
				{
					logger.error( "[ReadThread] Connection Close,Retry] IOException = ", sce);
					
					if(conn!=null)
						conn.close();
					
					Thread.sleep(1000);
					SmsGWConnection.isBind=false;
					
					open();
					continue;
//					return;
				}
				catch(Exception e)
				{
					logger.error("[ReadThread] Connection Close] Exception = ", e);
					return;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace ();
				logger.error("[ReadThread.run] Exception = ", e);
			}
		}
		
	}
	public void sendReportAck(Report rep){
		logger.debug("sendReportAck Message");
		try{
			ReportAck msg = new ReportAck();
			msg.setResult(SMSGWMessageConst.E_OK);
			byte packet[] = new byte[SMSGWMessageConst.HEADER_LENGTH + SMSGWMessageConst.SMSGW_REPORT_ACK_BODYLENGTH];
			int offset =0;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.REPORT_ACK);
			offset+=SMSGWMessageConst.SMSGW_TYPE_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.SMSGW_REPORT_ACK_BODYLENGTH);
			offset+=SMSGWMessageConst.SMSGW_LENGTH_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, msg.getResult());
			offset+=SMSGWMessageConst.RESULT_LENGTH;
			//Log4J.println(Log4J.INFO,"SEND ReportAck MSG : \n"+ByteSmsgwUtil.writeHexaLog(packet)+msg.toString());
			logger.info("SEND ReportAck MSG : \n"+msg.toString());
			if(!SmsServiceImpl.smsSession.send(packet))
				logger.info("ReportAck MSG send FAIL: \n"+ msg.toString());
		}
		catch(Exception e)
		{
			logger.error("run ReportAck Exception",e);
		}
	}
	
	private void setTloData(SmsVO smsVo) {
		if (smsVo == null) {
			logger.debug("############# SMS VO NUll !!!!");
			return;
		}
		Map<String, String> tlo = (Map<String, String>) SmsGWConst.tloMap.get(smsVo.getMsgId());
		String statusCode;
		String resultCode = smsVo.getResultCode();
		if(smsVo.getMsgStatus().equals(SmsStatusConst.SMS_STATUS_CODE_SEND_SUCCESS)) {
			statusCode = SmsStatusConst.SMS_RESULT_CODE_SEND_SUCCESS;
		}else {
			statusCode = SmsStatusConst.SMS_RESULT_CODE_SEND_FAIL;
		}
		logger.debug("TLO DATA 1 : "+tlo.size());
		tlo.put(TloData.SMS_RES_TIME, TloData.getNowDate());
		tlo.put(TloData.RESULT_CODE, statusCode);
		tlo.put(TloData.RSP_TIME, TloData.getNowDate());
		tlo.put(TloData.SMS_RESULT_CODE, resultCode);
		TloUtil.setTloData(tlo);

//		TloWriter tloWriter = new TloWriter();
		logger.debug("############# TLO WRITE !!!!");
		logger.debug("TLO DATA 2 : "+tlo.size());
		tloWriter.write(tlo);
		logger.debug("############# TLO WRITE  DONE !!!!");
		logger.debug("TRACE RESULT : "+tloWriter.write(tlo));
	}
}