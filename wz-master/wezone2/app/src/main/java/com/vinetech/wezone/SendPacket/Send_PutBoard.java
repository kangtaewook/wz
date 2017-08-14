package com.vinetech.wezone.SendPacket;

import com.vinetech.wezone.Data.Data_File;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-02-24.
 */

public class Send_PutBoard {

    public static final String FLAG_CONTENTS = "C";
    public static final String FLAG_FILE = "B";
    public static final String FLAG_ALL = "A";

    public String board_id;
    public String put_flag;
    public String content;
    public ArrayList<Data_File> board_file;
}
