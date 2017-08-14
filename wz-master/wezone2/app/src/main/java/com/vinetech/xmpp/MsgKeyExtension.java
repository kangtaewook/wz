package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class MsgKeyExtension implements PacketExtension{

	private String msgkey;

	public String getMsgKey() {
		return msgkey;
	}

	public void setMsgKey(String msgkey) {
		this.msgkey = msgkey;
	}
	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "msgkey";
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return "wezone";
	}

	@Override
	public String toXML()
	{
		StringBuilder buf = new StringBuilder();

		if( msgkey != null ){
			buf.append("<msgkey");
			buf.append(" xmlns=\"").append(getNamespace()).append("\">");
			buf.append(msgkey);
			buf.append("</msgkey>");
		}
		return buf.toString();
	}

	

}
