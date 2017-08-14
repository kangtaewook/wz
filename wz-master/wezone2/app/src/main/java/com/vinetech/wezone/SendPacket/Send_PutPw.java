package com.vinetech.wezone.SendPacket;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galuster on 2017-03-08.
 */

public class Send_PutPw {
    @SerializedName("passwd")
    public String passwd;

    @SerializedName("new_passwd")
    public String new_passwd;
}
