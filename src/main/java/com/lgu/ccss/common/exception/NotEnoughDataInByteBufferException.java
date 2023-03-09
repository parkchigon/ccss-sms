package com.lgu.ccss.common.exception;

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version 1.0, 11 Jun 2001
 */

@SuppressWarnings("serial")
public class NotEnoughDataInByteBufferException extends Exception
{
    private int available;
    private int expected;
    
    public NotEnoughDataInByteBufferException(int p_available, int p_expected)
    {
	    super("Not enough data in byte buffer. " + 
	        "Expected " + p_expected + 
	        ", available: "+p_available + ".");
        available = p_available;
        expected = p_expected;
    }
    
    public NotEnoughDataInByteBufferException(String s)
    {
	    super(s);
        available = 0;
        expected = 0;
    }
    
    public int getAvailable()
    {
        return available;
    }
    
    public int getExpected()
    {
        return expected;
    }
}