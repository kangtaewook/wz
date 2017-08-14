package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_BeaconInfo;
import com.vinetech.wezone.Data.Data_ServerInfo;
import com.vinetech.wezone.Data.Data_UnReadUserList;
import com.vinetech.wezone.Data.Data_UserInfo;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_PostLogin extends Rev_Base{

    @SerializedName("user_info")
    public Data_UserInfo user_info;

    @SerializedName("server_info")
    public ArrayList<Data_ServerInfo> server_info;

    @SerializedName("beacon_info")
    public ArrayList<Data_BeaconInfo> beacon_info;

    @SerializedName("userlist")
    public Data_UnReadUserList userlist;

}
