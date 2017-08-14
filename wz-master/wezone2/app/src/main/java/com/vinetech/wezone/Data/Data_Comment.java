package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-27.
 */

public class Data_Comment extends Data_Base{

    public static final String TYPE_WEZONE_COMMENT = "W";
    public static final String TYPE_WEZONE_BOARD_COMMENT = "B";

    @SerializedName("comment_id")
    public String comment_id;

    @SerializedName("wezone_id")
    public String wezone_id;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("type")
    public String type;

    @SerializedName("board_id")
    public String board_id;

    @SerializedName("content")
    public String content;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("update_datetime")
    public String update_datetime;

}
