package com.vinetech.wezone;

import com.vinetech.wezone.Data.Send_PutBeacon;
import com.vinetech.wezone.RevPacket.Rev_Advert;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_Beacon;
import com.vinetech.wezone.RevPacket.Rev_BeaconList;
import com.vinetech.wezone.RevPacket.Rev_Board;
import com.vinetech.wezone.RevPacket.Rev_BoardList;
import com.vinetech.wezone.RevPacket.Rev_BunnyzoneList;
import com.vinetech.wezone.RevPacket.Rev_ChatUserList;
import com.vinetech.wezone.RevPacket.Rev_CommetList;
import com.vinetech.wezone.RevPacket.Rev_EmailHelp;
import com.vinetech.wezone.RevPacket.Rev_FriendList;
import com.vinetech.wezone.RevPacket.Rev_GetServerInfo;
import com.vinetech.wezone.RevPacket.Rev_MessageList;
import com.vinetech.wezone.RevPacket.Rev_Notice;
import com.vinetech.wezone.RevPacket.Rev_PostBeacon;
import com.vinetech.wezone.RevPacket.Rev_PostBoard;
import com.vinetech.wezone.RevPacket.Rev_PostBunnyZone;
import com.vinetech.wezone.RevPacket.Rev_PostComment;
import com.vinetech.wezone.RevPacket.Rev_PostLogin;
import com.vinetech.wezone.RevPacket.Rev_Theme;
import com.vinetech.wezone.RevPacket.Rev_ThemeList;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.RevPacket.Rev_UserInfo;
import com.vinetech.wezone.RevPacket.Rev_WeZone;
import com.vinetech.wezone.RevPacket.Rev_WezoneHashtag;
import com.vinetech.wezone.RevPacket.Rev_WezoneList;
import com.vinetech.wezone.RevPacket.Rev_Wezoninfo;
import com.vinetech.wezone.RevPacket.Rev_ZoneMemberList;
import com.vinetech.wezone.RevPacket.Rev_user;
import com.vinetech.wezone.SendPacket.Send_CheckCode;
import com.vinetech.wezone.SendPacket.Send_PostBeacon;
import com.vinetech.wezone.SendPacket.Send_PostBoard;
import com.vinetech.wezone.SendPacket.Send_PostBunnyZone;
import com.vinetech.wezone.SendPacket.Send_PostComment;
import com.vinetech.wezone.SendPacket.Send_PostEmail;
import com.vinetech.wezone.SendPacket.Send_PostEmailHelp;
import com.vinetech.wezone.SendPacket.Send_PostEmailNoti;
import com.vinetech.wezone.SendPacket.Send_PostLogin;
import com.vinetech.wezone.SendPacket.Send_PostRegiste;
import com.vinetech.wezone.SendPacket.Send_PostThemeZone;
import com.vinetech.wezone.SendPacket.Send_PostWezone;
import com.vinetech.wezone.SendPacket.Send_PutBeaconArray;
import com.vinetech.wezone.SendPacket.Send_PutBoard;
import com.vinetech.wezone.SendPacket.Send_PutBunnyZone;
import com.vinetech.wezone.SendPacket.Send_PutComment;
import com.vinetech.wezone.SendPacket.Send_PutFriend;
import com.vinetech.wezone.SendPacket.Send_PutMyTheme;
import com.vinetech.wezone.SendPacket.Send_PutMyWezone;
import com.vinetech.wezone.SendPacket.Send_PutPushFlag;
import com.vinetech.wezone.SendPacket.Send_PutPw;
import com.vinetech.wezone.SendPacket.Send_PutRead;
import com.vinetech.wezone.SendPacket.Send_PutShare;
import com.vinetech.wezone.SendPacket.Send_PutThemeZone;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneMix;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneWithArray;
import com.vinetech.wezone.SendPacket.Send_PutUser;
import com.vinetech.wezone.SendPacket.Send_PutWezone;
import com.vinetech.wezone.SendPacket.Send_PutWezoneDelete;
import com.vinetech.wezone.SendPacket.Send_PutWezoneWithArray;
import com.vinetech.wezone.SendPacket.Send_PutWezoneWithArrayBeacon;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by galuster3 on 2017-01-23.
 *
 * 네트워크 통신에서 사용되는 모든 API 정의
 *
 */

public interface WezoneRestful {

    @GET("common/serverinfo/uuid/{uuid}")
    Call<Rev_GetServerInfo> getServerInfo(@Path("uuid") String uuid);

//    advert
    @GET("common/advert")
    Call<Rev_Advert> getAdvert();


    /**
     * Common
     */
    @Multipart
    @POST("file/upload")
    Call<Rev_Upload> upload(@Query("type") String type, @Query("status") String status, @Query("id") String id, @Part MultipartBody.Part file);

    /**
     * Login
     */
    @POST("login/checkcode")
    Call<Rev_Base> checkCode(@Body Send_CheckCode param);

    @POST("login/login")
    Call<Rev_PostLogin> postLogin(@Body Send_PostLogin login);

    @DELETE("login/logout/{device_id}")
    Call<Rev_Base> postLogout(@Path("device_id") String device_id);

    @POST("login/register")
    Call<Rev_Base> postRegister(@Body Send_PostRegiste param);

    @FormUrlEncoded
    @POST("login/usermail")
    Call<Rev_Base> postUserMail(@Field("email") String email);


    /**
     *  user
     */
    @GET("user/user/other_uuid/{other_uuid}/longitude/{longitude}/latitude/{latitude}/")
    Call<Rev_UserInfo>  getUserInfo(@Path("other_uuid") String other_uuid,@Path("longitude") String longitude,@Path("latitude") String latitude);

    @GET("user/user/other_uuid/{other_uuid}/")
    Call<Rev_UserInfo>  getUserInfo(@Path("other_uuid") String other_uuid);

    @GET("user/friends/offset/{offset}/limit/{limit}")
    Call<Rev_FriendList>  getFriendList(@Path("offset") String offset, @Path("limit") String limit);

    @GET("user/friends/keyword/{keyword}/offset/{offset}/limit/{limit}")
    Call<Rev_FriendList>  getFriendList(@Path("keyword") String keyword, @Path("offset") String offset, @Path("limit") String limit);

    @PUT("user/friend")
    Call<Rev_Base> putFriend(@Body Send_PutFriend param);

    @PUT("user/user/")
    Call<Rev_user> putUser(@Body Send_PutUser param);

    @PUT("user/pw/")
    Call<Rev_Base>putPw(@Body Send_PutPw param);

    @GET("user/friends/keyword/{keyword}")
    Call<Rev_FriendList>  getFriendSeachList(@Path("keyword") String keyword);

//
//    @GET("user/users/zone_id/{zone_id}/zone_type/{zone_type}/latitude/{latitude}/longitude/{longitude}")
//    Call<Rev_ZoneMemberList> getZoneMemeberList(@Path("zone_id") String zone_id, @Path("zone_type") String zone_type, @Path("latitude") String latitude, @Path("longitude") String longitude);

    @GET("user/users/zone_id/{zone_id}/zone_type/{zone_type}/offset/{offset}/limit/{limit}/latitude/{latitude}/longitude/{longitude}")
    Call<Rev_ZoneMemberList> getZoneMemeberList(@Path("zone_id") String zone_id, @Path("zone_type") String zone_type, @Path("offset") String offset, @Path("limit") String limit, @Path("latitude") String latitude, @Path("longitude") String longitude);

    /**
     * theme
     */
    @GET("theme/themes/")
    Call<Rev_ThemeList>  getThemeList();

    @GET("theme/mythemes/")
    Call<Rev_ThemeList>  getMyThemeList();

    @PUT("theme/mytheme/")
    Call<Rev_Base> putTheme(@Body Send_PutMyTheme param);

    @DELETE("theme/mytheme/{theme_id}")
    Call<Rev_Base> deleteTheme(@Path("theme_id") String theme_id);

    @GET("theme/themezones/")
    Call<Rev_ThemeList>  getMyThemeZoneList();

    @GET("theme/themezone/theme_id/{theme_id}")
    Call<Rev_Theme>  getMyThemeZone(@Path("theme_id") String theme_id);

    @POST("theme/themezone/")
    Call<Rev_Base>  postThemeZone(@Body Send_PostThemeZone param);

    @PUT("theme/themezone/")
    Call<Rev_Base>  putThemeZoneWithValue(@Body Send_PutThemeZone param);

    @PUT("theme/themezone/")
    Call<Rev_Base>  putThemeZoneWithArray(@Body Send_PutThemeZoneWithArray param);

    @PUT("theme/themezone/")
    Call<Rev_Base>  putThemeZoneWithMix(@Body Send_PutThemeZoneMix param);

    /**
     * bunnyzone
     */
    @GET("bunnyzone/bunnyzones/")
    Call<Rev_BunnyzoneList> getBunnyzoneList();

    @POST("bunnyzone/bunnyzone")
    Call<Rev_PostBunnyZone> postBunnyZone(@Body Send_PostBunnyZone param);

    @PUT("bunnyzone/bunnyzone")
    Call<Rev_Base> putBunnyZone(@Body Send_PutBunnyZone param);

    /**
     * Beacon
     * */
    @GET("common/beacon/uuid/{uuid}/mac/{mac}/beacon_uuid/{beacon_uuid}/beacon_major/{beacon_major}/beacon_minor/{beacon_minor}")
    Call<Rev_Beacon> getBeacon(@Path("uuid") String uuid, @Path("mac") String mac,@Path("beacon_uuid") String beacon_uuid,@Path("beacon_major") String beacon_major,@Path("beacon_minor") String beacon_minor);

    @GET("beacon/beacon/beacon_id/{beacon_id}")
    Call<Rev_Beacon> getBeacon(@Path("beacon_id") String beacon_id);

    @GET("beacon/beacons/")
    Call<Rev_BeaconList> getBeaconList();

    @POST("beacon/beacon")
    Call<Rev_PostBeacon> postBeacon(@Body Send_PostBeacon param);

    @DELETE("beacon/beacon/{beacon_id}")
    Call<Rev_Base> deleteBeacon(@Path("beacon_id") String beacon_id);

    @PUT("beacon/beacon")
    Call<Rev_Base> putBeaconArray(@Body Send_PutBeaconArray param);

    @PUT("beacon/beacon")
    Call<Rev_Base> puBeacon(@Body Send_PutBeacon param);

    @PUT("beacon/share")
    Call<Rev_Base> putBeaconShare(@Body Send_PutShare param);

    /**
     * Wezone
     * */
//    @GET("common/beacon/uuid/{uuid}/mac/{mac}")
//    Call<Rev_Beacon> getBeacon(@Path("uuid") String uuid, @Path("mac") String mac);

    @GET("/wezone/wezone/wezone_id/{wezone_id}/")
    Call<Rev_Wezoninfo> getWezone(@Path("wezone_id") String wezone_id);

    @GET("/wezone/wezone/wezone_id/{wezone_id}/longitude/{longitude}/latitude/{latitude}/")
    Call<Rev_Wezoninfo> getWezone(@Path("wezone_id") String wezone_id,@Path("longitude") String longitude,@Path("latitude") String latitude);

    @GET("/wezone/wezones/")
    Call<Rev_WezoneList> getWezoneList();

    @GET("/wezone/wezones/offset/{offset}/limit/{limit}/search_type/{search_type}/keyword/{keyword}/")
    Call<Rev_WezoneList> getWezoneSearchList(@Path("offset") String offset,
                                             @Path("limit") String limit,
                                             @Path("search_type") String search_type,
                                             @Path("keyword") String keyword);


    @GET("/wezone/wezones/longitude/{longitude}/latitude/{latitude}/offset/{offset}/limit/{limit}/search_type/{search_type}/keyword/{keyword}/")
    Call<Rev_WezoneList> getWezoneSearchList(@Path("longitude") String longitude,
                                             @Path("latitude") String latitude,
                                             @Path("offset") String offset,
                                             @Path("limit") String limit,
                                             @Path("search_type") String search_type,
                                             @Path("keyword") String keyword);

//    @GET("user/users/zone_id/{zone_id}/zone_type/{zone_type}/latitude/{latitude}/longitude/{longitude}")
//    Call<Rev_WezoneList> getDistance(@Path("zone_id") String zone_id, @Path("zone_type") String zone_type, @Path("latitude") String latitude,  @Path("longitude") String longitude );

    @GET("/wezone/mywezones/longitude/{longitude}/latitude/{latitude}/other_uuid/{other_uuid}/")
    Call<Rev_WezoneList> getMyWezoneList(@Path("longitude") String longitude, @Path("latitude") String latitude,@Path("other_uuid") String other_uuid);

    @GET("/wezone/mywezones/longitude/{longitude}/latitude/{latitude}/")
    Call<Rev_WezoneList> getMyWezoneList(@Path("longitude") String longitude, @Path("latitude") String latitude);

    @GET("/wezone/mywezones/other_uuid/{other_uuid}/")
    Call<Rev_WezoneList> getMyWezoneList(@Path("other_uuid") String other_uuid);

    @GET("/wezone/mywezones/")
    Call<Rev_WezoneList> getMyWezoneList();

    @POST("wezone/wezone/")
    Call<Rev_WeZone>postWeZone(@Body Send_PostWezone param);

    @PUT("wezone/wezone/")
    Call<Rev_Base>putWezone(@Body Send_PutWezone param);

    @PUT("wezone/wezone/")
    Call<Rev_Base>putWezoneWithArray(@Body Send_PutWezoneWithArray param);

    @PUT("wezone/wezone/")
    Call<Rev_Base>putWezoneWithArrayBeacon(@Body Send_PutWezoneWithArrayBeacon param);

    @PUT("wezone/mywezone")
    Call<Rev_Base>putWezoneDelete(@Body Send_PutWezoneDelete param);

    @DELETE("wezone/wezone/{wezone_id}")
    Call<Rev_Base>deleteWezoneDelete(@Path("wezone_id") String wezone_id);


    /**
     * Wezone Board
     */
    @GET("wezone/board/board_id/{board_id}")
    Call<Rev_Board> getWezoneBoard(@Path("board_id") String board_id);

    @POST("wezone/board/")
    Call<Rev_PostBoard>postWeZoneBoard(@Body Send_PostBoard param);

    @PUT("wezone/board/")
    Call<Rev_Base>putWeZoneBoard(@Body Send_PutBoard param);

    @GET("/wezone/boards/wezone_id/{wezone_id}/offset/{offset}/limit/{limit}/notice_flag/{notice_flag}")
    Call<Rev_BoardList> getBoardList(@Path("wezone_id") String wezone_id, @Path("offset") String offset, @Path("limit") String limit, @Path("notice_flag") String notice_flag);

    @GET("/wezone/boards/wezone_id/{wezone_id}/notice_flag/{notice_flag}")
    Call<Rev_BoardList> getBoardList(@Path("wezone_id") String wezone_id, @Path("notice_flag") String notice_flag);

    @GET("/wezone/boards/wezone_id/{wezone_id}/offset/{offset}/limit/{limit}")
    Call<Rev_BoardList> getBoardList(@Path("wezone_id") String wezone_id, @Path("offset") String offset, @Path("limit") String limit);

    @PUT("wezone/mywezone/")
    Call<Rev_Base> putMyWezone(@Body Send_PutMyWezone param);

    @DELETE("wezone/board/{board_id}")
    Call<Rev_Base> deleteWezone(@Path("board_id") String board_id);

    /**
     * Wezone Comment
     */
    @GET("/wezone/comments/wezone_id/{wezone_id}/type/{type}/board_id/{board_id}/offset/{offset}/limit/{limit}")
    Call<Rev_CommetList> getCommentList(@Path("wezone_id") String wezone_id, @Path("type") String type, @Path("board_id") String board_id, @Path("offset") String offset, @Path("limit") String limit);

    @GET("/wezone/comments/wezone_id/{wezone_id}/type/{type}/offset/{offset}/limit/{limit}")
    Call<Rev_CommetList> getCommentList(@Path("wezone_id") String wezone_id, @Path("type") String type, @Path("offset") String offset, @Path("limit") String limit);

    @POST("wezone/comment/")
    Call<Rev_PostComment> postComment(@Body Send_PostComment param);

    @PUT("wezone/comment/")
    Call<Rev_Base> putComment(@Body Send_PutComment param);


    /**
     *  Wezone Member
     */
    @GET("/wezone/members/wezone_id/{wezone_id}/offset/{offset}/limit/{limit}")
    Call<Rev_ZoneMemberList> getWezoneMemberList(@Path("wezone_id") String wezone_id, @Path("offset") String offset, @Path("limit") String limit);

    /**
     *  Wezone hashtags
     */
    @GET("/wezone/hashtags")
    Call<Rev_WezoneHashtag> getWezoneHashtag();


    /**
     * Chat
     */

    @GET("/chat/userlist/offset/{offset}/limit/{limit}")
    Call<Rev_ChatUserList> getChatUserList(@Path("offset") String offset, @Path("limit") String limit);

    @GET("chat/messages/other_uuid/{other_uuid}/kind/{kind}/offset/{offset}/limit/{limit}")
    Call<Rev_MessageList> getMessageList(@Path("other_uuid") String other_uuid, @Path("kind") String kind, @Path("offset") String offset, @Path("limit") String limit);

    @GET("chat/messages/other_uuid/{other_uuid}/kind/{kind}/offset/{offset}/limit/{limit}/msg_key/{msg_key}")
    Call<Rev_MessageList> getMessageList(@Path("other_uuid") String other_uuid, @Path("kind") String kind, @Path("offset") String offset, @Path("limit") String limit, @Path("msg_key") String msg_key);

    @PUT("chat/userlist/")
    Call<Rev_Base> putUserList(@Body Send_PutPushFlag param);

    @PUT("chat/read/")
    Call<Rev_Base> putRead(@Body Send_PutRead param);

    /**
     * Email
     */

    @POST("email/send")
    Call<Rev_Base> postEmail(@Body Send_PostEmail param);

    @POST("email/help")
    Call<Rev_EmailHelp> putEmailHelp(@Body Send_PostEmailHelp param);

    @POST("email/noti")
    Call<Rev_Base> postEmailNoti(@Body Send_PostEmailNoti param);

    /**
     * Push
     */

    @GET("/push/notis/offset/{offset}/limit/{limit}")
    Call<Rev_Notice> getNotice(@Path("offset") String offset, @Path("limit") String limit);
}