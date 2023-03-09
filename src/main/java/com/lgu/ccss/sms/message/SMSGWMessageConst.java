package com.lgu.ccss.sms.message;


public final class SMSGWMessageConst
{
	//MTGW header length
	public static final int HEADER_LENGTH = 8;
	
	public static final int SMSGW_TYPE_LENGTH		= 4;
	public static final int SMSGW_LENGTH_LENGTH		= 4;
	
	// MTGW MSG BODY LENGTH
	public static final int SMSGW_BIND_BODYLENGTH = 32;
	public static final int SMSGW_BIND_ACK_BODYLENGTH = 20;

	public static final int SMSGW_LINK_BODYLENGTH = 0;
	public static final int SMSGW_LINK_ACK_BODYLENGTH = 0;

	public static final int SMSGW_DELIVER_BODYLENGTH = 264;
	public static final int SMSGW_DELIVER_ACK_BODYLENGTH = 72;
	
	public static final int SMSGW_LDELIVER_BODYLENGTH = 264;
	public static final int SMSGW_LDELIVER_ACK_BODYLENGTH = 72;

	public static final int SMSGW_REPORT_BODYLENGTH = 104;
	public static final int SMSGW_REPORT_ACK_BODYLENGTH = 4;
	
	//FIELD LENGTH
	public static final int ID_LENGTH = 16;
	public static final int PWD_LENGTH = 16;
	public static final int RESULT_LENGTH = 4;
	public static final int PREFIX_LENGTH = 16;
	public static final int TID_LENGTH = 4;
	public static final int ORGADDR_LENGTH = 32;
	public static final int DSTADDR_LENGTH = 32;
	public static final int CALLBACK_LENGTH = 32;
	public static final int TEXT_LENGTH = 160;
	public static final int SN_LENGTH = 4;
	public static final int DELIVERTIME_LENGTH = 20;
	public static final int DESTCODE_LENGTH = 12;
	
	//MSGTYPE 
	public static final int BIND = 0;
	public static final int BIND_ACK = 1;
	public static final int DELIVER = 2;
	public static final int DELIVER_ACK = 3;
	public static final int REPORT = 4;
	public static final int REPORT_ACK = 5;
	public static final int LINK_SEND = 6;
	public static final int LINK_RECV = 7;
	
	/** BIND_ACK RESULT CODE */
	public static final int E_OK = 0;
	public static final int E_SYSFAIL = 1;
	public static final int E_AUTH_FAIL = 2;
	
	/** DELIVER_ACK RESULT CODE */
	public static final int E_FORMAT_ERR = 3;
	public static final int E_EXPIRED_PWD = 13;
	
	/** REPORT RESULT CODE */
	public static final int E_SEND = 6;
	public static final int E_INVALIDDST = 7;
	public static final int E_POWEROFF = 8;
	public static final int E_HIDDEN = 9;
	public static final int E_TERMFUL = 10;
	public static final int E_ETC = 11;
	public static final int E_PORTED_OUT = 13;
	public static final int E_ROAMING_FAIL = 25;
	public static final int E_DISAGREEMENT = 26;

	/** REPORT_ACK RESULT CODE*/
	public static final int E_NOT_BOUND = 4;

	/** �Ϲ� SMSGW TID */
	public static final int GENERAL_SMSGW_TID = 4098;

	public static final int SMS_SPLIT_BYTESIZE = 81;

	public static final int MAX_SMS_COUNT = 3;
}
