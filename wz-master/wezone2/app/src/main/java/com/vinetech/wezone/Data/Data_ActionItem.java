package com.vinetech.wezone.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-02-17.
 */

public class Data_ActionItem extends Data_Base{

    public static final String ID_NOT_USE = "0";
    public static final String ID_SOUND = "1";
    public static final String ID_PUSH_MSG = "2";
    public static final String ID_IMAGE = "3";
    public static final String ID_APP = "4";
    public static final String ID_EMAIL = "5";
    public static final String ID_CAMERA = "8";
    public static final String ID_SOS = "9";
    public static final String ID_NOTIC = "6";

    public static String getTitleText(String id){
        switch(id){
            case ID_NOT_USE:
                return "사용안함";

            case ID_SOUND:
                return "사운드 재생";

            case ID_PUSH_MSG:
                return "Push 메세지";

            case ID_IMAGE:
                return "이미지 보이기";

            case ID_APP:
                return "앱 실행";

            case ID_EMAIL:
                return "이메일 받기";

            case ID_CAMERA:
                return "카메라 실행 및 셔터";

            case ID_SOS:
                return "SOS 보내기";

            case ID_NOTIC:
                return "공지글";
            default:
                return "사용안함";
        }
    }

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("data")
    public ArrayList<Data_ActionItemData> data;

//    public boolean isSelected;
}
