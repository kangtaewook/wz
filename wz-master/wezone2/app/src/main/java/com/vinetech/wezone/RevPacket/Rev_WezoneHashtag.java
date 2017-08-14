package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Hash;

import java.util.List;

/**
 * Created by galuster on 2017-03-16.
 */
public class Rev_WezoneHashtag extends Rev_Base {


    @SerializedName("list")
    public List<Data_Hash> list;
}
