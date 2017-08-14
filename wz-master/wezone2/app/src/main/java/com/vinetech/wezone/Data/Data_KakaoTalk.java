package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

public class Data_KakaoTalk extends Data_Base {

	/**
	 *
	 */
	private static final long serialVersionUID = -3933623795895552581L;

	@SerializedName("id")
	public String id;

	@SerializedName("uuid")
	public String uuid;
	
	@SerializedName("nickname")
	public String nickname;

	
	@SerializedName("thumbnailImagePath")
	public String thumbnailImagePath;

	@SerializedName("profileImagePath")
	public String profileImagePath;

	
}
