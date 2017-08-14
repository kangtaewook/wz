package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class FromExtension implements PacketExtension{

	private String from;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "from";
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

		if( from != null ){
			buf.append("<from");
			buf.append(" xmlns=\"").append(getNamespace()).append("\">");
			buf.append(from);
			buf.append("</from>");
		}
		return buf.toString();
	}

	

}
