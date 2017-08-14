package com.vinetech.wezone.Data;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Data_Google extends Data_Base {



	/**
	 * 
	 */
	private static final long serialVersionUID = -7565586549900010726L;

	@SerializedName("id")
	public String id;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("given_name")
	public String given_name;
	
	@SerializedName("family_name")
	public String family_name;
	
	@SerializedName("link")
	public String link;
	
	@SerializedName("picture")
	public Uri picture;
	
	@SerializedName("gender")
	public String gender;
	
	@SerializedName("locale")
	public String locale;

	@SerializedName("email")
	public String email;

}
