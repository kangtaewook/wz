package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;


public class Data_OtherUser extends Data_Base
{
	@SerializedName("user_uuid")
	public String user_uuid;

	@SerializedName("kind")
	public String kind;

	@SerializedName("img_url")
	public String img_url;

	@SerializedName("user_name")
	public String user_name;

}
