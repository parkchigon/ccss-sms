package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;


public class Bind extends SMSGWMessage
{
	public static final String PDU_NAME = "BIND";

	private final static int BIND_ID_SIZE		= 16;
	private final static int BIND_PASSWORD_SIZE	= 16;

	public final static int BIND_BODY_SIZE = BIND_ID_SIZE + BIND_PASSWORD_SIZE;

	private String id = null;
	private String pwd = null;

	public Bind()
	{
		setMessageType(SMSGWMessageConst.BIND);//��� ����
		setMessageLength(BIND_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString( id, BIND_ID_SIZE);
		buffer.appendString( pwd, BIND_PASSWORD_SIZE);
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		id		= buffer.removeString(BIND_ID_SIZE, null);
		pwd		= buffer.removeString(BIND_PASSWORD_SIZE, null);
	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getLogString("ID",id));
		sb.append(getLogString("PASSWORD",pwd));
		sb.append(getEndBanner(PDU_NAME));
		return sb.toString();
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getPwd()
	{
		return pwd;
	}

	public void setPwd(String pwd)
	{
		this.pwd = pwd;
	}


}
