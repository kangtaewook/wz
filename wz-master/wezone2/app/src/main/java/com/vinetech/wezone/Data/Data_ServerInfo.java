package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster3 on 2017-02-09.
 */

public class Data_ServerInfo extends Data_Base{

    @SerializedName("server_name")
    public String server_name;

    @SerializedName("server_kind")
    public String server_kind;

    @SerializedName("server_ip")
    public String server_ip;

    @SerializedName("server_port")
    public String server_port;

    @SerializedName("server_dns")
    public String server_dns;

    @SerializedName("server_provider")
    public String server_provider;

    @SerializedName("groupchat_hostname")
    public String groupchat_hostname;

    @SerializedName("xmpp_hostname")
    public String xmpp_hostname;



}
