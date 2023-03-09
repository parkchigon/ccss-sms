package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;


public class Link_Recv extends SMSGWMessage
{
	public static final String PDU_NAME = "LINK_RECV";


	public final static int LINK_RECV_BODY_SIZE = SMSGWMessageConst.SMSGW_LINK_ACK_BODYLENGTH;

	public Link_Recv()
	{
		setMessageType(SMSGWMessageConst.LINK_RECV);//��� ����
		setMessageLength(LINK_RECV_BODY_SIZE);
	}

	public ByteBuffer getBody()
	{
		ByteBuffer buffer = new ByteBuffer();
		return buffer;
	}

	public void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{

	}

	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(getStartBanner(PDU_NAME));
		sb.append(toStringHeader());
		sb.append(getEndBanner(PDU_NAME));
		return sb.toString();
	}
}
