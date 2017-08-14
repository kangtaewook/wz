package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

public class Data_Facebook extends Data_Base {

	/**
	 *
	 */
	private static final long serialVersionUID = -3933623795895552581L;

	@SerializedName("id")
	public String id;
	
	@SerializedName("firstName")
	public String firstName;
	
	@SerializedName("middleName")
	public String middleName;
	
	@SerializedName("lastName")
	public String lastName;
	
	@SerializedName("name")
	public String name;
	
//	@SerializedName("linkUri")
//	public Uri linkUri;

	public String imgUri;
	
}
