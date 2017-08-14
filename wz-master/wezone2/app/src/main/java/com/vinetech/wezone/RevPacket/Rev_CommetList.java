package com.vinetech.wezone.RevPacket;

import com.google.gson.annotations.SerializedName;
import com.vinetech.wezone.Data.Data_Comment;

import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Rev_CommetList extends Rev_Base{

    @SerializedName("list")
    public List<Data_Comment> list;
}
