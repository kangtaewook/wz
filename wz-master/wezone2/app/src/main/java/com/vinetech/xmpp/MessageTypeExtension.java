package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class MessageTypeExtension implements PacketExtension{
	private String message_type;

	public String getMessageType() {
		return message_type;
	}

	public void setMessageType(String message_type) {
		this.message_type = message_type;
	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "message_type";
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
		if(message_type != null){
			buf.append("<message_type");
			buf.append(" xmlns=\"").append(getNamespace()).append("\">");
			buf.append(message_type);
			buf.append("</message_type>");
		}
		return buf.toString();
	}

	

}
