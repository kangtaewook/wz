package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class TypeExtension implements PacketExtension{

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "type";
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return "galuster";
	}

	@Override
	public String toXML()
	{
		StringBuilder buf = new StringBuilder();

		if( type != null ){
			buf.append("<type");
			buf.append(" xmlns=\"").append(getNamespace()).append("\">");
			buf.append(type);
			buf.append("</type>");
		}
		return buf.toString();
	}

	

}
