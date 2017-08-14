package com.vinetech.wezone.SendPacket;

import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Beacon_id;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-02-20.
 */

public class Send_PostWezone {
    public String title;
    public String introduction;
    public String img_url;
    public String bg_img_url;
    public String longitude;
    public String latitude;
    public String wezone_type;
    public String member_limit;
    public String location_type;

    public ArrayList<Data_Beacon_id> beacon;
    public String location_data;
    public String push;

    public ArrayList<Data_ActionItem> zone_in;
    public ArrayList<Data_ActionItem> zone_out;


}
