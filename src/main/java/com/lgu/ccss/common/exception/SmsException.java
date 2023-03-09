package com.lgu.ccss.common.exception;

public class SmsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5183886339647217643L;

	
	public SmsException() {
		super("50000");
	}
	
	public SmsException(String message) {
		super(message);
	}
}
