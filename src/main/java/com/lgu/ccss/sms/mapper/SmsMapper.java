package com.lgu.ccss.sms.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lgu.ccss.sms.model.SmsVO;

@Component
public class SmsMapper {
	
	@Autowired
	SmsMapperOracle smsMapperOracle;
	
	@Autowired
	SmsMapperAltibase smsMapperAltibase;
	
	public int insertSmsSendHistory(SmsVO smsVO) {
		return smsMapperOracle.insertSmsSendHistory(smsVO);
	}
	public List<SmsVO> selectSmsTargetList(SmsVO smsVO) {
		return smsMapperAltibase.selectSmsTargetList(smsVO);
	}
	public SmsVO selectTargetMsg(SmsVO smsVO) {
		return smsMapperAltibase.selectTargetMsg(smsVO);
	}
	public int updateSmsInitStatus(SmsVO smsVO) {
		return smsMapperAltibase.updateSmsInitStatus(smsVO);
	}
	public int updateSmsTargetStatus(SmsVO smsVO) {
		return smsMapperAltibase.updateSmsTargetStatus(smsVO);
	}
	public int updateSmsTargetList(SmsVO smsVO) {
		return smsMapperAltibase.updateSmsTargetList(smsVO);
	}
	public int deleteSmsTarget(SmsVO smsVO) {
		return smsMapperAltibase.deleteSmsTarget(smsVO);
	}
	public int updateSmsTargetStatusList(List<SmsVO> targetList) {
		// TODO Auto-generated method stub
		return smsMapperAltibase.updateSmsTargetStatusList(targetList);
		
	}
	public int resetSmsStatus() {
		// TODO Auto-generated method stub
		return smsMapperAltibase.resetSmsStatus();
		
	}
	
	
}
