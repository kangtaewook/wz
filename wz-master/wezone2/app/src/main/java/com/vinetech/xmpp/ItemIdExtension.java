package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class ItemIdExtension implements PacketExtension{

	private String item_id;

	public String getItemId() {
		return item_id;
	}

	public void setItemId(String itemid) {
		this.item_id = itemid;
	}
	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "item_id";
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

		if( item_id != null ){
			buf.append("<item_id");
			buf.append(" xmlns=\"").append(getNamespace()).append("\">");
			buf.append(item_id);
			buf.append("</item_id>");
		}
		return buf.toString();
	}

	

}
