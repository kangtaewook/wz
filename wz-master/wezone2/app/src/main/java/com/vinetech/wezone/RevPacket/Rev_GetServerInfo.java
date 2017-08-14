package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_ServerInfo;

import java.util.List;

/**
 * Created by galuster3 on 2017-02-09.
 */

public class Rev_GetServerInfo {

    @SerializedName("server_info")
    public List<Data_ServerInfo> server_info;
}
