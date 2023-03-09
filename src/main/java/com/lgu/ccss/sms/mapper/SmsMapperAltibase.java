package com.lgu.ccss.sms.mapper;

import java.util.List;

import com.lgu.ccss.config.annontation.Slave;
import com.lgu.ccss.sms.model.SmsVO;
@Slave
public interface SmsMapperAltibase {
	List<SmsVO> selectSmsTargetList(SmsVO smsVO);
	SmsVO selectTargetMsg(SmsVO smsVO);
	int updateSmsInitStatus(SmsVO smsVO);
	int updateSmsTargetStatus(SmsVO smsVO);
	int updateSmsTargetList(SmsVO smsVO);
	int deleteSmsTarget(SmsVO smsVO);
	int updateSmsTargetStatusList(List<SmsVO> targetList);
	int resetSmsStatus();
	
}
