package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-03-06.
 */

public class Data_Chat_UserList extends Data_Base{

    @SerializedName("username")
    public String username;

    @SerializedName("other_uuid")
    public String other_uuid;

    @SerializedName("sfrom")
    public String sfrom;

    @SerializedName("kind")
    public String kind;

    @SerializedName("message_type")
    public String message_type;

    @SerializedName("read")
    public String read;

    @SerializedName("push_flag")
    public String push_flag;

    @SerializedName("id")
    public String id;

    @SerializedName("last_id")
    public String last_id;

    @SerializedName("txt")
    public String txt;

    @SerializedName("update_at")
    public String update_at;

    @SerializedName("create_at")
    public String create_at;

    @SerializedName("delete_flag")
    public String delete_flag;

    @SerializedName("img_url")
    public String img_url;

    @SerializedName("user_name")
    public String user_name;

    @SerializedName("unread")
    public Data_UnRead unread;

    @SerializedName("member_count")
    public String member_count;

    public boolean isSelected;

}
