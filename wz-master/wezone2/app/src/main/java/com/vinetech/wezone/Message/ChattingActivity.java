package com.vinetech.wezone.Message;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.ui.CustomPagerView;
import com.vinetech.ui.PageControl;
import com.vinetech.util.CustomList.PullToRefreshBase;
import com.vinetech.util.CustomList.PullToRefreshListView;
import com.vinetech.util.ImageGetter;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.UIControl;
import com.vinetech.util.UIViewAnimation;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.EmoticonItem;
import com.vinetech.wezone.Common.Fragment_Emoticon;
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_Message;
import com.vinetech.wezone.Data.Data_MsgKey;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_MessageList;
import com.vinetech.wezone.SendPacket.Send_PutPushFlag;
import com.vinetech.wezone.SendPacket.Send_PutRead;
import com.vinetech.xmpp.LibXmppManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 채팅 화면
 *
 *
 * 메세지 송수신 절차
 *
 * A유저 메세지 발송(xmpp) -> B 유저
 *
 * 1. B 점속 중  onXmppReceiveMessage 에서 메세지 수신 -> putRead 메소드를 통해 읽음 여부 서버 발송 -> A 유저 onXmppReceiveCommand 에서 읽음 메세지 키 수신 -> 읽음 처리
 * 2. B 접속 중이 아닐때, 접속과 동시에 http를 통해 메세지 리스트 수신 -> A 유저 onXmppReceiveCommand 에서 읽음 메세지 키 수신 -> 읽음 처리
 *
 * ** 읽음 처리 관련 Command 메세지는 putRead 메소드 호출 시 서버에서 발송함.
 *
 */

public class ChattingActivity extends BaseActivity {
    public static String CHAT_TYPE = "chat_type";
    public static int CHAT_TYPE_NORMAL = 0;
    public static int CHAT_TYPE_GROUP = 1;

    public static String OTHER_DATA = "other";

    public static String LAST_CHAT_ITEM = "last_chat_item";
    public static String PUSH_FLAG = "push_flag";


    public static void startActivity(BaseActivity activity, Data_OtherUser other) {
        Intent intent = new Intent(activity, ChattingActivity.class);
        intent.putExtra(CHAT_TYPE,CHAT_TYPE_NORMAL);
        intent.putExtra(OTHER_DATA,other);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_CHATTING);
    }

    public static void startActivityWithGroup(BaseActivity activity, Data_OtherUser other){
        Intent intent = new Intent(activity, ChattingActivity.class);
        intent.putExtra(CHAT_TYPE,CHAT_TYPE_GROUP);
        intent.putExtra(OTHER_DATA,other);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_CHATTING);
    }

    public Data_OtherUser mOtherData;

    private PullToRefreshListView chatting_list;


    private ChattingListAdapter mChattingListAdapter;

//    private LinearLayout linearlayout_edit_area;

    private LinearLayout linearlayout_btn_push;

    private EditText edittext_message;
    private LinearLayout linearlayout_btn_send;

    private ArrayList<Data_Message> mMessageList;


    private PullToRefreshBase.Mode mMode;
    private PullToRefreshBase.State mState;

    int mOffset = 0;
    int mLimit = 30;

    public int mTotalCount = 0;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private LinearLayout linearlayout_new_msg_area;
    private TextView textview_toast_msg;
    private LinearLayout linearlayout_btn_down;

    private int mChatType;

    private LinearLayout mEmoticonToggleBtnLayout;

    private View mWindowView, mTouchSkipView;
    private RelativeLayout mThumbEmoticonLayout;
    private ImageView mEmoticonsThumbImgView;
    private LinearLayout linearlayout_bottom_area;
    private LinearLayout mEmoticonsPageLayout;

    private CustomPagerView mEmoticonsPager;
    private Emoticon_PagerAdapter mEmoticonsPagerAdapter;
    private boolean isShowingEmoticonView;
    private int mEmoticonThumbResId;
    private ImageGetter mEmoticonImageGetter;

    private Rect mEmoticonLayoutCheckRect;
    private int mKeyboardHeight, mStatusBarHeight;
    private int mScreenWidth, mScreenHeight;

    private LinearLayout linearlayout_btn_basic;
    private LinearLayout linearlayout_btn_bani;
    private LinearLayout linearlayout_btn_nuri;
    private LinearLayout linearlayout_btn_sini;
    private LinearLayout linearlayout_btn_txt_one;
    private LinearLayout linearlayout_btn_txt_two;


    private class Emoticon_PagerAdapter extends FragmentStatePagerAdapter
    {

        private List<Fragment_Emoticon> fragments;
        public Emoticon_PagerAdapter(FragmentManager fm, List<Fragment_Emoticon> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) { return this.fragments.get(position); }
        @Override
        public int getCount() { return this.fragments.size();}
    }


    boolean isScreenOn = true;
    ScreenOnReceiver mScreenOnReceiver;


    ArrayList<Send_PutRead> mPutReadList;
    ClipData myClip = null;
    ClipboardManager clipboard = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        mOtherData = (Data_OtherUser) getIntent().getSerializableExtra(OTHER_DATA);
        mChatType = getIntent().getIntExtra(CHAT_TYPE, CHAT_TYPE_NORMAL);

        String title = WezoneUtil.isEmptyStr(mOtherData.user_name) ? "채팅" : mOtherData.user_name;
        setHeaderView(R.drawable.btn_back_white, title, 0);

        mEmoticonLayoutCheckRect = new Rect();

        String user_uuid = mOtherData.user_uuid;
        if(mChatType == CHAT_TYPE_GROUP){
            if(getShare().getServerInfo().groupchat_hostname != null) {
                user_uuid = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
            }
        }

        Data_Chat_UserList chatItem = getShare().getChatUserItem(user_uuid);

        linearlayout_btn_push = (LinearLayout) findViewById(R.id.linearlayout_btn_push);
        linearlayout_btn_push.setOnClickListener(mOnClickListener);

        if(chatItem != null){
            if("F".equals(chatItem.push_flag)){
                linearlayout_btn_push.setSelected(false);
            }else{
                linearlayout_btn_push.setSelected(true);
            }
        }else{
            linearlayout_btn_push.setSelected(true);
        }

        chatting_list = (PullToRefreshListView) findViewById(R.id.chatting_list);

        clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        chatting_list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String text = mChattingListAdapter.getText(pos);
                myClip = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(myClip);

                Toast toast = Toast.makeText(ChattingActivity.this, "복사하였습니다.", Toast.LENGTH_SHORT);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = Math.round((float)(displayMetrics.heightPixels * 0.1));
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, height);
                toast.show();

                return true;
            }

        });

//        linearlayout_edit_area = (LinearLayout) findViewById(R.id.linearlayout_edit_area);

        edittext_message = (EditText) findViewById(R.id.edittext_message);
        edittext_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmoticonView(false);
            }
        });

        linearlayout_btn_send = (LinearLayout) findViewById(R.id.linearlayout_btn_send);
        linearlayout_btn_send.setOnClickListener(mOnClickListener);

        mMessageList = new ArrayList<Data_Message>();

        mChattingListAdapter = new ChattingListAdapter(ChattingActivity.this, mMessageList);

        chatting_list.setAdapter(mChattingListAdapter);

        chatting_list.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                mMode = direction;
                mState = state;
            }
        });

        chatting_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if(mMode == PullToRefreshBase.Mode.PULL_FROM_START)
                {
                    chatting_list.setStackFromBottom(false);
                    chatting_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);

                    // 방개설하고 채팅 리스트 없는 경우 당겨서 Refresh한 경우 예외처리
                    if(mMessageList.isEmpty() || mTotalCount <= mMessageList.size())
                    {
                        (new Timer()).schedule(new TimerTask() {
                            @Override
                            public void run()
                            {
                                runOnUiThread(new Runnable() { @Override
                                public void run() { chatting_list.onRefreshComplete(); } });
                            }
                        }, 200);
                    }
                    else
                    {

                        if(mTotalCount > mMessageList.size()){
                            (new Timer()).schedule(new TimerTask() {
                                @Override
                                public void run()
                                {
                                    runOnUiThread(new Runnable() { @Override
                                    public void run() {

                                        mOffset = mMessageList.size();
                                        getChattingList(false);
                                    } });
                                }
                            }, 0);
                        }

                    }
                }
            }
        });

        chatting_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if(lastitemVisibleFlag){
                    if(linearlayout_new_msg_area.getVisibility() == View.VISIBLE) {
                        linearlayout_new_msg_area.setVisibility(View.GONE);
                    }
                }
            }

        });


        linearlayout_new_msg_area = (LinearLayout) findViewById(R.id.linearlayout_new_msg_area);
        textview_toast_msg = (TextView) findViewById(R.id.textview_toast_msg);
        linearlayout_btn_down = (LinearLayout) findViewById(R.id.linearlayout_btn_down);
        linearlayout_btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveScrollToBottomWithAnimation(true);

                if(linearlayout_new_msg_area.getVisibility() == View.VISIBLE) {
                    linearlayout_new_msg_area.setVisibility(View.GONE);
                }
            }
        });

        getChattingList(true);

        //안읽음 메세지 초기화

        if(mChatType == CHAT_TYPE_GROUP){
            String roomId = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
            getShare().removeUnReadWithUUID(roomId);
        }else{
            getShare().removeUnReadWithUUID(mOtherData.user_uuid);
        }



        initEmoticonViews();


        mScreenOnReceiver = new ScreenOnReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenOnReceiver, filter);

        mPutReadList = new ArrayList<>();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mOtherData = (Data_OtherUser) getIntent().getSerializableExtra(OTHER_DATA);
        mChatType = getIntent().getIntExtra(CHAT_TYPE, CHAT_TYPE_NORMAL);

        getChattingList(true);

        //안읽음 메세지 초기화
        if(mChatType == CHAT_TYPE_GROUP){
            String roomId = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
            getShare().removeUnReadWithUUID(roomId);
        }else{
            getShare().removeUnReadWithUUID(mOtherData.user_uuid);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO)
        {
            chatting_list.setSelection(mChattingListAdapter.getCount());
        }
        else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES)
        {
            showEmoticonView(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(mMessageList != null && mMessageList.size() > 0){

            String chatType = "chat";
            if(mChatType == CHAT_TYPE_GROUP){
                chatType = "groupchat";
            }

            Call<Rev_MessageList> revMessageListCall = wezoneRestful.getMessageList(mOtherData.user_uuid,chatType,"0", "1000",getLastMsgKey());
            revMessageListCall.enqueue(new Callback<Rev_MessageList>() {
                @Override
                public void onResponse(Call<Rev_MessageList> call, Response<Rev_MessageList> response) {
                    Rev_MessageList revMessageList = response.body();
                    if(isNetSuccess(revMessageList)){
                        if(revMessageList.message != null && revMessageList.message.size() > 0){
                            ArrayList<Data_Message> tempMessageList = new ArrayList<>();
                            tempMessageList.addAll(revMessageList.message);
                            Collections.reverse(tempMessageList);

                            mMessageList.addAll(tempMessageList);
                            moveScrollToBottomWithAnimation(false);

                            mChattingListAdapter.setOtherUser(mOtherData);
                            mChattingListAdapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Rev_MessageList> call, Throwable t) {

                }
            });
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mScreenOnReceiver);

    }


    @Override
    public void onClickLeftBtn(View v) {
        Intent i = getLastChatItem();
        if(i == null){
            super.onClickLeftBtn(v);
        }else{
            setResult(RESULT_OK,i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        if (View.VISIBLE == mEmoticonsPageLayout.getVisibility()) {
            isShowingEmoticonView = false;
            showEmoticonView(false, false);
        }else{
            Intent i = getLastChatItem();
            if(i == null){
                super.onBackPressed();
            }else{
                setResult(RESULT_OK,i);
                finish();
            }
        }
    }

    public Intent getLastChatItem(){

        if(mMessageList == null || mMessageList.size() == 0)
            return null;

        Data_Message message = mMessageList.get(mMessageList.size() - 1);

        Intent i = new Intent();
        i.putExtra(OTHER_DATA,mOtherData);
        i.putExtra(LAST_CHAT_ITEM, message);

        if(linearlayout_btn_push.isSelected()){
            i.putExtra(PUSH_FLAG,"T");
        }else{
            i.putExtra(PUSH_FLAG,"F");
        }

        return i;
    }


    public void putPushFlag(String value){

        Send_PutPushFlag data = new Send_PutPushFlag();
        data.push_flag = value;
        data.other_uuids = new ArrayList<>();

        String user_uuid = mOtherData.user_uuid;
        if(mChatType == CHAT_TYPE_GROUP){
            user_uuid = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
        }

        HashMap<String,String> other_uuids = new HashMap<>();
        other_uuids.put("other_uuid",user_uuid);
        data.other_uuids.add(other_uuids);

        Call<Rev_Base> revBaseCall =  wezoneRestful.putUserList(data);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });

    }

    public void getChattingList(boolean isProgress) {

        String chatType = "chat";
        if(mChatType == CHAT_TYPE_GROUP){
            chatType = "groupchat";
        }

        if(isProgress){
            showProgressPopup();
        }
        Call<Rev_MessageList> revMessageListCall = wezoneRestful.getMessageList(mOtherData.user_uuid,chatType,String.valueOf(mOffset), String.valueOf(mLimit));
        revMessageListCall.enqueue(new Callback<Rev_MessageList>() {
            @Override
            public void onResponse(Call<Rev_MessageList> call, Response<Rev_MessageList> response) {
                Rev_MessageList revMessageList = response.body();
                if(isNetSuccess(revMessageList)){

                    mTotalCount = Integer.valueOf(revMessageList.total_count);

                    if("F".equals(revMessageList.push_flag)){
                        linearlayout_btn_push.setSelected(false);
                    }else{
                        linearlayout_btn_push.setSelected(true);
                    }

                    ArrayList<Data_Message> tempMessageList = new ArrayList<>();
                    tempMessageList.addAll(revMessageList.message);
                    Collections.reverse(tempMessageList);

                    if(mMode == PullToRefreshBase.Mode.PULL_FROM_START){
                        chatting_list.onRefreshComplete();

                        mMessageList.addAll(0,tempMessageList);
                    }else{
                        mMessageList.addAll(tempMessageList);
                        moveScrollToBottomWithAnimation(false);
                    }
                    mChattingListAdapter.setOtherUser(mOtherData);
                    mChattingListAdapter.notifyDataSetChanged();
                }

                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_MessageList> call, Throwable t) {
                Toast.makeText(ChattingActivity.this,"네트워크 상태가 좋지 않습니다.",Toast.LENGTH_SHORT).show();
                hidePorgressPopup();
                finish();
            }
        });
    }

    public void sendMasseage(String str) {

        Data_Message myMessage = new Data_Message();
        myMessage.sfrom = getUuid();

        myMessage.txt = str;
        myMessage.created_at = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
        myMessage.img_url = getImageUrl();
        myMessage.user_name = getName();
        myMessage.msgkey = UUID.randomUUID().toString();

        boolean isSuccess = false;

        if(mChatType == CHAT_TYPE_NORMAL){
            String to = mOtherData.user_uuid + "@" + LibXmppManager.getInstance().getServiceName();
            isSuccess = LibXmppManager.getInstance().sendMessage(to, Define.MESSAGE_TYPE_CHAT,myMessage, getUuid(), getName(), getImageUrl(),myMessage.msgkey);
        }else{
            String to = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
            isSuccess = LibXmppManager.getInstance().sendMessageToRoom(to, Define.MESSAGE_TYPE_CHAT,myMessage, getUuid(), getName(), getImageUrl(),myMessage.msgkey);
        }

//        boolean isSuccess = LibXmppManager.getInstance().sendMessage(to, myMessage);
        if (isSuccess) {

            edittext_message.setText("");

            //인코딩 한번.
//            myMessage.content = UIControl.encodeBase64WithString(myMessage.content);
            mMessageList.add(mMessageList.size(), myMessage);

            mChattingListAdapter.notifyDataSetChanged();

//            showEmoticonView(false,false);
            moveScrollToBottomWithAnimation(true);
        } else {
//            Toast.makeText(ChattingActivity.this, "발송 실패", Toast.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            switch (viewId){
                case R.id.linearlayout_btn_push: {

                    String user_uuid = mOtherData.user_uuid;
                    if(mChatType == CHAT_TYPE_GROUP){
                        user_uuid = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
                    }

                    int idx = getUserDataIndex(user_uuid);
                    if(idx != -1){
                        if (linearlayout_btn_push.isSelected()) {
                            linearlayout_btn_push.setSelected(false);
                            putPushFlag("F");
                            getShare().getUnReadList().get(idx).push_flag = "F";
                        } else {
                            linearlayout_btn_push.setSelected(true);
                            putPushFlag("T");
                            getShare().getUnReadList().get(idx).push_flag = "T";
                        }
                    }
                }
                    break;

                case R.id.linearlayout_btn_send:{
//                    String strTemp = edittext_message.getText().toString().trim();
                    String strTemp = WezoneUtil.toRichContentText(edittext_message);
                    boolean isEmptyContent = WezoneUtil.isEmptyStr(strTemp);

                    String emoticonContents = null;
                    if( mEmoticonThumbResId == 0 && isEmptyContent == true )
                    {
                        Toast.makeText(ChattingActivity.this, "대화를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }


                    if( mEmoticonThumbResId > 0 )
                    {
                        emoticonContents = Define.Emoticon.THUMB_EMOTICON_TEXTS[mEmoticonThumbResId - R.drawable.sticker_01];
                        //contents = "/스"+(mEmoticonThumbResId-R.drawable.sticker01+1)+"/";

//				addChatList(contents);
                        dismissThumbEmoticon();

                    }

                    if(emoticonContents != null) {
                        sendMasseage(emoticonContents);
                    }

                    if(!isEmptyContent){
                        sendMasseage(strTemp);
                    }
                }
                    break;

            }
        }
    };

    @Override
    public void onXmppReceiveCommand(MassegeType type, String fromID, ArrayList<Data_MsgKey> msgkeys) {
        super.onXmppReceiveCommand(type, fromID, msgkeys);

        for(Data_MsgKey msgkey : msgkeys){
            for(int i=0; i<mMessageList.size(); i++){
               if(msgkey.msgkey.equals(mMessageList.get(i).msgkey)){
                   int cnt = WezoneUtil.isEmptyStr(mMessageList.get(i).is_read) ? 0 : Integer.valueOf(mMessageList.get(i).is_read);
                   cnt++;
                   mMessageList.get(i).is_read = String.valueOf(cnt);
               }
            }
        }
        mChattingListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onXmppReceiveMessage(MassegeType type, String fromID, String sfrom ,String other_user_uuid, String other_user_name, String other_user_img_url, String message, String msgkey) {

        if(type == MassegeType.groupchat){
            String roomId = mOtherData.user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
            if(roomId.equals(fromID)){
                if(!getUuid().equals(sfrom)){
                    Data_Message myMessage = new Data_Message();
                    myMessage.message_type = Define.MESSAGE_TYPE_CHAT;
                    myMessage.txt = message;
                    myMessage.created_at = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
                    myMessage.sfrom = sfrom;
                    myMessage.img_url = other_user_img_url;
                    myMessage.user_name = other_user_name;
                    myMessage.is_read = "0";
                    myMessage.msgkey = msgkey;
                    mMessageList.add(mMessageList.size(), myMessage);

                    mChattingListAdapter.notifyDataSetChanged();

                    putRead(roomId,"groupchat",msgkey, LibXmppManager.getInstance().getResource(),null);

                    if(lastitemVisibleFlag){
                        moveScrollToBottomWithAnimation(false);
                    }else{
                        if(linearlayout_new_msg_area.getVisibility() == View.GONE){
                            UIViewAnimation.VisibleViewWithAnimation(linearlayout_new_msg_area);
                        }

                        textview_toast_msg.setText(myMessage.txt);
                    }
                }
            }
        }else{
            if(mOtherData.user_uuid.equals(sfrom)){
                if(!getUuid().equals(sfrom)){
                    Data_Message myMessage = new Data_Message();
                    myMessage.message_type = Define.MESSAGE_TYPE_CHAT;
                    myMessage.txt = message;
                    myMessage.created_at = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
                    myMessage.sfrom = sfrom;
                    myMessage.img_url = other_user_img_url;
                    myMessage.user_name = other_user_name;
                    myMessage.is_read = "1";
                    myMessage.msgkey = msgkey;
                    mMessageList.add(mMessageList.size(), myMessage);

                    mChattingListAdapter.notifyDataSetChanged();

                    putRead(mOtherData.user_uuid, "chat", msgkey, LibXmppManager.getInstance().getResource(),null);

                    if(lastitemVisibleFlag){
                        moveScrollToBottomWithAnimation(false);
                    }else{
                        if(linearlayout_new_msg_area.getVisibility() == View.GONE){
                            UIViewAnimation.VisibleViewWithAnimation(linearlayout_new_msg_area);
                        }

                        textview_toast_msg.setText(myMessage.txt);
                    }
                }
            }
        }
    }

    private void moveScrollToBottomWithAnimation(final boolean isAni) {
        chatting_list.post(new Runnable() {
            @Override
            public void run() {

                if (isAni) {
                    chatting_list.smoothScrollToPosition(mChattingListAdapter.getCount());
                } else {
                    chatting_list.setSelection(mChattingListAdapter.getCount() - 1);
                }
            }
        });
    }

    private void putRead(String other_uuid, String kind, String msgkey, String device_id, ArrayList<HashMap<String,String>> msgkeys){

        Send_PutRead sendPutRead = new Send_PutRead();
        sendPutRead.other_uuid = other_uuid;
        sendPutRead.kind = kind;
        sendPutRead.msgkey = msgkey;
        sendPutRead.device_id = device_id;
        sendPutRead.msgkeys = msgkeys;
        if(isScreenOn){
            Call<Rev_Base> revBaseCall = wezoneRestful.putRead(sendPutRead);
            revBaseCall.enqueue(new Callback<Rev_Base>() {
                @Override
                public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

                }

                @Override
                public void onFailure(Call<Rev_Base> call, Throwable t) {

                }
            });
        }else{
            mPutReadList.add(sendPutRead);
        }
    }

    private void initEmoticonViews()
    {
        mScreenHeight = CryptPreferences.getInt(this, Define.SHARE_KEY_SCREEN_HEIGHT, 0);
        mScreenWidth = CryptPreferences.getInt(this, Define.SHARE_KEY_SCREEN_WIDTH, 0);

        mStatusBarHeight = getResources().getDimensionPixelSize(R.dimen.statusbar_height);
        mKeyboardHeight = CryptPreferences.getInt(this, Define.SHARE_KEY_KEYBOARD_HEIGHT, 0);


        mEmoticonImageGetter = new ImageGetter(this);

        mThumbEmoticonLayout = (RelativeLayout)findViewById(R.id.emoticons_thumb_layout);
        mEmoticonsThumbImgView = (ImageView)findViewById(R.id.emoticons_thumb_imgview);
        mTouchSkipView = findViewById(R.id.touch_skip_view);

//		mInputLayout = (LinearLayout)findViewById(R.id.input_layout);
        linearlayout_bottom_area = (LinearLayout)findViewById(R.id.linearlayout_bottom_area);
//		mInputDividerView = findViewById(R.id.input_divider_layout);

        mEmoticonsPageLayout = (LinearLayout)findViewById(R.id.emoticons_page_layout);
        mEmoticonsPager = (CustomPagerView) findViewById(R.id.ViewPager_emoticon);

        PageControl emoticonsPageControl = (PageControl)findViewById(R.id.PageControl_emoticon);
        mEmoticonsPager.setPageControl(emoticonsPageControl);

        mEmoticonToggleBtnLayout = (LinearLayout)findViewById(R.id.linearlayout_btn_emoticon);
        mEmoticonToggleBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.GONE == mEmoticonsPageLayout.getVisibility()) {
                    isShowingEmoticonView = true;
                    showEmoticonView(true,true);
                } else {
                    isShowingEmoticonView = false;
                    showEmoticonView(false,false);
                }
            }
        });

        linearlayout_btn_basic = (LinearLayout) findViewById(R.id.linearlayout_btn_basic);
        linearlayout_btn_basic.setOnClickListener(mEmoticonClickListener);
        linearlayout_btn_bani = (LinearLayout) findViewById(R.id.linearlayout_btn_bani);
        linearlayout_btn_bani.setOnClickListener(mEmoticonClickListener);
        linearlayout_btn_nuri = (LinearLayout) findViewById(R.id.linearlayout_btn_nuri);
        linearlayout_btn_nuri.setOnClickListener(mEmoticonClickListener);
        linearlayout_btn_sini = (LinearLayout) findViewById(R.id.linearlayout_btn_sini);
        linearlayout_btn_sini.setOnClickListener(mEmoticonClickListener);
        linearlayout_btn_txt_one = (LinearLayout) findViewById(R.id.linearlayout_btn_txt_one);
        linearlayout_btn_txt_one.setOnClickListener(mEmoticonClickListener);
        linearlayout_btn_txt_two = (LinearLayout) findViewById(R.id.linearlayout_btn_txt_two);
        linearlayout_btn_txt_two.setOnClickListener(mEmoticonClickListener);


        reloadStickerFragment(0);

        mEmoticonsPageLayout.setVisibility(View.GONE);

        mWindowView = getWindow().getDecorView();

        mWindowView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout() {

                Rect visibleRect = new Rect();
                mWindowView.getWindowVisibleDisplayFrame(visibleRect);

                if( mScreenHeight == 0 )
                {
                    final View rootView = mWindowView.getRootView();
                    mScreenWidth = rootView.getWidth();
                    mScreenHeight = rootView.getHeight();

                    CryptPreferences.putInt(ChattingActivity.this, Define.SHARE_KEY_SCREEN_WIDTH, mScreenWidth,false);
                    CryptPreferences.putInt(ChattingActivity.this, Define.SHARE_KEY_SCREEN_HEIGHT, mScreenHeight,true);
                }

                int heightDifference = mScreenHeight - (visibleRect.bottom - visibleRect.top);
                if( heightDifference > mStatusBarHeight )
                {
                    if( mKeyboardHeight != heightDifference )
                    {
                        mKeyboardHeight = heightDifference;
                        CryptPreferences.putInt(ChattingActivity.this, Define.SHARE_KEY_KEYBOARD_HEIGHT, mKeyboardHeight);
                    }

                    if( getEmoticonHeaderLayoutBottom() == mScreenHeight-mKeyboardHeight+mStatusBarHeight )
                    {
                        if ( mMessageList.size() == chatting_list.getLastVisiblePosition()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    chatting_list.setSelection(mMessageList.size());
                                }
                            }, 300);
                        }
                    }
                    if( isShowingEmoticonView == false && mEmoticonsPageLayout.getVisibility() == View.VISIBLE )
                        showEmoticonView(false, false);
                }
                else
                {

                    if( mEmoticonsPageLayout.getVisibility() == View.GONE )
                    {
                        chatting_list.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
                        chatting_list.requestLayout();
                    }
                }
            }
        });
    }

    private void reloadStickerFragment(int idx){

        List<Fragment_Emoticon> fragmentEmoticon = new ArrayList<Fragment_Emoticon>();

        // 0 ~ 5 이모티콘
        switch(idx){
            case 0:{
                ArrayList<EmoticonItem> itemList = new ArrayList<EmoticonItem>();
                for(int i=1; i< 21; i++){
                    EmoticonItem item = new EmoticonItem(WezoneUtil.getEmoticonImageId(i), WezoneUtil.getEmoticonName(i));
                    itemList.add(item);
                }

                EmoticonItem item = new EmoticonItem(R.drawable.btn_del_emoticon, "삭제");
                itemList.add(item);

                fragmentEmoticon.add(new Fragment_Emoticon(itemList, ChattingActivity.this, new Fragment_Emoticon.OnTouchDownListener()
                {
                    @Override
                    public void onEmoticonTouchDown(int position)
                    //public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                        final int DELETE_POSITION = 20;

                        Editable editable = edittext_message.getEditableText();

                        if( position != DELETE_POSITION )
                        {
                            mEmoticonImageGetter.setIconSize(ImageGetter.EMOTICON_SIZE_SMALL);
                            Spanned htmlSpanned = Html.fromHtml(WezoneUtil.getHTMLImgTag(String.valueOf(position+1)),mEmoticonImageGetter,null);

                            position = Selection.getSelectionStart(editable);
                            editable.insert((position < 0) ? 0 : position, htmlSpanned);

                            edittext_message.requestFocus();
                        }
                        else
                        {
                            WezoneUtil.deleteLastCharacter(edittext_message, editable);
                        }
                    }
                }));
            }
                break;

            case 1:
            {
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_01, R.drawable.sticker_01, 8);
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_09, R.drawable.sticker_09, 8);
            }
                break;

            case 2:{
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_17, R.drawable.sticker_17, 8);
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_25, R.drawable.sticker_25, 8);
            }
                break;

            case 3:{
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_33, R.drawable.sticker_33, 8);
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_41, R.drawable.sticker_41, 8);
            }
                break;

            case 4:
            {
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_49, R.drawable.sticker_49, 8);
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_58, R.drawable.sticker_58, 8);
            }
                break;

            case 5:
            {
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_66, R.drawable.sticker_66, 8);
                addStickerFragment(fragmentEmoticon, R.drawable.sticker_74, R.drawable.sticker_74, 8);
            }
            break;

        }

        mEmoticonsPagerAdapter = new Emoticon_PagerAdapter(getSupportFragmentManager(), fragmentEmoticon);
        mEmoticonsPager.setAdapter(mEmoticonsPagerAdapter);
    }

    private void addStickerFragment(List<Fragment_Emoticon> fragmentEmoticon, final int stickerThumbResId, final int stickerResId, int count)
    {
        ArrayList<EmoticonItem> itemList = new ArrayList<EmoticonItem>();
        for(int i=0; i< count; i++)
        {
            itemList.add(new EmoticonItem(stickerThumbResId+i, WezoneUtil.getEmoticonName(21+i)));
        }
        fragmentEmoticon.add(new Fragment_Emoticon(itemList, this, new Fragment_Emoticon.OnTouchDownListener()
        {
            @Override
            public void onEmoticonTouchDown(int position)
            //public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if( mThumbEmoticonLayout.getVisibility() == View.GONE )
                {
                    mThumbEmoticonLayout.setVisibility(View.VISIBLE);
                    mTouchSkipView.setVisibility(View.VISIBLE);

                    if( mThumbEmoticonLayout.getLayoutParams().height < 0 )
                    {
                        int thumbEmoticonPrviewHeight = getResources().getDimensionPixelSize(R.dimen.dimen_123dp);

                        mThumbEmoticonLayout.getLayoutParams().height = thumbEmoticonPrviewHeight;
                    }
                }

                mEmoticonThumbResId = stickerResId+position;
                mEmoticonsThumbImgView.setBackgroundResource(mEmoticonThumbResId);

                UIViewAnimation.VisibleViewWithAnimation(mEmoticonsThumbImgView, 1500);
            }
        }, R.layout.fragment_emoticon_thumb, R.layout.gridview_emoticon_thumb_item));
    }

    public void showEmoticonView(boolean isShow)
    {
        showEmoticonView(isShow, false);
    }
    public void showEmoticonView(boolean isShow, boolean isHideKeyboard)
    {

        int topPadding = getResources().getDimensionPixelSize(R.dimen.dimen_44dp);

        int calcListViewHeight = 0;

        if( isShow == true )
        {

            chatting_list.setSelection(mChattingListAdapter.getCount());

            UIControl.hideSoftKeyBoard(this, edittext_message);

            if( getEmoticonHeaderLayoutBottom() == mScreenHeight-mKeyboardHeight+mStatusBarHeight )
            {
                (new Timer()).schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showEmoticonView(true);
                            }
                        });
                    }
                },200);
                return;
            }
//            UIControl.setBackgroundDrawable(m_Context,mEmoticonToggleBtnLayout,R.drawable.selector_btn_keypad);

            //kwonkh5
            //키보드가 펼쳐질 때 Delay가 발생하여 키보드 높이를 구하지 못할 때 방어코드
            //Nexus5의 경우 기본 키보드 높이가 이모티콘 레이아웃보다 작아서 짤림 현상 발생
            int defaultKeyboardSize = ( mScreenHeight / 2 ) - mStatusBarHeight;

            if( mKeyboardHeight == 0 || mKeyboardHeight < defaultKeyboardSize ){
                mKeyboardHeight = defaultKeyboardSize;
            }

            mEmoticonsPageLayout.setVisibility(View.VISIBLE);
            mEmoticonsPageLayout.getLayoutParams().height = mKeyboardHeight-mStatusBarHeight;
//            isShowingEmoticonView = false;
        }
        else
        {
            mEmoticonsPageLayout.setVisibility(View.GONE);

            if( isHideKeyboard == true )
                UIControl.hideSoftKeyBoard(this, edittext_message);
            else
                UIControl.toggleSoftKeyBoard(this);

//            UIControl.setBackgroundDrawable(m_Context, mEmoticonToggleBtnLayout, R.drawable.selector_btn_emoticon);

            isShowingEmoticonView = false;
        }

        calcListViewHeight = mScreenHeight - (mKeyboardHeight + topPadding + linearlayout_bottom_area.getLayoutParams().height + mStatusBarHeight*2);
        chatting_list.getLayoutParams().height = calcListViewHeight;
    }

    private int getEmoticonHeaderLayoutBottom()
    {
        linearlayout_bottom_area.getWindowVisibleDisplayFrame(mEmoticonLayoutCheckRect);
        return mEmoticonLayoutCheckRect.bottom;
    }

    public View.OnClickListener mEmoticonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            linearlayout_btn_basic.setSelected(false);
            linearlayout_btn_bani.setSelected(false);
            linearlayout_btn_nuri.setSelected(false);
            linearlayout_btn_sini.setSelected(false);
            linearlayout_btn_txt_one.setSelected(false);
            linearlayout_btn_txt_two.setSelected(false);

            switch (viewId){
                case R.id.linearlayout_btn_basic: {
                    reloadStickerFragment(0);
                    linearlayout_btn_basic.setSelected(true);
                }
                    break;


                case R.id.linearlayout_btn_nuri:{
                    reloadStickerFragment(1);
                    linearlayout_btn_nuri.setSelected(true);
                }
                    break;

                case R.id.linearlayout_btn_bani: {
                    reloadStickerFragment(2);
                    linearlayout_btn_bani.setSelected(true);
                }
                    break;

                case R.id.linearlayout_btn_sini: {
                    reloadStickerFragment(3);
                    linearlayout_btn_sini.setSelected(true);
                }
                    break;

                case R.id.linearlayout_btn_txt_one: {
                    reloadStickerFragment(4);
                    linearlayout_btn_txt_one.setSelected(true);
                }
                    break;

                case R.id.linearlayout_btn_txt_two: {
                    reloadStickerFragment(5);
                    linearlayout_btn_txt_two.setSelected(true);
                }
                    break;
            }
        }
    };

    public void dismissKeyboardAndEmoticonLayout()
    {
        UIControl.hideSoftKeyBoard(this, edittext_message);

//        UIControl.setBackgroundDrawable(m_Context,mEmoticonToggleBtnLayout,R.drawable.selector_btn_emoticon);

        mEmoticonsPageLayout.setVisibility(View.GONE);
        chatting_list.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
    }

    private void dismissThumbEmoticon()
    {
        if( mThumbEmoticonLayout != null && mThumbEmoticonLayout.getVisibility() == View.VISIBLE )
        {
            mTouchSkipView.setVisibility(View.GONE);
            mThumbEmoticonLayout.setVisibility(View.GONE);
            mEmoticonsThumbImgView.setBackgroundColor(Color.TRANSPARENT);
            mEmoticonThumbResId = 0;
        }
    }

    public void onClickTouchSkip(View v) {}
    public void onClickThumbEmoticonClose(View v)
    {
        dismissThumbEmoticon();
    }

    public void onClickListCell(View v)
    {
        dismissKeyboardAndEmoticonLayout();
    }


    public void sendPutReadDatas(){
        if(mPutReadList != null){

            Send_PutRead sendData = new Send_PutRead();
            sendData.msgkeys = new ArrayList<>();

            if(mPutReadList.size() > 0){
                for(Send_PutRead data : mPutReadList){

                    sendData.other_uuid = data.other_uuid;
                    sendData.device_id = data.device_id;
                    sendData.kind = data.kind;
                    HashMap<String,String> tempHash = new HashMap<>();
                    tempHash.put("msgkey",data.msgkey);
                    sendData.msgkeys.add(tempHash);
                }

                putRead(sendData.other_uuid,sendData.kind,null,sendData.device_id,sendData.msgkeys);
                mPutReadList.clear();
            }
        }
    }

    class ScreenOnReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
                sendPutReadDatas();
            }
            else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
            }
        }
    }

    public String getLastMsgKey(){

        if(mMessageList == null)
            return null;

        return mMessageList.get(mMessageList.size()-1).msgkey;
    }

}
