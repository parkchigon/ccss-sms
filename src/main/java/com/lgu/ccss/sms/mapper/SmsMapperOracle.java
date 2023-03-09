package com.lgu.ccss.sms.mapper;

import com.lgu.ccss.config.annontation.Master;
import com.lgu.ccss.sms.model.SmsVO;

@Master
public interface SmsMapperOracle {
	int insertSmsSendHistory(SmsVO smsVO);
}
