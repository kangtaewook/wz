package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class DelayExtension implements PacketExtension{

	private String delay;

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}
	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "delay";
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return "urn:xmpp:delay";
	}

	@Override
	public String toXML()
	{
		StringBuilder buf = new StringBuilder();

		if( delay != null ){
			buf.append("<delay");
			buf.append(" xmlns=\"").append(getNamespace()).append("\">");
			buf.append(delay);
			buf.append("</delay>");
		}
		return buf.toString();
	}

	

}
