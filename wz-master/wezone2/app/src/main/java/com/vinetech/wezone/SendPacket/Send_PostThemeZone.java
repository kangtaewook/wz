package com.vinetech.wezone.SendPacket;

import com.vinetech.wezone.Data.Data_ActionItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by galuster on 2017-02-20.
 */

public class Send_PostThemeZone {
    public String img_url;
    public String bg_img_url;
    public String title;
    public String theme_id;

    public String push_flag;

    public ArrayList<Data_ActionItem> theme_in;
    public ArrayList<Data_ActionItem> theme_out;

    public ArrayList<HashMap<String,String>> beacon;
}
