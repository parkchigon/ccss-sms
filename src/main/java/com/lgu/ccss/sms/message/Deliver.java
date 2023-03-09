package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;


public class Deliver extends SMSGWMessage
{
	public static final String PDU_NAME = "DELIVER";

	private final static int DELIVER_TID_SIZE	= 4;
	private final static int DELIVER_ORGADDR_SIZE	= 32;
	private final static int DELIVER_DSTADDR_SIZE	= 32;
	private final static int DELIVER_CALLBACK_SIZE	= 32;
	private final static int DELIVER_TEXT_SIZE	= 160;
	private final static int DELIVER_SN_SIZE	= 4;

	public final static int DELIVER_BODY_SIZE = DELIVER_TID_SIZE + 
												DELIVER_ORGADDR_SIZE +
												DELIVER_DSTADDR_SIZE +
												DELIVER_CALLBACK_SIZE +
												DELIVER_TEXT_SIZE +
												DELIVER_SN_SIZE;
	
	private int tid = -1;
	private String orgaddr = null;
	private String dstaddr = null;
	private String callback = null;
	private String text = null;
	private int sn = -1;

	public Deliver()
	{
		setMessageType(SMSGWMessageConst.DELIVER);
		setMessageLength(DELIVER_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(tid);
		buffer.appendString( orgaddr, DELIVER_ORGADDR_SIZE);
		buffer.appendString( dstaddr, DELIVER_DSTADDR_SIZE);
		buffer.appendString( callback, DELIVER_CALLBACK_SIZE);
		buffer.appendString( text, DELIVER_TEXT_SIZE);
		buffer.appendInt(sn);
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		tid			= buffer.removeInt();
		orgaddr		= buffer.removeString(DELIVER_ORGADDR_SIZE, null);
		dstaddr		= buffer.removeString(DELIVER_DSTADDR_SIZE, null);
		callback	= buffer.removeString(DELIVER_CALLBACK_SIZE, null);
		text		= buffer.removeString(DELIVER_TEXT_SIZE, null);
		sn			= buffer.removeInt();
	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getLogString("TID",tid));
		sb.append(getLogString("DELIVER_ORGADDR",orgaddr));
		sb.append(getLogString("DELIVER_DSTADDR",dstaddr));
		sb.append(getLogString("DELIVER_CALLBACK",callback));
//		sb.append(getLogString("DELIVER_TEXT",text));
		sb.append(getLogString("SN",sn));
		sb.append(getEndBanner(PDU_NAME));
		return sb.toString();
	}
	
	public int getTid()
	{
		return tid;
	}

	public void setTid(int tid)
	{
		this.tid = tid;
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

	public String getCallback()
	{
		return callback;
	}

	public void setCallback(String callback)
	{
		this.callback = callback;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public int getSn()
	{
		return sn;
	}

	public void setSn(int sn)
	{
		this.sn = sn;
	}
}
