package com.lgu.ccss.sms.mapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lgu.ccss.sms.constant.SmsStatusConst;
import com.lgu.ccss.sms.model.SmsVO;

@Component
public class SmsDao {
	private final Logger logger = LoggerFactory.getLogger(SmsDao.class);
	
	@Value("#{config['sms.systemId']}")
	private String systemId;
	@Value("#{systemProperties.SERVER_ID}")
	private String svrId;
	
	@Autowired
	private SmsMapper smsMapper;
	
	public List<SmsVO> getTargetList(int selectSize) {
		// 예약전송시간 이전 메시지 + 대기상태  메시지를 조회
		
		SmsVO smsVo = new SmsVO();
		Date nowDt = new Date();
		smsVo.setSendDt(new SimpleDateFormat("yyyyMMddHHmmdd").format(nowDt));
		smsVo.setMsgStatus(SmsStatusConst.SMS_STATUS_CODE_SEND_READY);
		smsVo.setSvrId(svrId);
		smsVo.setRowCount(selectSize);
		logger.debug("### select Data : "+smsVo.toString());
		List<SmsVO> targetList = smsMapper.selectSmsTargetList(smsVo);

		return targetList;
	}
	public boolean updateSmsInitStatus(SmsVO smsVo,String status) {
		smsVo.setMsgStatus(status);
		smsVo.setUpdId(systemId);
		logger.debug("### update Data : "+smsVo.toString());
		
		int resultStatus = smsMapper.updateSmsInitStatus(smsVo);
		logger.debug("ResultStatus : "+resultStatus);
		
		return true;
	}
	
	public SmsVO selectTargetMsg(SmsVO smsVo) {
		return smsMapper.selectTargetMsg(smsVo);
	}
	public boolean smsUpdateStatus(SmsVO smsVo,String status) {
		smsVo.setMsgStatus(status);
		smsVo.setUpdId(systemId);
		logger.debug("### update Data : "+smsVo.toString());
		int resultStatus = smsMapper.updateSmsTargetStatus(smsVo);
		logger.debug("rseult : "+resultStatus);
		if(resultStatus<1) {
			// 실패시 처리
			logger.debug("Status update Fail");
			return false;
		}
		return true;
	}
	public void smsProcessFinish(SmsVO smsVo,String code) {
		logger.debug("############ smsVo : "+smsVo.toString());
		smsVo.setMsgStatus(code);
		smsVo.setSendTryCnt(smsVo.getSendTryCnt());
		smsVo.setUpdId(systemId);
		smsVo.setSendDt(new SimpleDateFormat("yyyyMMddHHmmdd").format(new Date()));
//		smsVo.setProcessDt(DateUtils.getCurrentTime("yyyyMMdd"));
		smsMapper.insertSmsSendHistory(smsVo); // 성공 이력 저장
		smsMapper.deleteSmsTarget(smsVo);
	}
	public void resetSMSStatus() {
		smsMapper.resetSmsStatus();
	}
	public void smsUpdateCount(SmsVO smsVo) {
		smsVo.setSendTryCnt(smsVo.getSendTryCnt()+1);
		smsMapper.updateSmsTargetList(smsVo); // 시도 횟수 업데이트
	}
	public void smsUpdateStatusList(List<SmsVO> targetList) {
		// TODO Auto-generated method stub
		
		smsMapper.updateSmsTargetStatusList(targetList);
		
	}
}
