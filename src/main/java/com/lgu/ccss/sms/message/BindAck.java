package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;

public class BindAck extends SMSGWMessage
{
	public static final String PDU_NAME = "BIND_ACK";

	private final static int BIND_ACK_RESULT_SIZE	= 4;
	private final static int BIND_ACK_PREFIX_SIZE	= 16;

	public final static int BIND_ACK_BODY_SIZE = BIND_ACK_RESULT_SIZE + BIND_ACK_PREFIX_SIZE;

	private int result = -1;
	private String prefix = null;

	public BindAck()
	{
		setMessageType(SMSGWMessageConst.BIND_ACK);
		setMessageLength(BIND_ACK_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt( result);
		buffer.appendString( prefix, BIND_ACK_PREFIX_SIZE);
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		result		= buffer.removeInt();
		prefix		= buffer.removeString(BIND_ACK_PREFIX_SIZE, null);
	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getLogString("BIND_ACK_RESULT",result));
		sb.append(getLogString("BIND_ACK_PREFIX",prefix));
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

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}


}
