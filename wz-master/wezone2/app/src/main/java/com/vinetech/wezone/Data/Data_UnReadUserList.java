package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-02-14.
 */

public class Data_UnReadUserList extends Data_Base{

    @SerializedName("total_count")
    public String total_count;

    @SerializedName("total_unread")
    public String total_unread;

    @SerializedName("list")
    public ArrayList<Data_Chat_UserList> list;


}
