package com.lgu.ccss.common.exception;

@SuppressWarnings("serial")
public class SmsGWConnectionException extends Exception
{
	public SmsGWConnectionException() { super(); } 
	public SmsGWConnectionException(String reason) { super(reason); } 
}