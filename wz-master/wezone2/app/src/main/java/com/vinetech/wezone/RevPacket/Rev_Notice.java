package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Notice;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_Notice extends Rev_Base {

    @SerializedName("list")
    public ArrayList<Data_Notice> list;
}
