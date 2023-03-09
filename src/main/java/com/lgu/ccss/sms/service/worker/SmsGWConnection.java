package com.lgu.ccss.sms.service.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lgu.ccss.common.exception.InvalidMessageException;
import com.lgu.ccss.common.exception.NotEnoughDataInByteBufferException;
import com.lgu.ccss.common.exception.SmsGWConnectionException;
import com.lgu.ccss.common.util.ByteBuffer;
import com.lgu.ccss.common.util.ByteSmsgwUtil;
import com.lgu.ccss.sms.mapper.SmsDao;
import com.lgu.ccss.sms.message.Bind;
import com.lgu.ccss.sms.message.BindAck;
import com.lgu.ccss.sms.message.Deliver;
import com.lgu.ccss.sms.message.DeliverAck;
import com.lgu.ccss.sms.message.Link_Recv;
import com.lgu.ccss.sms.message.Link_Send;
import com.lgu.ccss.sms.message.Report;
import com.lgu.ccss.sms.message.ReportAck;
import com.lgu.ccss.sms.message.SMSGWHeader;
import com.lgu.ccss.sms.message.SMSGWMessage;
import com.lgu.ccss.sms.message.SMSGWMessageConst;

@Component
public class SmsGWConnection {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("#{config['sms.domain']}")
	private String smsDomain;
	@Value("#{config['sms.port']}")
	private int smsPort;
	@Value("#{config['sms.id']}")
	private String smsId;
	@Value("#{config['sms.pw']}")
	private String smsPw;
	@Autowired
	private SmsDao smsDao;
	
	private boolean isPing = false;
	
	@Value("#{config['socket.timeout']}")
	private int receiveTimeout;
//	private int receiveTimeout = 10000;
//	private int receiveTimeout = 60000;
	
	private boolean opened = false;
	public static boolean isBind = false;
	private Socket socket = null;
	
	
	public int open() throws IOException
	{
		logger.debug("SMSGW Connection opened : "+opened);
		if (!opened)
		{
			try
			{
				logger.debug("###############################################################");
				logger.debug("address : "+smsDomain + " | port : "+smsPort);
				// reset Status
				smsDao.resetSMSStatus();
				socket = new Socket(smsDomain, smsPort);
				try
				{
					logger.info("[SMSGWConnection.open()] "+smsDomain +": "+ smsPort);
					sendBindMsg();
				}
				catch (SmsGWConnectionException e)
				{
					e.printStackTrace();
				}
				
				opened = true;

				return 1;
			}
			catch (IOException e)
			{
				throw new IOException("[SMSGWConnection.open()]IOException opening SMSGWConnection "+e.getMessage());
			}
		}
		else
		{
			return 2;
//			throw new IOException("[SMSGWConnection.open()] Already opened connection");
		}
	}
	
	public void sendBindMsg() throws SmsGWConnectionException, IOException
	{
		try
		{
			Bind bind = new Bind();
			bind.setId(smsId);
			bind.setPwd(smsPw);
			byte packet[] = new byte[SMSGWMessageConst.HEADER_LENGTH + SMSGWMessageConst.SMSGW_BIND_BODYLENGTH];
			int offset =0;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.BIND);
			offset+=SMSGWMessageConst.SMSGW_TYPE_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.SMSGW_BIND_BODYLENGTH);
			offset+=SMSGWMessageConst.SMSGW_LENGTH_LENGTH;
			ByteSmsgwUtil.makeBytesMsg(packet, offset, bind.getId().getBytes(), SMSGWMessageConst.ID_LENGTH);
			offset+=SMSGWMessageConst.ID_LENGTH;
			ByteSmsgwUtil.makeBytesMsg(packet, offset, bind.getPwd().getBytes(), SMSGWMessageConst.PWD_LENGTH);
			offset+=SMSGWMessageConst.PWD_LENGTH;
			BindAck bindAck = new BindAck();
			while(!isBind){
				//Log4J.println (Log4J.INFO, "[SMSGWConnection.open()] SEND BIND MSG\n"+ByteSmsgwUtil.writeHexaLog(packet));
				logger.info("[SMSGWConnection.open()] SEND BIND MSG\n"+bind.toString());
				send(packet);
				bindAck=(BindAck)readBind();
				logger.info("[SMSGWConnection.open()] GET BIND_ACK MSG\n"+bindAck.toString());
				if(bindAck.getResult()== SMSGWMessageConst.E_OK){
					isBind = true;
				}else{
					Thread.sleep(5000);
					close();
				}
			}
		}catch (InvalidMessageException e){
			e.printStackTrace();
			close();
		}catch (NotEnoughDataInByteBufferException e){
			e.printStackTrace();
			close();
		}catch (InterruptedException e){
			e.printStackTrace();
			close();
		}catch (Exception e){
			e.printStackTrace();
			close();
		}
	}
	public boolean close() throws IOException
	{
		try
		{
			if(socket!=null)
			{
				socket.close();
			}
			socket = null;
			opened = false;

			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new IOException("[SMSGWConnection.close()] IOException closing socket "+e.getMessage());
		}
	}
	public void send(byte[] data) throws SmsGWConnectionException, IOException
	{
		if(socket==null)
			throw new SmsGWConnectionException("[SMSGWConnection.send()]Connection Closed");
		try
		{
			OutputStream os = socket.getOutputStream();
			os.write(data);
			os.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new IOException("[SMSGWConnection.send()]IOException sending data "+e.getMessage());
		}
		catch(NullPointerException npe)
		{
			npe.printStackTrace();
			throw new SmsGWConnectionException("[SMSGWConnection.send()] Connection Closed");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SmsGWConnectionException("[SMSGWConnection.send()] Exception "+e.getMessage());
		}
	}
	
	public SMSGWMessage readBind() throws SmsGWConnectionException, IOException, InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		try
		{
			SMSGWMessage smsgwMsg = null;
			if (socket == null)
				throw new SmsGWConnectionException("[SMSGWConnection.read()]Connection Closed");
			socket.setSoTimeout((int)receiveTimeout);
			InputStream is = socket.getInputStream ();
			//Read Header
			ByteBuffer headerBuffer = readHeader(is);
			if(headerBuffer==null || headerBuffer.length()!=SMSGWMessageConst.HEADER_LENGTH)
				throw new SmsGWConnectionException("[SMSGWConnection.read()] Header is Abnormal");
			//Make Header
			SMSGWHeader header = new SMSGWHeader(headerBuffer);
			if( header.getMessageLength() ==-1)
				throw new SmsGWConnectionException ("get body length Abnormal"+header.getMessageLength());
			//Read Body
			ByteBuffer bodyBuffer = readBody(is, header.getMessageLength());
			if(bodyBuffer==null || bodyBuffer.length()!=header.getMessageLength())
				throw new SmsGWConnectionException("[SMSGWConnection.read()] Body is Abnormal");
			//Make Message
			smsgwMsg = makeMessage( header, bodyBuffer);
			return smsgwMsg;
		}
		catch(SocketTimeoutException ste)
		{
			//timeout 시 send Bind Msg
			logger.info("[SMSGWConnection.read()] Send Bind Timeout");
			return null;
		}
		catch(IOException e)
		{
			throw new IOException("[SMSGWConnection.read()]IOException receive via SCPIPConnection "+e.getMessage());
		}
	}
	private ByteBuffer readHeader(InputStream is) throws SmsGWConnectionException, SocketTimeoutException, IOException
	{
		try
		{
			if (socket == null || is == null) {
				throw new SmsGWConnectionException("[SMSGWConnection.readHeader()]Connection Closed");
			}
			byte [] smsgwHdr = new byte [SMSGWMessageConst.HEADER_LENGTH];
			while(true)
			{
				for (int i = 0; i < SMSGWMessageConst.HEADER_LENGTH; i++)
				{
					int read = is.read ();
					if(read == -1) {
						close();
						throw new SmsGWConnectionException("[SMSGWConnection.readHeader()]Connection Closed");
						
					}
					smsgwHdr[i] = (byte)read;
				}
				ByteBuffer buffer = new ByteBuffer(smsgwHdr);
				return buffer;
			}
		}
		catch(SocketTimeoutException ste)
		{
			throw new SocketTimeoutException("[SMSGWConnection.readHeader()]SocketTimeoutException readHeader "+ste.getMessage());
		}
		catch (IOException e)
		{
			
			throw new IOException("[SMSGWConnection.readHeader()]IOException readHeader "+e.getMessage());
		}
	}
	private ByteBuffer readBody(InputStream is, int bodyLen) throws SmsGWConnectionException, IOException
	{
		try
		{
			if (socket == null || is == null)
				throw new SmsGWConnectionException("[SMSGWConnection.readBody()]Connection Closed");
			if(bodyLen == 0)
				logger.info("[SMSGWConnection.readBody()] bodyLen == 0");
			byte [] smsgwBody = new byte [bodyLen];
			while(true)
			{
				try
				{
					for (int i = 0; i < bodyLen; i++)
					{
						int read = is.read ();
						if(read == -1)
							throw new SmsGWConnectionException("[SMSGWConnection.readBody()]Connection Closed");
						smsgwBody[i] = (byte)read;
					}
					ByteBuffer buffer = new ByteBuffer(smsgwBody);
					return buffer;
				}
				catch(SocketTimeoutException ste)
				{
					throw new IOException(ste.getMessage());
				}
				catch (IOException e)
				{
					throw e;
				}
			}
		}
		catch (IOException e)
		{
			throw new IOException("[SMSGWConnection.readBody()]IOException At readBody "+e.getMessage());
		}
	}
	private SMSGWMessage makeMessage(SMSGWHeader header, ByteBuffer bodyBuffer) throws SmsGWConnectionException, IOException
	{
		try
		{
			int messgeType = header.getMessageType();
			SMSGWMessage smsgwMsg = null;
			switch(messgeType)
			{
				case SMSGWMessageConst.LINK_SEND:
					smsgwMsg = new Link_Send();
					smsgwMsg.setHeader(header);
					return smsgwMsg;
				case SMSGWMessageConst.LINK_RECV:
					smsgwMsg = new Link_Recv();
					smsgwMsg.setHeader(header);
					return smsgwMsg;
				case SMSGWMessageConst.BIND:
					smsgwMsg = new Bind();
					smsgwMsg.parseData (header, bodyBuffer);
					return smsgwMsg;
				case SMSGWMessageConst.BIND_ACK:
					smsgwMsg = new BindAck();
					smsgwMsg.parseData (header, bodyBuffer);
					return smsgwMsg;
				case SMSGWMessageConst.DELIVER:
					smsgwMsg = new Deliver();
					smsgwMsg.parseData (header, bodyBuffer);
					return smsgwMsg;
				case SMSGWMessageConst.DELIVER_ACK:
					smsgwMsg = new DeliverAck();
					smsgwMsg.parseData (header, bodyBuffer);
					return smsgwMsg;
				case SMSGWMessageConst.REPORT:
					smsgwMsg = new Report();
					smsgwMsg.parseData (header, bodyBuffer);
					return smsgwMsg;
				case SMSGWMessageConst.REPORT_ACK:
					smsgwMsg = new ReportAck();
					smsgwMsg.parseData (header, bodyBuffer);
					return smsgwMsg;
				default:
					return smsgwMsg;
			}
		}
		catch (Exception e)
		{
			throw new SmsGWConnectionException("[SMSGWConnection.makeMessage()] make, decode, Exception "+e.getMessage());
		}
	}
	public SMSGWMessage read() throws SmsGWConnectionException, IOException, InvalidMessageException, NotEnoughDataInByteBufferException, UnsupportedEncodingException
	{
		try
		{
			SMSGWMessage smsgwMsg = null;
			if (socket == null)
				throw new SmsGWConnectionException("[SMSGWConnection.read()]Connection Closed");
			socket.setSoTimeout((int)receiveTimeout);

			InputStream is = socket.getInputStream ();
			//Read Header
			ByteBuffer headerBuffer = readHeader(is);
			if(headerBuffer==null || headerBuffer.length()!=SMSGWMessageConst.HEADER_LENGTH)
				throw new SmsGWConnectionException("[SMSGWConnection.read()] Header is Abnormal");

			//Make Header
			SMSGWHeader header = new SMSGWHeader(headerBuffer);
			if( header.getMessageLength() ==-1)
				throw new SmsGWConnectionException ("get body length Abnormal"+header.getMessageLength());
			if(header.getMessageType()==SMSGWMessageConst.LINK_RECV)
			{
				smsgwMsg = makeMessage(header,null);
				return smsgwMsg;
			}
			//Read Body
			ByteBuffer bodyBuffer = readBody(is, header.getMessageLength());
			if(bodyBuffer==null || bodyBuffer.length()!=header.getMessageLength())
				throw new SmsGWConnectionException("[SMSGWConnection.read()] Body is Abnormal");
			//Make Message
			smsgwMsg = makeMessage( header, bodyBuffer);
			return smsgwMsg;
		}catch(SocketTimeoutException ste){
			//timeout 시 send Link Msg
			logger.info("[SMSGWConnection.read()] Send Link Msg");
			if(isPing)
			{
				close();
				isPing = false;
				throw new IOException("Connection is not valid as no ping response!");
			}
			else if(sendLink())
			{
				isPing = true;
				return null;
			}
			else
			{
				close();
				throw new IOException("Connection is not valid as Link Error!");
			}
		}
		catch(IOException e)
		{
			throw new IOException("[SMSGWConnection.read()]IOException receive via SCPIPConnection "+e.getMessage());
		}
	}
	private boolean sendLink()
	{
		try
		{
			if (socket == null)
				throw new SmsGWConnectionException("[SMSGWConnection.read()]Connection Closed");
			byte packet[] = new byte[SMSGWMessageConst.HEADER_LENGTH + SMSGWMessageConst.SMSGW_LINK_BODYLENGTH];
			int offset =0;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.LINK_SEND);
			offset+=SMSGWMessageConst.SMSGW_TYPE_LENGTH;
			ByteSmsgwUtil.makeIntMsg(packet, offset, SMSGWMessageConst.SMSGW_LINK_BODYLENGTH);
			offset+=SMSGWMessageConst.SMSGW_LENGTH_LENGTH;
			//Log4J.println (Log4J.INFO, "[SMSGWConnection.sendLink()] SEND LINK_SEND MSG\n"+ByteSmsgwUtil.writeHexaLog(packet));
			logger.info("[SMSGWConnection.sendLink()] SEND LINK_SEND MSG\n");
			send(packet);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public void setLink(boolean data)
	{
		isPing = data;
	}
}

