package com.lgu.ccss.sms.service.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lgu.ccss.sms.constant.SmsStatusConst;
import com.lgu.ccss.sms.mapper.SmsDao;
import com.lgu.ccss.sms.model.SmsVO;

@Service
public class StatusInitServiceImpl implements StatusInitService{
	private final Logger logger = LoggerFactory.getLogger(StatusInitServiceImpl.class);
	
	@Autowired
	SmsDao smsDao;
	
	@Override
	public boolean doTask() {
		// TODO Auto-generated method stub
		SmsVO smsVo = new SmsVO();
		logger.debug("########################################################");
		logger.debug("#                  UPDATE INIT STATUS                  #");
		logger.debug("########################################################");
		return smsDao.updateSmsInitStatus(smsVo, SmsStatusConst.SMS_STATUS_CODE_SEND_READY);
	}
}
