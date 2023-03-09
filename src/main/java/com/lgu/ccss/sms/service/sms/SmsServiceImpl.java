package com.lgu.ccss.sms.service.sms;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lgu.ccss.sms.constant.SmsGWConst;
import com.lgu.ccss.sms.constant.SmsStatusConst;
import com.lgu.ccss.sms.mapper.SmsDao;
import com.lgu.ccss.sms.model.SmsVO;
import com.lgu.ccss.sms.service.worker.SmsGWConnection;
import com.lgu.ccss.sms.service.worker.SmsSession;
import com.lgu.ccss.sms.service.worker.SmsWorkerThread;
import com.lgu.common.tlo.TloWriter;


@Service
public class SmsServiceImpl implements SmsService{
	private final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
	
	@Value("#{config['sms.threadCnt']}")
	private String threadCnt;
	@Value("#{config['sms.domain']}")
	private String smsDomain;
	@Value("#{config['sms.port']}")
	private String smsPort;
	@Value("#{config['sms.tid']}")
	private int smsTid;
	@Value("#{config['sms.rebindTime']}")
	private int rebindTime;
	@Value("#{config['sms.orgNo']}")
	private String orgNo;
//	@Value("#{config['daemon.vectorSize']}")
//	private int vectorSize;
	@Value("#{config['db.rowCount']}")
	private int rowCount;
	@Value("#{systemProperties.SERVER_ID}")
	private String svrId;
	
	@Autowired
	SmsGWConnection smsGWConnection;
	@Autowired
	SmsDao smsDao;
	
	@Autowired
	private TloWriter tloWriter;

	public static SmsSession smsSession = null;
	public static Vector<SmsVO> msgVector		= null;
	public static SmsWorkerThread[] workerThreadArr = null;
	
	@Override
	public void doTask() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("SmsService doTask!");
		if(connectSMSGW() == 0) {
			logger.error("SMSGW Connection Error");
			Thread.sleep(rebindTime);
		}else {
			logger.debug("msgVector Size : "+SmsGWConst.SMS_QUEUE_SIZE);
			if(msgVector == null) {
				msgVector= new Vector<SmsVO>();
				msgVector.setSize(SmsGWConst.SMS_QUEUE_SIZE);
				msgVector.clear();
			}
			logger.debug("makeWorkerThread!");
			if( workerThreadArr == null ){
				makeWorkerThread();
			}
			logger.debug("getMessage!");
			getMessage();
		}
		
		
		
	}
	public void getMessage() {
		List<SmsVO> targetList = new ArrayList<SmsVO>();
		logger.debug("msgVector : "+msgVector.capacity());
		logger.debug("msgVector : "+msgVector.size());
		logger.debug("msgVector : "+msgVector.isEmpty());
		if(msgVector.isEmpty()) {
			
			targetList = smsDao.getTargetList(rowCount);
		}
//		int selectSize = msgVector.capacity()-msgVector.size();
//		if(selectSize > rowCount) {
//			selectSize=rowCount;
//		}
//		logger.debug("Select Message RowCount : "+rowCount);
//		List<SmsVO> targetList = smsDao.getTargetList(selectSize);
		logger.debug("targetList.size : "+targetList.size());
		if( targetList == null || targetList.size() == 0) {
			return;
		}	
		
		for( int i = 0; i < targetList.size(); i++ ){
			SmsVO smsVo = targetList.get(i);
//			int sendTryCnt = smsVo.getSendTryCnt();
			smsVo.setSendTryCnt(1);
			smsVo.setOrgNo(orgNo);
			if(smsDao.smsUpdateStatus(smsVo,  SmsStatusConst.SMS_STATUS_CODE_SEND_ING)) {
				msgVector.add(smsVo);
			}
		}
	} 
	
	public void makeWorkerThread ()	{
		workerThreadArr = new SmsWorkerThread[Integer.parseInt(threadCnt)];
		for (int i = 0; i < Integer.parseInt(threadCnt); i++) {
			SmsWorkerThread smsWorkerThread = new SmsWorkerThread (i + 1,smsDao,smsTid,svrId,tloWriter);
			workerThreadArr[i]=smsWorkerThread;
			logger.info("Make WorkerThread " + i);
		}
	}
	
	public int connectSMSGW() {
		try {
			if(smsSession == null) {
				smsSession = new SmsSession(smsGWConnection,smsDao,tloWriter);
			}
			logger.debug("smsSession Instance : "+smsSession);
			return smsSession.start();
		}catch(Exception e)	{
			e.printStackTrace();
			logger.error("[SMSGWAgent.connectSMSGW()] Exception ",e);
			return 0;
		}
	}
}
