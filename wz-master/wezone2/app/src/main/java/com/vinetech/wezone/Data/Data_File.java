package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-24.
 */

public class Data_File extends Data_Base {

    public static final String FILE_TYPE_PHOTO = "1";
    public static final String FILE_TYPE_VIDEO = "2";
    public static final String FILE_TYPE_AUDIO = "3";

    @SerializedName("url")
    public String url;

    @SerializedName("type")
    public String type;

    @SerializedName("format")
    public String format;

}
