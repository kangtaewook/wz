package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Message;
import com.vinetech.wezone.Data.Data_OtherUser;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_MessageList extends Rev_Base {

    @SerializedName("message")
    public List<Data_Message> message;

    @SerializedName("other")
    public Data_OtherUser other;

    @SerializedName("push_flag")
    public String push_flag;
}
