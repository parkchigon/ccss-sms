package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;


public class SMSGWHeader
{
	int messageType;
	int messageLength;

	public SMSGWHeader()
	{
		
	}

	public SMSGWHeader(ByteBuffer buffer) throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		setData(buffer);
	}

	public int getMessageType()
	{
		return messageType;
	}

	public void setMessageType(int messageType)
	{
		this.messageType = messageType;
	}

	public int getMessageLength()
	{
		return messageLength;
	}

	public void setMessageLength(int messageLength)
	{
		this.messageLength = messageLength;
	}

	public ByteBuffer getData()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(messageType);
		buffer.appendInt(messageLength);
		return buffer;
	}

	public void setData(ByteBuffer buffer) 
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		messageType		= buffer.removeInt();
		messageLength	= buffer.removeInt();
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("messageType     :").append(messageType).append("\r\n");
		sb.append("messageLength   :").append(messageLength).append("\r\n");
		return sb.toString();
	}
}
