package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_UserInfo;

/**
 * Created by galuster3 on 2017-02-09.
 */

public class Rev_UserInfo extends Rev_Base{

    @SerializedName("user_info")
    public Data_UserInfo user_info;

}
