package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

public class Data_PushData extends Data_Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3826594323015060087L;
	
	@SerializedName("sender_url")
	public String sender_url;
	
	@SerializedName("id")
	public String id;

	@SerializedName("zone_id")
	public String zone_id;

	@SerializedName("kind")
	public String kind;

	@SerializedName("sender_id")
	public String sender_id;

	@SerializedName("sender_name")
	public String sender_name;

	@SerializedName("receiver_id")
	public String receiver_id;

	@SerializedName("content")
	public String content;

	@SerializedName("item_id")
	public String item_id;

	@SerializedName("type")
	public String type;

}
