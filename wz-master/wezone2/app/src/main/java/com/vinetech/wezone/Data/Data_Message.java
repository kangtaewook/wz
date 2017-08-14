package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;


public class Data_Message extends Data_Base
{

	@SerializedName("id")
	public String id;

	@SerializedName("username")
	public String username;

	@SerializedName("uuid")
	public String uuid;

	@SerializedName("sfrom")
	public String sfrom;

	@SerializedName("timestamp")
	public String timestamp;

	@SerializedName("peer")
	public String peer;

	@SerializedName("bare_peer")
	public String bare_peer;

	@SerializedName("xml")
	public String xml;

	@SerializedName("txt")
	public String txt;

	@SerializedName("kind")
	public String kind;

	@SerializedName("nick")
	public String nick;

	@SerializedName("created_at")
	public String created_at;

	@SerializedName("message_type")
	public String message_type;

	@SerializedName("is_read")
	public String is_read;

	@SerializedName("img_url")
	public String img_url;

	@SerializedName("user_name")
	public String user_name;

	@SerializedName("msgkey")
	public String msgkey;

}
