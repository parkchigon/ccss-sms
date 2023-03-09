package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;

public class ReportAck extends SMSGWMessage
{
	public static final String PDU_NAME = "REPORT_ACK";

	private final static int REPORT_ACK_RESULT_SIZE	= 4;

	public final static int REPORT_ACK_BODY_SIZE = REPORT_ACK_RESULT_SIZE;

	private int result = -1;

	public ReportAck()
	{
		setMessageType(SMSGWMessageConst.REPORT_ACK);
		setMessageLength(REPORT_ACK_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt( result);
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		result		= buffer.removeInt();
	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getLogString("REPORT_ACK_RESULT",result));
		sb.append(getEndBanner(PDU_NAME));
		return sb.toString();
	}
	
	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}
}
