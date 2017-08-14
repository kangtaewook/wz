package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;


public class CustomExtension implements PacketExtension{

	private String user_uuid;
	private String user_name;
	private String user_img_url;

	public void setUserUuid(String uuid){
		this.user_uuid = uuid;
	}

	public void setUserName(String name){
		this.user_name = name;
	}

	public void setUserImageUrl(String img_url){
			this.user_img_url = img_url;
	}

	public String getUser_uuid(){
		return this.user_uuid;
	}

	public String getUserName(){
		return this.user_name;
	}

	public String getUserImgUrl(){
		return this.user_img_url;
	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "wezone";
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return "wezone:m";
	}

	@Override
	public String toXML()
	{

		StringBuilder buf = new StringBuilder();

        buf.append("<wezone");
        buf.append(" xmlns=\"").append(getNamespace()).append("\">");
        
        if(user_uuid != null){
        	buf.append("<user_uuid>").append(user_uuid).append("</user_uuid>");
        }
        if( user_name != null ){
        	buf.append("<user_name>").append(user_name).append("</user_name>");
        }
		if(user_uuid != null){
			buf.append("<user_uuid>").append(user_uuid).append("</user_uuid>");
		}
		if( user_name != null ){
			buf.append("<user_name>").append(user_name).append("</user_name>");
		}
		if(user_img_url != null){
			buf.append("<user_img_url>").append(user_img_url).append("</user_img_url>");
		}
        buf.append("</wezone>");
		return buf.toString();
	}

	

}
