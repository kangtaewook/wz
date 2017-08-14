package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by galuster3 on 2017-02-24.
 */

public class Data_Board extends Data_Base{

    @SerializedName("board_id")
    public String board_id;

    @SerializedName("wezone_id")
    public String wezone_id;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("notice_flag")
    public String notice_flag;

    @SerializedName("content")
    public String content;

    @SerializedName("board_file")
    public List<Data_File> board_file;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("create_datetime")
    public String create_datetime;

    @SerializedName("update_datetime")
    public String update_datetime;

    @SerializedName("comment_total_count")
    public String comment_total_count;

    @SerializedName("comment")
    public List<Data_Comment> comment;


    public boolean isSelected;
    public boolean isSelectedname;
    public boolean isSelected_ONOFF;

}
