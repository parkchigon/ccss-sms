package com.lgu.ccss.sms.model;

public class SmsVO {
	
	private String msgId;
	private String msgStatus;
	private String code;
	private String msgTitle;
	private String msgCont;
	private String msgType;
	private String recvPhoneNo;
	private String sendType;
	private String regDt;
	private String regId;
	private String updDt;
	private String updId;
	private String sendDt;
	private String svrId;
	private String orgNo;
	private String callbackNo;
	private Integer sendTryCnt;
	private String processDt;
	private String resultCode;
	
	private int rowCount;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgCont() {
		return msgCont;
	}

	public void setMsgCont(String msgCont) {
		this.msgCont = msgCont;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getRecvPhoneNo() {
		return recvPhoneNo;
	}

	public void setRecvPhoneNo(String recvPhoneNo) {
		this.recvPhoneNo = recvPhoneNo;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getRegDt() {
		return regDt;
	}

	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getUpdDt() {
		return updDt;
	}

	public void setUpdDt(String updDt) {
		this.updDt = updDt;
	}

	public String getUpdId() {
		return updId;
	}

	public void setUpdId(String updId) {
		this.updId = updId;
	}

	public String getSendDt() {
		return sendDt;
	}

	public void setSendDt(String sendDt) {
		this.sendDt = sendDt;
	}

	public String getSvrId() {
		return svrId;
	}

	public void setSvrId(String svrId) {
		this.svrId = svrId;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getCallbackNo() {
		return callbackNo;
	}

	public void setCallbackNo(String callbackNo) {
		this.callbackNo = callbackNo;
	}

	public Integer getSendTryCnt() {
		return sendTryCnt;
	}

	public void setSendTryCnt(Integer sendTryCnt) {
		this.sendTryCnt = sendTryCnt;
	}

	public String getProcessDt() {
		return processDt;
	}

	public void setProcessDt(String processDt) {
		this.processDt = processDt;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	@Override
	public String toString() {
		return "SmsVO [msgId=" + msgId + ", msgStatus=" + msgStatus + ", code=" + code + ", msgTitle=" + msgTitle
				+ ", msgCont=" + msgCont + ", msgType=" + msgType + ", recvPhoneNo=" + recvPhoneNo + ", sendType="
				+ sendType + ", regDt=" + regDt + ", regId=" + regId + ", updDt=" + updDt + ", updId=" + updId
				+ ", sendDt=" + sendDt + ", svrId=" + svrId + ", orgNo=" + orgNo + ", callbackNo=" + callbackNo
				+ ", sendTryCnt=" + sendTryCnt + ", processDt=" + processDt + ", resultCode=" + resultCode
				+ ", rowCount=" + rowCount + "]";
	}
	
	
	
	
	
	
//	private String smsSeq;
//	private String senderId;
//	private String senderName;
//	private String sendType;			//I:즉시, B:예약
//	private String content;
//	private String insertDate;
//	private String bookDate;
//	private String sendStatusCode;		//대기, 실패, 성공
//	private String cancelYn;
//	private String receiverMobileNum;
//	private String senderMobileNum;
//	private int	sendingCount;
//	private String receiverUserIdNum;
//	private String receiverUserName;
//	private String receiverType;		//A:지인, S:군인
//	private String sendDate;
//	private String smsAgreeYn;
	
	
	
}
