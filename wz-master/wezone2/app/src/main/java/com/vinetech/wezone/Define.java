package com.vinetech.wezone;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class Define {
    public static final String LOG_TAG = "vinetech";
    public static final boolean LOG_YN = true;

    public static final int NET_CONNECT_TIMEOUT = 20;
    public static final int NET_READ_TIMEOUT = 30;

    public static final String BASE_URL = "https://app.wezon.com";

    public static String NETURL_TERNS_OF_SERVICE = "https://app.wezon.com/web/privacy.html";
    public static String NETURL_STAGE_TERNS_OF_SERVICE = "https://www-stage.langtudy.com/privacy_%s.html";

    public static String NETURL_BEACON_MARKET = "http://vinemall.co.kr/m2/goods/view.php?category=001001002&goodsno=36";

    public static String NETURL_EVENT = "http://vinemall.co.kr/m2/goods/goods_qna_list.php?isAll=Y";

    public static String DEFUALT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String GOOGLE_API_KEY = "AIzaSyCaCd3n55gtq7sg2ktrVxnFMkmzX45Pd1I";

    // -------------------------------------------------------------------------------------------------
    // 암호화
    // -------------------------------------------------------------------------------------------------
    public static final boolean CRYPT_CACHE_IMAGE_ENABLE = true;
    public static final boolean CRYPT_CHAT_ENABLE = true;
    public static final boolean CRYPT_ENABLE = (CRYPT_CACHE_IMAGE_ENABLE || CRYPT_CHAT_ENABLE);

    public static final boolean CRYPT_RSA = true;

    public static final String		SHARE_KEY_UUID				 = "SHARE_KEY_UUID";
    public static final String		SHARE_KEY_NAME				 = "SHARE_KEY_NAME";
    public static final String		SHARE_KEY_IMAGE_URL			 = "SHARE_KEY_IMAGE_URL";
    public static final String		SHARE_KEY_BEACON_ID				 = "SHARE_KEY_BEACON_ID";
    public static final String		SHARE_KEY_WEZONE_ID				 = "SHARE_KEY_WEZONE_ID";
    public static final String		SHARE_KEY_BUNNYZONE_ID				 = "SHARE_KEY_BUNNYZONE_ID";


    public static final String      SHARE_KEY_KEYBOARD_HEIGHT			     = "SHARE_KEY_KEYBOARD_HEIGHT";
    public static final String      SHARE_KEY_SCREEN_HEIGHT			         = "SHARE_KEY_SCREEN_HEIGHT";
    public static final String      SHARE_KEY_SCREEN_WIDTH			         = "SHARE_KEY_SCREEN_WIDTH";

    // -------------------------------------------------------------------------------------------------
    // 테마
    // -------------------------------------------------------------------------------------------------
    public static final int THEME_BLUE = 0;
    public static final int THEME_RED = 1;
    public static final int THEME_YELLOW = 2;

    public static final String WEZONE_THEME = "wezone_theme";


    public static final String INTENTKEY_PUSH_VALUE = "PUSH_VALUE";
    public static final String INTENTKEY_PUSH_MESSAGE = "PUSH_MESSAGE";

    public static final String      SHARE_KEY_PUSH_TOKEN		 =	 "SHARE_KEY_PUSH_TOKEN";

    public static final String     SHARE_KEY_STAGE_MODE = "SHARE_KEY_STAGE_MODE";

    public static final String      SHARE_KEY_BEACON_INFO = "SHARE_KEY_BEACON_INFO";

    public static final String      SHARE_KEY_PROVIDER_TYPE		 =	 "SHARE_KEY_PROVIDER_TYPE";
    public static final String      SHARE_KEY_PROVIDER_MAIL		 =	 "SHARE_KEY_PROVIDER_MAIL";
    public static final String      SHARE_KEY_PROVIDER_NAME		 =	 "SHARE_KEY_PROVIDER_NAME";
    public static final String      SHARE_KEY_PROVIDER_ID		 =	 "SHARE_KEY_PROVIDER_ID";
    public static final String      SHARE_KEY_LANGTUDY_ID		 =	 "SHARE_KEY_LANGTUDY_ID";
    public static final String      SHARE_KEY_LANGTUDY_PW		 =	 "SHARE_KEY_LANGTUDY_PW";


    public static final String INTENTKEY_USER_VALUE = "INTENTKEY_USER_VALUE";
    public static final String INTENTKEY_BEACONE_VALUE = "INTENTKEY_BEACONE_VALUE";
    public static final String INTENTKEY_THEME_VALUE = "INTENTKEY_THEME_VALUE";
    public static final String INTENTKEY_BEACONE_VALUE_2 = "INTENTKEY_BEACONE_VALUE_2";
    public static final String INTENTKEY_FRIEND_VALUE = "INTENTKEY_FRIEND_VALUE";
    public static final String INTENTKEY_DISTANCE_VALUE = "INTENTKEY_DISTANCE_VALUE";
    public static final String INTENTKEY_USER_VALUE_USERINFO = "INTENTKEY_USER_VALUE_USERINFO";
    public static final String INTENTKEY_USER_SOS_TYPE = "INTENTKEY_USER_SOS_TYPE";
    public static final String INTENTKEY_NOTICE_VALUE = "INTENTKEY_NOTICE_VALUE";
    public static final String INTENTKEY_NOTICE_COUNT_VALUE = "INTENTKEY_NOTICE_COUNT_VALUE";
    public static final String INTENTKEY_NOTICE_OFF = "INTENTKEY_NOTICE_OFF";



    public static final String INTENTKEY_LOCATION_VALUE = "INTENTKEY_LOCATION_VALUE";
    public static final String INTENTKEY_LOCATION_MESSAGE_VALUE = "INTENTKEY_LOCATION_MESSAGE_VALUE";
    public static final String INTENTKEY_LOCATION_TYPE_VALUE = "INTENTKEY_LOCATION_TYPE_VALUE";
    public static final String WEZONE_LIST_PROFILE_TYPE = "WEZONE_LIST_PROFILE_TYPE";
    public static final String WEZONE_LIST_MY_TYPE = "WEZONE_LIST_MY_TYPE";
    public static final String WEZONE_LIST_NEAR_TYPE = "WEZONE_LIST_NEAR_TYPE";


    public static final int INTENT_RESULT_PHOTO = 20;
    public static final int INTENT_RESULT_CAMERA = 30;
    public static final int INTENT_RESULT_PHOTO2 = 40;

    public static final int INTENT_RESULT_ALBUM_IMAGE = 110;

    public static final int INTENT_RESULT_IMAGE_EDIT = 120;
    public static final int INTENT_RESULT_IMAGE_CROP = 121;

    public static final int INTENT_RESULT_USER = 200;
    public static final int INTENT_RESULT_USER_SOS = 201;

    public static final int INTENT_RESULT_BEACONE = 300;
    public static final int INTENT_RESULT_START_BEACONE = 301;
    public static final int INTENT_RESULT_DFU_BEACON = 302;

    public static final int INTENT_RESULT_THEME = 310;

    public static final int INTENT_RESULT_THEME_ZONE = 320;

    public static final int INTENT_RESULT_WEZONE = 300;
    public static final int INTENT_RESULT_WEZONE_LOCATION = 301;
    public static final int INTENT_RESULT_WEZONE_DISTANCE = 302;
    public static final int INTENT_RESULT_WEZONE_IN = 303;
    public static final int INTENT_RESULT_WEZONE_HASH = 304;


    public static final int INTENT_RESULT_BUNNYZONE = 400;

    public static final int INTENT_RESULT_CHATTING = 500;

    public static final int INTENT_RESULT_MYPROFILEACTIVITY_EDIT = 601;
    public static final int INTENT_RESULT_MYPROFILEACTIVITY_VIEW = 602;

    public static final int INTENT_RESULT_LOCATION = 500;

    public static final int INTENT_RESULT_BOARD = 600;

    public static final int INTENT_RESULT_REVIEW = 700;

    public static final int INTENT_RESULT_WEBVIEW = 800;

    public static final int INTENT_RESULT_EDIT_TEXT = 900;
    public static final int INTENT_RESULT_GET_SOUND = 901;
    public static final int INTENT_RESULT_GET_SOUND_RING = 902;
    public static final int INTENT_RESULT_GET_APP = 904;
    public static final int INTENT_RESULT_NOTIFICATION = 903;
    public static final int INTENT_RESULT_APP = 904;
    public static final int INTENT_RESULT_EMAIL_PUSH = 907;
    public static final int INTENT_RESULT_EMAIL_PUSH_CONTENT = 908;
    public static final int INTENT_RESULT_NOTIFICATION_SOS = 911;

    public static final int INTENT_RESULT_EDIT_TEXT_PROFILE = 905;
    public static final int INTENT_RESULT_EDIT_TEXT_WEZONE = 906;

    public static final int INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE = 909;
    public static final int INTENT_RESULT_PROFILE_FRIEND_PULSE = 910;

    public static final int INTENT_RESULT_REQUEST_ENABLE_BLUETOOTH = 990;

    public static final String MESSAGE_TYPE_CHAT = "0";
    public static final String MESSAGE_TYPE_STICKCON = "1";
    public static final String MESSAGE_TYPE_COMMAND = "9";

    public static final String IMAGE_TYPE_BACKGROUND = "bg";
    public static final String IMAGE_TYPE_PROFILE = "pr";
    public static final String IMAGE_TYPE_BOARD = "bo";
    public static final String IMAGE_TYPE_BEACON = "bc";
    public static final String IMAGE_TYPE_BUNNYZONE = "bn";
    public static final String IMAGE_TYPE_WEZONE = "we";
    public static final String IMAGE_TYPE_THEMEZONE = "bn";
    public static final String IMAGE_TYPE_WEZONE_BACKGROUND = "wg";
    public static final String IMAGE_TYPE_SETTING_HELP = "he";


    public static final String IMAGE_STATUS_UPDATE = "1";
    public static final String IMAGE_STATUS_NEW = "2";


    public static final String TYPE_MANAGER = "M";
    public static final String TYPE_STAFF = "S";
    public static final String TYPE_NORMAL = "N";
    public static final String TYPE_INVITED = "W";
    public static final String TYPE_REFUSE = "R";
    public static final String TYPE_DELETE = "X";

    public static final String LOACTION_TYPE_B = "B";
    public static final String LOACTION_TYPE_G = "G";

    public static final String ZONE_POSSIBLE_T = "T";
    public static final String ZONE_POSSIBLE_F = "F";

    public static final String SOS_MESSAGE = "SOS_MESSAGE";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    // -------------------------------------------------------------------------------------------------
    // 이미지
    // -------------------------------------------------------------------------------------------------
    public static final int IMAGE_CONTENT_MAX_SIZE = 1280; // 가로 또는 세로 최대 1280

    public static final int CACHE_USER_IMAGE_DELETE_TIME = -15; // 15일


    //2초
    public static final int BEACON_SCAN_INTERVAL = 2000;

    public static final String BEACON_QUEUE_CNT = "BEACON_QUEUE_CNT";
    public static final int BEACON_QUEUE_CNT_DEFAULT = 5;
    public static final String BEACON_IN_RANGE = "BEACON_IN_RANGE";
    public static final int BEACON_IN_RANGE_DEFAULT = 65;
    public static final String BEACON_OUT_RANGE = "BEACON_OUT_RANGE";
    public static final int BEACON_OUT_RANGE_DEFAULT = 95;
    public static final String BEACON_OUT_TIME = "BEACON_OUT_TIME";
    public static final int BEACON_OUT_TIME_DEFAULT = 10;
    public static final String BEACON_BACKGROUND_OUT_TIME = "BEACON_BACKGROUND_OUT_TIME";
    public static final int BEACON_BACKGROUND_OUT_TIME_DEFAULT = 20;

    public static final String BEACON_REPLACE_NAME = "@beacon@";


    public static class Emoticon
    {
        public static final String[] ALL_TEXTS =
                {"(화남)","(충격)","(혼란)","(키키)","(엉엉)","(불끈)","(헤롱)","(러브)","(야호)","(부끄)","(오잉)","(캬악)",
                        "(흐흐)","(희죽)","(탈진)","(허걱)","(띠용)","(당황)","(갈증)","(거품)",


                        "(누리 노래)","(누리 웃음)","(누리 버럭)","(누리 멋짐)","(누리 씨익)",
                                "(누리 피곤)","(누리 제발)","(누리 궁금)","(누리 풉)","(누리 짱)",
                                "(누리 으악)","(누리 슬픔)","(누리 잠)","(누리 당근)","(누리 신남)",
                                "(누리 숨음)",


                        "(바니 노래)", "(바니 웃음)", "(바니 버럭)", "(바니 멋짐)", "(바니 씨익)",
                        "(바니 피곤)","(바니 제발)","(바니 궁금)", "(바니 풉)", "(바니 짱)",
                        "(바니 으악)", "(바니 슬픔)", "(바니 잠)", "(바니 당근)", "(바니 신남)",
                        "(바니 숨음)",

                        "(시니 노래)", "(시니 웃음)", "(시니 버럭)", "(시니 멋짐)", "(시니 씨익)",
                        "(시니 피곤)", "(시니 제발)", "(시니 궁금)", "(시니 풉)", "(시니 짱)",
                        "(시니 으악)", "(시니 슬픔)", "(시니 잠)", "(시니 당근)", "(시니 신남)", "(시니 숨음)",

                        "(저왔어요)", "(어서옵쇼)", "(아주 환영해)", "(잘부탁해)", "(그래)", "(지금 나 진지해)",
                        "(아 진짜?)", "(지금 장난해)", "(좋냥ㅋ)", "(나한테 왜그래)","(짠!)", "(응 아니야)",
                        "(나 안잔다)", "(응? 네?)", "(너의 마음을 루팡)", "(아이 신나)",

                        "(야근추가요)", "(퇴근뭔가요?)", "(월급)", "(빚이 많아)", "(잘가)", "(그러시던지요)", "(가고있습니다)",
                        "(난 틀렸어)", "(수고했어)", "(점심소고기)", "(후식엔 커피)", "(활기차게 입장)", "(전부 때려쳐)",
                        "(그대가 들어오죠)", "(아무것도 하기싫다)", "(홧팅)"
    };

        public static final String[] EMOTICON_TEXTS =
                {"(화남)","(충격)","(혼란)","(키키)","(엉엉)","(불끈)","(헤롱)","(러브)","(야호)","(부끄)","(오잉)","(캬악)",
                        "(흐흐)","(희죽)","(탈진)","(허걱)","(띠용)","(당황)","(갈증)","(거품)"};

        public static final String[] THUMB_EMOTICON_TEXTS =
                {
                        "(누리 노래)","(누리 웃음)","(누리 버럭)","(누리 멋짐)",
                        "(누리 씨익)", "(누리 피곤)","(누리 제발)","(누리 궁금)",
                        "(누리 풉)","(누리 짱)", "(누리 으악)","(누리 슬픔)",
                        "(누리 잠)","(누리 당근)","(누리 신남)", "(누리 숨음)",

                        "(바니 노래)", "(바니 웃음)", "(바니 버럭)", "(바니 멋짐)",
                        "(바니 씨익)", "(바니 피곤)","(바니 제발)","(바니 궁금)",
                        "(바니 풉)", "(바니 짱)", "(바니 으악)", "(바니 슬픔)",
                        "(바니 잠)", "(바니 당근)", "(바니 신남)", "(바니 숨음)",

                        "(시니 노래)", "(시니 웃음)", "(시니 버럭)", "(시니 멋짐)",
                        "(시니 씨익)", "(시니 피곤)", "(시니 제발)", "(시니 궁금)",
                        "(시니 풉)", "(시니 짱)", "(시니 으악)", "(시니 슬픔)",
                        "(시니 잠)", "(시니 당근)", "(시니 신남)", "(시니 숨음)",

                        "(저왔어요)", "(어서옵쇼)", "(아주 환영해)", "(잘부탁해)",
                        "(그래)", "(지금 나 진지해)","(아 진짜?)", "(지금 장난해)",
                        "(좋냥ㅋ)", "(나한테 왜그래)","(짠!)", "(응 아니야)",
                        "(나 안잔다)", "(응? 네?)", "(너의 마음을 루팡)", "(아이 신나)",

                        "(야근추가요)", "(퇴근뭔가요?)", "(월급)", "(빚이 많아)",
                        "(잘가)", "(그러시던지요)", "(가고있습니다)", "(난 틀렸어)",
                        "(수고했어)", "(점심소고기)", "(후식엔 커피)", "(활기차게 입장)",
                        "(전부 때려쳐)", "(그대가 들어오죠)", "(아무것도 하기싫다)", "(홧팅)"
                };

        public static final String[] THUMB_EMOTICON_NURI =
                {"(누리 노래)","(누리 웃음)","(누리 버럭)","(누리 멋짐)","(누리 씨익)",
                        "(누리 피곤)","(누리 제발)","(누리 궁금)","(누리 풉)","(누리 짱)",
                        "(누리 으악)","(누리 슬픔)","(누리 잠)","(누리 당근)","(누리 신남)",
                        "(누리 숨음)"
                };

        public static final String[] THUMB_EMOTICON_BANI = {
                "(바니 노래)", "(바니 웃음)", "(바니 버럭)", "(바니 멋짐)", "(바니 씨익)",
                "(바니 피곤)","(바니 제발)","(바니 궁금)", "(바니 풉)", "(바니 짱)",
                "(바니 으악)", "(바니 슬픔)", "(바니 잠)", "(바니 당근)", "(바니 신남)",
                "(바니 숨음)"
        };

        public static final String[] THUMB_EMOTICON_SINI = {
                "(시니 노래)", "(시니 웃음)", "(시니 버럭)", "(시니 멋짐)", "(시니 씨익)",
                "(시니 피곤)", "(시니 제발)", "(시니 궁금)", "(시니 풉)", "(시니 짱)",
                "(시니 으악)", "(시니 슬픔)", "(시니 잠)", "(시니 당근)", "(시니 신남)", "(시니 숨음)"
        };

        public static final String[] THUMB_EMOTICON_TEXT_ONE = {
                "(저왔어요)", "(어서옵쇼)", "(아주 환영해)", "(잘부탁해)", "(그래)", "(지금 나 진지해)",
                "(아 진짜?)", "(지금 장난해)", "(좋냥ㅋ)", "(나한테 왜그래)","(짠!)", "(응 아니야)",
                "(나 안잔다)", "(응? 네?)", "(너의 마음을 루팡)", "(아이 신나)"
        };

        public static final String[] THUMB_EMOTICON_TEXT_TWO = {
                "(야근추가요)", "(퇴근뭔가요?)", "(월급)", "(빚이 많아)", "(잘가)", "(그러시던지요)", "(가고있습니다)",
                "(난 틀렸어)", "(수고했어)", "(점심소고기)", "(후식엔 커피)", "(활기차게 입장)", "(전부 때려쳐)",
                "(그대가 들어오죠)", "(아무것도 하기싫다)", "(홧팅)"
        };


//    	public static final String[] ALL_TEXTS =
//		{"/화남/","/충격/","/혼란/","/키키/","/엉엉/","/불끈/","/헤롱/","/러브/","/야호/","/부끄/","/오잉/","/캬악/",
//		"/흐흐/","/희죽/","/탈진/","/허걱/","/띠용/","/당황/","/갈증/","/거품/",
//
//		"/신이 야호/","/신이 좋아/","/신이 축하/","/신이 최고/","/신이 연주/","/한이 연주/"
//		};
//
//    	public static final String[] EMOTICON_TEXTS =
//    		{"/화남/","/충격/","/혼란/","/키키/","/엉엉/","/불끈/","/헤롱/","/러브/","/야호/","/부끄/","/오잉/","/캬악/",
//    		"/흐흐/","/희죽/","/탈진/","/허걱/","/띠용/","/당황/","/갈증/","/거품/"};
//
//    	public static final String[] THUMB_EMOTICON_TEXTS = {"/신이 야호/","/신이 좋아/","/신이 축하/","/신이 최고/","/신이 연주/","/한이 연주/"};

    }
}
