package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-02-14.
 */

public class Data_UnRead extends Data_Base {

    @SerializedName("count")
    public String count;

    @SerializedName("msgkeys")
    public ArrayList<Data_MsgKey> msgkeys;

}
