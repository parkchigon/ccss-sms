package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;

public class Report extends SMSGWMessage
{
	public static final String PDU_NAME = "REPORT";

	private final static int REPORT_RESULT_SIZE	= 4;
	private final static int REPORT_ORGADDR_SIZE	= 32;
	private final static int REPORT_DSTADDR_SIZE	= 32;
	private final static int REPORT_SN_SIZE	= 4;
	private final static int REPORT_DELIVERTIME_SIZE	= 20;
	private final static int REPORT_DESTCODE_SIZE	= 12;

	public final static int REPORT_BODY_SIZE = REPORT_RESULT_SIZE + 
												REPORT_ORGADDR_SIZE +
												REPORT_DSTADDR_SIZE +
												REPORT_SN_SIZE+
												REPORT_DELIVERTIME_SIZE +
												REPORT_DESTCODE_SIZE;
	
	private int result = -1;
	private String orgaddr = null;
	private String dstaddr = null;
	private int sn = -1;
	private String deliverTime = null;
	private String destCode = null;

	public Report()
	{
		setMessageType(SMSGWMessageConst.REPORT);
		setMessageLength(REPORT_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(result);
		buffer.appendString( orgaddr, REPORT_ORGADDR_SIZE);
		buffer.appendString( dstaddr, REPORT_DSTADDR_SIZE);
		buffer.appendInt(sn);
		buffer.appendString( deliverTime, REPORT_DELIVERTIME_SIZE);
		buffer.appendString( destCode, REPORT_DESTCODE_SIZE);
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		result			= buffer.removeInt();
		orgaddr		= buffer.removeString(REPORT_ORGADDR_SIZE, null);
		dstaddr		= buffer.removeString(REPORT_DSTADDR_SIZE, null);
		sn			= buffer.removeInt();
		deliverTime		= buffer.removeString(REPORT_DELIVERTIME_SIZE, null);
		destCode		= buffer.removeString(REPORT_DESTCODE_SIZE, null);
	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getLogString("RESULT",result));
		sb.append(getLogString("REPORT_ORGADDR",orgaddr));
		sb.append(getLogString("REPORT_DSTADDR",dstaddr));
		sb.append(getLogString("SN",sn));
		sb.append(getLogString("REPORT_DELIVERTIME",deliverTime));
		sb.append(getLogString("REPORT_DESTCODE",destCode));
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

	public String getOrgaddr()
	{
		return orgaddr;
	}

	public void setOrgaddr(String orgaddr)
	{
		this.orgaddr = orgaddr;
	}

	public String getDstaddr()
	{
		return dstaddr;
	}

	public void setDstaddr(String dstaddr)
	{
		this.dstaddr = dstaddr;
	}

	public int getSn()
	{
		return sn;
	}

	public void setSn(int sn)
	{
		this.sn = sn;
	}

	public String getDeliverTime()
	{
		return deliverTime;
	}

	public void setDeliverTime(String deliverTime)
	{
		this.deliverTime = deliverTime;
	}

	public String getDestCode()
	{
		return destCode;
	}

	public void setDestCode(String destCode)
	{
		this.destCode = destCode;
	}
}
