package com.lgu.ccss.sms.message;


import java.io.UnsupportedEncodingException;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.util.ByteBuffer;


public abstract class SMSGWMessage
{
	public static final String CRLF			= "\r\n";
	public static final String LOG_STRING_DELIM	= ":";
	public static final int LOG_HEADER_LENGTH	= 16;
	public static final int LOG_BANNER_LENGTH	= 24;
	public static final String LOG_STRING_SPACE	= " ";
	public static final String LOG_STRING_TAG	= "---------------------------------";
	public static final String LOG_STRING_START	= "START";
	public static final String LOG_STRING_END	= "END";
	public static final String LOG_STRING_TAP	= "\t";
	
	public static final int LOG_DEPTH_1		= 0;
	public static final int LOG_DEPTH_2		= 1;
	public static final int LOG_DEPTH_3		= 2;
	public static final int LOG_DEPTH_4		= 3;

	public int logDepth = LOG_DEPTH_1;

	public SMSGWHeader header;
	
	public static final String PDU_ELEMENT_HEADER = "header";
	
	public SMSGWMessage()
	{
	}

	public ByteBuffer getData() throws InvalidMessageException
	{
		ByteBuffer bodyBuf = new ByteBuffer();
		bodyBuf.appendBuffer(getBody());
		setMessageLength(bodyBuf.length());
		ByteBuffer pduBuf = getHeader();
		pduBuf.appendBuffer(bodyBuf);
		return pduBuf;
	}

	public void setData(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		int initialBufLen = buffer.length();
		try
		{
			if(buffer.length() < SMSGWMessageConst.HEADER_LENGTH )
				throw new InvalidMessageException("Header Size is short! "+buffer.length());
			ByteBuffer headerBuf = buffer.removeBytes(SMSGWMessageConst.HEADER_LENGTH);
			setHeader(headerBuf);
			setBody(buffer);
			if(buffer.length() != (initialBufLen - ( getMessageLength() + SMSGWMessageConst.HEADER_LENGTH )))
				throw new InvalidMessageException("Something is abnormal! "+buffer.length());
		}
		catch(NotEnoughDataInByteBufferException ndbe)
		{
			throw ndbe;
		}
		catch(InvalidMessageException ipe)
		{
			throw ipe;
		}
		catch(UnsupportedEncodingException uee)
		{
			throw uee;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InvalidMessageException("PDU is abnormal "+e.getMessage());
		}
	}

	public abstract ByteBuffer getBody()throws InvalidMessageException;

	public abstract void setBody(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException;

	private void checkHeader()
	{
		if (header == null)
		{
			header = new SMSGWHeader();
		}
	}

	private ByteBuffer getHeader()
	{
		checkHeader();
		return header.getData();
	}

	private void setHeader(ByteBuffer buffer)
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		checkHeader();
		header.setData(buffer);
	}

	public String toStringHeader()
	{
		checkHeader();
		return header.toString();
	}

	public void setMessageLength(int length)
	{
		checkHeader();
		header.setMessageLength(length);
	}

	public int getMessageLength()
	{
		checkHeader();
		return header.getMessageLength();
	}

	public void setMessageType(int type)
	{
		checkHeader();
		header.setMessageType(type);
	}

	public int getMessageType()
	{
		checkHeader();
		return header.getMessageType();
	}

	public void setHeader(SMSGWHeader pduHeader )
	{
		this.header = pduHeader;
	}

	public String getLogString(String attribute, String value)
	{
		StringBuffer buffer = new StringBuffer();
		for( int i = 0; i < logDepth; i++)
			buffer.append(LOG_STRING_TAP);
		buffer.append(checkLength(attribute, LOG_HEADER_LENGTH)).append(LOG_STRING_DELIM).append(value).append(CRLF);
		return buffer.toString();
	}

	public String getLogString(String attribute, int value)
	{
		StringBuffer buffer = new StringBuffer();
		for( int i = 0; i < logDepth; i++)
			buffer.append(LOG_STRING_TAP);
		buffer.append(checkLength(attribute, LOG_HEADER_LENGTH)).append(LOG_STRING_DELIM).append(value).append(CRLF);
		return buffer.toString();
	}

	public String getStartBanner(String pduName)
	{
		return getBanner(pduName, LOG_STRING_START);
		//return "";
	}

	public String getEndBanner(String pduName)
	{
		return getBanner(pduName, LOG_STRING_END);
		//return "";
	}

	public String getBanner(String str, String str1)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(LOG_STRING_TAG)
		.append(checkLength(str + LOG_STRING_SPACE + str1, LOG_BANNER_LENGTH))
		.append(LOG_STRING_TAG).append(CRLF);
		return buffer.toString();
	}

	public String checkLength(String str, int strLength)
	{
		if( str == null )
			return null;
		if( str.length() < strLength )
		{
			int lengthDiff = strLength - str.length();
			StringBuffer buffStr = new StringBuffer(str);
			for( int i = 0; i < lengthDiff; i++ )
			{
				buffStr.append(LOG_STRING_SPACE);
			}
			return buffStr.toString();
		}
		else if( str.length() == strLength )
			return str;
		else
			return str;
	}

	public void parseData(SMSGWHeader header, ByteBuffer bodyBuffer) 
	throws InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		this.header = header;
		try
		{
			if(bodyBuffer.length() != header.getMessageLength())
				throw new InvalidMessageException("parseData Body is abnormal! "+header.getMessageLength()+" : "+bodyBuffer.length());
			setBody(bodyBuffer);
		}
		catch(NotEnoughDataInByteBufferException ndbe)
		{
			throw ndbe;
		}
		catch(InvalidMessageException ipe)
		{
			throw ipe;
		}
		catch(UnsupportedEncodingException uee)
		{
			throw uee;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InvalidMessageException("Message is abnormal "+e.getMessage());
		}
	}
}
