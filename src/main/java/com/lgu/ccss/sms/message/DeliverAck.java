package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;

public class DeliverAck extends SMSGWMessage
{
	public static final String PDU_NAME = "DELIVER_ACK";

	private final static int DELIVER_ACK_RESULT	= 4;
	private final static int DELIVER_ACK_ORGADDR_SIZE	= 32;
	private final static int DELIVER_ACK_DSTADDR_SIZE	= 32;
	private final static int DELIVER_ACK_SN_SIZE	= 4;

	public final static int DELIVER_ACK_BODY_SIZE = DELIVER_ACK_RESULT + 
												DELIVER_ACK_ORGADDR_SIZE +
												DELIVER_ACK_DSTADDR_SIZE +
												DELIVER_ACK_SN_SIZE;
	
	private int result = -1;
	private String orgaddr = null;
	private String dstaddr = null;
	private int sn = -1;

	public int getResult()
	{
		return this.result;
	}
	
	public int getSn()
	{
		return this.sn;
	}
	
	public DeliverAck()
	{
		setMessageType(SMSGWMessageConst.DELIVER_ACK);
		setMessageLength(DELIVER_ACK_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(result);
		buffer.appendString( orgaddr, DELIVER_ACK_ORGADDR_SIZE);
		buffer.appendString( dstaddr, DELIVER_ACK_DSTADDR_SIZE);
		buffer.appendInt(sn);
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		result		= buffer.removeInt();
		orgaddr		= buffer.removeString(DELIVER_ACK_ORGADDR_SIZE, null);
		dstaddr		= buffer.removeString(DELIVER_ACK_DSTADDR_SIZE, null);
		sn			= buffer.removeInt();
	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getLogString("RESULT",result));
		sb.append(getLogString("DELIVER_ACK_ORGADDR",orgaddr));
		sb.append(getLogString("DELIVER_ACK_DSTADDR",dstaddr));
		sb.append(getLogString("SN",sn));
		sb.append(getEndBanner(PDU_NAME));
		return sb.toString();
	}

}
