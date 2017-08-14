package com.vinetech.xmpp;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Data.Data_Message;
import com.vinetech.wezone.Data.Data_MsgKeyList;
import com.vinetech.wezone.Data.Data_ServerInfo;
import com.vinetech.wezone.Define;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.parsing.UnparsablePacket;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author sumin
 *         <p/>
 *         xmpp ?��?��브리�? 추�?(?��?��)
 *         asmack-android-19-0.8.10.jar
 *         <p/>
 *         ?��?��브러�? ?��?��로드 ?��?��?��
 *         http://asmack.freakempire.de
 */

public class LibXmppManager {
    public enum StatusMode {
        chat,
        available,
        unavailable,
        away,
    }

    private final int MESSAGE_CHAT = 0;
    private final int MESSAGE_GROUPCHAT = 1;
    private final int MESSAGE_NORMAL = 2;
    private final int MESSAGE_INVITE = 3;
    private final int MESSAGE_ERROR = 99;

    private static LibXmppManager instance = null;

    private XMPPConnection connection;

    private ConnectionListener connectionListener;

    private onXmppChatListener xmppChatListener;

    private String host;
    private int port;
    private String serviceName;
    private String username;
    private String password;
    private String mucServiceName;
    private String resource;

    private String mConnectionId;

    private boolean isMessageFilterListener = false;
    private boolean mIsConnecting;

    private Map<String, Object> chatMap = Collections.synchronizedMap(new HashMap<String, Object>());
    private Map<String, String> nickNameMap = Collections.synchronizedMap(new HashMap<String, String>());

    private MultiUserChat mChatRoom;

    private PacketListener mPacketListener;

    private PacketListener mChatPacketListener;
    private PacketListener mGroupchatPacketListener;
    private PacketListener mNormalPacketListener;
    private PacketListener mErrorPacketListener;

    public static LibXmppManager getInstance() {
        return instance;
    }


    public static void initXmpp(Data_ServerInfo serverInfo, String id, String pw, String device_model) {
        closeXmpp();

        if( instance == null ){
            instance = new LibXmppManager();
        }

        instance.setServerInfo(serverInfo, id, pw, device_model);
        instance.connect();
    }

    public static void closeXmpp() {
        if (instance != null) instance.close();
    }

    public LibXmppManager() {

        //멀티 로그인시 아래와 같은 타입이 오면 필터링을 못함
        //Ejabberd 에서 발송하는 듯.

//
        ProviderManager pm = ProviderManager.getInstance();

        pm.addExtensionProvider("wezone", "wezone:m", new CustomExtensionProvider());

        pm.addExtensionProvider("message_type", "wezone", new MessageTypeExtensionProvider());
//        pm.addExtensionProvider("item_id", "wezone", new ItemIdExtensionProvider());
        pm.addExtensionProvider("from", "wezone", new FromExtensionProvider());
        pm.addExtensionProvider("msgkey", "wezone", new MsgKeyExtensionProvider());
//        pm.addExtensionProvider("type", "galuster", new TypeExtensionProvider());
//
//        pm.addExtensionProvider("archived", "urn:xmpp:mam:tmp", new ArchivedProvider());
//        pm.addExtensionProvider("stanza-id", "urn:xmpp:sid:0", new StanzaIdProvider());

        //오프라인 메세지
        pm.addExtensionProvider("delay", "urn:xmpp:delay", new DelayExtensionProvider());

//        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

    }

    private static void addSimplePacketExtension(final String name, final String namespace) {
        ProviderManager.getInstance().addExtensionProvider(name, namespace,
                new PacketExtensionProvider() {
                    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {

                        StreamHandlingPacket packet = new StreamHandlingPacket(name, namespace);
                        int attributeCount = parser.getAttributeCount();
                        for (int i = 0 ; i < attributeCount ; i++) {
                            packet.addAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
                        }
                        return packet;
                    }
                });
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getConnectionId() {
        return mConnectionId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getMucServiceName() {
        return mucServiceName;
    }

    public void setMucServiceName(String mucServiceName) {
        this.mucServiceName = mucServiceName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean isLogin() {
        return connection != null && connection.isAuthenticated();
    }

    public boolean isMessageFilterListener() {
        return isMessageFilterListener;
    }

    public onXmppChatListener getXmppChatListener() {
        return this.xmppChatListener;
    }

    public void setXmppChatListener(onXmppChatListener xmppChatListener) {
        this.xmppChatListener = xmppChatListener;
    }

    public void setServerInfo(Data_ServerInfo serverInfo, String id, String pw, String device_model) {
        if (serverInfo != null) {
            setHost(serverInfo.server_dns);
            setPort(Integer.parseInt(serverInfo.server_port));

            setUsername(id);
            setPassword(pw);
            setResource(device_model);
            setServiceName(serverInfo.xmpp_hostname);
//            setServiceName("wezone.com");
        }
    }

    public void setChatLinsteners() {
        if (isConnected()) {

            PacketFilter chat = new MessageTypeFilter(Message.Type.chat);
            mChatPacketListener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) {

                    android.os.Message msg = new android.os.Message();
                    msg.what = MESSAGE_CHAT;
                    msg.obj = packet;
                    notiHandler.sendMessage(msg);

                }
            };

            connection.removePacketListener(mChatPacketListener);
            connection.addPacketListener(mChatPacketListener, chat);
//            connection.setParsingExceptionCallback(callback);


            //그룹채팅
            PacketFilter groupchat = new MessageTypeFilter(Message.Type.groupchat);
            mGroupchatPacketListener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) {

                    android.os.Message msg = new android.os.Message();
                    msg.what = MESSAGE_GROUPCHAT;
                    msg.obj = packet;
                    notiHandler.sendMessage(msg);
                }

            };
            connection.removePacketListener(mGroupchatPacketListener);
            connection.addPacketListener(mGroupchatPacketListener, groupchat);

            //?��버에?�� ?��?��! (?���? 공�?)
            PacketFilter normal = new MessageTypeFilter(Message.Type.normal);
            mNormalPacketListener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) {

                    android.os.Message msg = new android.os.Message();
                    msg.what = MESSAGE_NORMAL;
                    msg.obj = packet;
                    notiHandler.sendMessage(msg);
                }

            };
            connection.removePacketListener(mNormalPacketListener);
            connection.addPacketListener(mNormalPacketListener, normal);

            //?��?��
            PacketFilter error = new MessageTypeFilter(Message.Type.error);
            mErrorPacketListener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) {

                    android.os.Message msg = new android.os.Message();
                    msg.what = MESSAGE_ERROR;
                    msg.obj = packet;
                    notiHandler.sendMessage(msg);
                }

            };
            connection.removePacketListener(mErrorPacketListener);
            connection.addPacketListener(mErrorPacketListener, error);


            isMessageFilterListener = true;
        }
    }

    //?���? ?��?��?��
    private Handler notiHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            Message message = (Message) msg.obj;

            PacketExtension delayPk = message.getExtension("delay", "urn:xmpp:delay");
            if(delayPk !=  null){
                return;
            }

            String messageBody;

            if(Define.LOG_YN) {
                Log.d(Define.LOG_TAG, "what[" + msg.what + "]");
                Log.d(Define.LOG_TAG, "body[" + message.getBody() + "]");
            }
            switch (msg.what) {

                case MESSAGE_NORMAL:
                    //SDSLog.d(SDSLog.TAGIDX_XMPP,"MESSAGE_NORMAL");
                    if ((messageBody = message.getBody()) != null) {
                        String roomJID = StringUtils.parseBareAddress(message.getFrom());
                        String fromUserID = StringUtils.parseResource(message.getFrom());
                        xmppChatListener.onXmppReceiveMessege(onXmppChatListener.MassegeType.normal, roomJID, fromUserID, messageBody);
                    }
                    break;

                case MESSAGE_CHAT:
                case MESSAGE_GROUPCHAT:
                    if (message != null) {

                        messageBody = message.getBody();

                        PacketExtension messageType_pk = message.getExtension("message_type", "wezone");
                        MessageTypeExtension messageTypeExtension = (messageType_pk != null) ? (MessageTypeExtension) messageType_pk : null;

                        PacketExtension type_pk = message.getExtension("from", "wezone");
                        FromExtension fromExtension = (type_pk != null) ? (FromExtension) type_pk : null;

                        PacketExtension msg_pk = message.getExtension("msgkey", "wezone");
                        MsgKeyExtension msgKeyExtension = (msg_pk != null) ? (MsgKeyExtension) msg_pk : null;

                        PacketExtension packetExension = message.getExtension("wezone", "wezone:m");
                        CustomExtension customExtension = (packetExension != null) ? (CustomExtension) packetExension : null;

                        String toId = StringUtils.parseBareAddress(message.getTo());

                        String sFrom = fromExtension == null ? null : fromExtension.getFrom();
                        String fromId = StringUtils.parseBareAddress(message.getFrom());
                        String resource = StringUtils.parseResource(message.getFrom());

                        onXmppChatListener.MassegeType mType = onXmppChatListener.MassegeType.chat;
                        if(msg.what == MESSAGE_GROUPCHAT){
                            mType = onXmppChatListener.MassegeType.groupchat;
                        }

                        if(messageTypeExtension != null){
                            String message_type = messageTypeExtension.getMessageType();
                            if(WezoneUtil.isEmptyStr(message_type)){
                                xmppChatListener.onXmppReceiveMessege(mType, null, fromId, messageBody);
                            }else{

                                //Command
                                if("9".equals(message_type)){
                                    String msgkey = msgKeyExtension.getMsgKey();

                                    Gson gSon = new Gson();
                                    try{
                                        Data_MsgKeyList data = gSon.fromJson(getStringWithDecode(msgkey.trim()), Data_MsgKeyList.class);
                                        xmppChatListener.onXmppReceiveCommand(mType,fromId,data.msgkeys);
                                    }catch (Exception e){

                                    }
                                }else{
                                    if (customExtension != null) {

                                        String other_user_uuid = customExtension.getUser_uuid();
                                        String other_user_name = customExtension.getUserName();
                                        String other_user_image_url = customExtension.getUserImgUrl();

                                        String msgkey = msgKeyExtension.getMsgKey();
                                        xmppChatListener.onXmppReceiveMessage(mType,fromId,sFrom,other_user_uuid,other_user_name,other_user_image_url,messageBody,msgkey);

                                    }else{
                                        xmppChatListener.onXmppReceiveMessege(mType, null, fromId, messageBody);
                                    }
                                }
                            }
                        }else{
                            xmppChatListener.onXmppReceiveMessege(mType, null, fromId, messageBody);
                        }
                    }
                    break;

                case MESSAGE_INVITE: {

                }

                case MESSAGE_ERROR: {
                    xmppChatListener.onXmppReceiveError(message.getBody());
                }
                break;
            }
        }

    };

    private ConnectionConfiguration getConnectionConfig() {
        if(Define.LOG_YN) {
            Log.d(Define.LOG_TAG, "host :" + host + " port : " + port + " serviceName :" + serviceName);
        }

        ConnectionConfiguration connConfig = new ConnectionConfiguration(host, port, serviceName);
        connConfig.setReconnectionAllowed(true);

        if(Define.LOG_YN) {
            connConfig.setDebuggerEnabled(true);
        }else{
            connConfig.setDebuggerEnabled(false);
        }

        connConfig.setCompressionEnabled(true);
        connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);


        SmackConfiguration.setPacketReplyTimeout(5000);
        return connConfig;
    }

    public void connect() {
        if (isConnected() == false || isLogin() == false) {
            (new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                }

                @Override
                protected String doInBackground(String... params) {
                    //				if(connection.isSocketClosed()){
                    //				if(isReachable(host,port)){

                    if (connection == null) {
                        connection = new XMPPConnection(getConnectionConfig());
                    }

                    if (connection.isConnected() == false && mIsConnecting == false) {
                        mIsConnecting = true;
                        try {
                            if (connectionListener != null)
                                connection.removeConnectionListener(connectionListener);

                            connectionListener = new ConnectionListener() {
                                @Override
                                public void reconnectionSuccessful() {
                                    if(Define.LOG_YN) {
                                        Log.d(Define.LOG_TAG, "XMPP-리스?�� reconnectionSuccessful()");
                                    }
                                }

                                @Override
                                public void reconnectionFailed(Exception arg0) {
                                    if(Define.LOG_YN) {
                                        Log.d(Define.LOG_TAG, "XMPP-리스?�� reconnectionFailed() = " + arg0);
                                    }
                                }

                                @Override
                                public void reconnectingIn(int arg0) {
                                    if(Define.LOG_YN) {
                                        Log.d(Define.LOG_TAG, "XMPP-리스?�� reconnectingIn()=" + arg0);
                                    }
                                }

                                @Override
                                public void connectionClosedOnError(Exception e) {
                                    if (xmppChatListener != null)
                                        xmppChatListener.onXmppClosedOnError(e);
                                }

                                @Override
                                public void connectionClosed() {
                                    if(Define.LOG_YN) {
                                        Log.d(Define.LOG_TAG, "XMPP-리스?�� connectionClosed()");
                                    }
                                    if (xmppChatListener != null)
                                        xmppChatListener.onXmppClosed();
                                }
                            };
                            connection.addConnectionListener(connectionListener);

                            if(Define.LOG_YN) {
                                Log.d(Define.LOG_TAG, "connection.connect()");
                            }
                            connection.connect();
                            mConnectionId = connection.getConnectionID();

                            if(Define.LOG_YN) {
                                Log.d(Define.LOG_TAG, "connecte getConnectionID()=" + mConnectionId);
                            }
                            if (xmppChatListener != null)
                                xmppChatListener.onXmppConnected();
                            //*/
                        } catch (XMPPException e1) {
                            if(Define.LOG_YN) {
                                Log.d(Define.LOG_TAG, "connecte Exception=" + e1);
                            }
//								e1.printStackTrace();
                        } finally {
                            mIsConnecting = false;
                        }
                    }

                    if (connection.isConnected() == true) {
                        if (connection.isAuthenticated() == false) {
                            try {
                                if(WezoneUtil.isEmptyStr(username) || WezoneUtil.isEmptyStr(username)) {
                                    if (xmppChatListener != null) {
                                        xmppChatListener.onXmppReceiveError("no login data");
                                    }
                                }else{
                                    connection.login(username, password, resource);

                                    if (xmppChatListener != null) {
                                        xmppChatListener.onXmppLoginOK();
//                                    setStatusMode(StatusMode.available);
                                    }
                                }
                            } catch (XMPPException e) {
                                if(Define.LOG_YN) {
                                    Log.d(Define.LOG_TAG, "connection.login exception= " + e);
                                }
                            } catch (IllegalStateException e){

                            }finally {

                            }
                        }

                    }
                    //				}
                    return null;
                }
            }).execute();
        }
    }

    public void close() {
        mIsConnecting = false;

        if (chatMap != null && chatMap.isEmpty() == false)
            chatMap.clear();

        if (connection != null && connection.isConnected() == true) {

            try {
//                leaveAll();

                disconnect();
            } catch (Exception e) {
                if(Define.LOG_YN) {
                    Log.d(Define.LOG_TAG, "close() e=" + e);
                }
            }
        }
    }

    private void disconnect() {
        if (connection != null && connection.isConnected() == true) {
            if (connectionListener != null) {
                connection.removeConnectionListener(connectionListener);
                connectionListener = null;
            }
            (new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                }

                @Override
                protected String doInBackground(String... params) {
                    if (connection != null && connection.isConnected() == true) {
                        try {
                            connection.disconnect();

                            if (xmppChatListener != null)
                                xmppChatListener.onXmppClosed();

                        } catch (Exception e) {
                            if(Define.LOG_YN) {
                                Log.d(Define.LOG_TAG, "disconnect() e=" + e);
                            }
                        }
                    }
                    return null;
                }
            }).execute();
        }
    }

    private boolean isReachable(String host, int port) {
        try {
            SocketAddress sockaddr = new InetSocketAddress(host, port);
            Socket sock = new Socket();
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setStatusMode(StatusMode mode) {
        if (isConnected()) {
            Type presenceType = null;
            Mode presenceMode = null;
            Presence presence = null;

            if (mode == StatusMode.unavailable) {
                presenceType = Type.unavailable;
            } else {
                presenceType = Type.available;
            }

            if (mode == StatusMode.chat) {
                presenceMode = Mode.chat;
            } else if (mode == StatusMode.available) {
                presenceMode = Mode.available;
            } else if (mode == StatusMode.away) {
                presenceMode = Mode.away;
            }

            presence = new Presence(presenceType);
            presence.setMode(presenceMode);
            presence.setPriority(128);

            connection.sendPacket(presence);
        }
    }

    public boolean sendMessage(String to, String type, Data_Message data_message, String other_uuid, String user_name, String user_img_url, String msgkey) {

        if (isConnected()) {

            Message msg = new Message(to, Message.Type.chat);
            msg.setTo(to);

//            String decodeStr = UIControl.encodeBase64WithString(data_message.content);
            msg.setBody(data_message.txt);

            MessageTypeExtension mte = new MessageTypeExtension();
            mte.setMessageType(type);

            FromExtension fromEx = new FromExtension();
            fromEx.setFrom(data_message.sfrom);

            MsgKeyExtension msgkeyEx = new MsgKeyExtension();
            msgkeyEx.setMsgKey(msgkey);

            if(other_uuid != null){
                CustomExtension ce = new CustomExtension();
                ce.setUserUuid(other_uuid);
                ce.setUserName(user_name);
                ce.setUserImageUrl(user_img_url);
                msg.addExtension(ce);
            }

            msg.addExtension(mte);
            msg.addExtension(fromEx);
            msg.addExtension(msgkeyEx);


            connection.sendPacket(msg);

            return true;
        } else {
            return false;
        }
    }

    public boolean sendMessageToRoom(String to, String type, Data_Message data_message, String other_uuid, String user_name, String user_img_url,String msgkey) {

        if (isConnected()) {

            Message msg = new Message(to, Message.Type.groupchat);
            msg.setTo(to);

//            String decodeStr = UIControl.encodeBase64WithString(data_message.content);
            msg.setBody(data_message.txt);

            MessageTypeExtension mte = new MessageTypeExtension();
            mte.setMessageType(type);

            FromExtension fromEx = new FromExtension();
            fromEx.setFrom(data_message.sfrom);

            MsgKeyExtension msgkeyEx = new MsgKeyExtension();
            msgkeyEx.setMsgKey(msgkey);

            if(other_uuid != null){
                CustomExtension ce = new CustomExtension();
                ce.setUserUuid(other_uuid);
                ce.setUserName(user_name);
                ce.setUserImageUrl(user_img_url);
                msg.addExtension(ce);
            }

            msg.addExtension(mte);
            msg.addExtension(fromEx);
            msg.addExtension(msgkeyEx);

            MultiUserChat muc = (MultiUserChat) chatMap.get(to);
            if(muc != null){
                try {
                    muc.sendMessage(msg);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }

            return true;
        } else {
            return false;
        }
    }


    public void joinRoom(String roomJID){

        if (isConnected() && connection.isAuthenticated()) {
            try {

                MultiUserChat muc = (MultiUserChat) chatMap.get(roomJID);
                String nickName = nickNameMap.get(roomJID);

                if (muc.isJoined() == false) {
                    muc.leave();
                    muc.join(nickName);
                }

            } catch (Exception e) {
                if(Define.LOG_YN) {
                    Log.d(Define.LOG_TAG, "join exception=" + e);
                }
            }
        }
    }

    public void leaveRoom(){
        if(mChatRoom != null){
            mChatRoom.leave();
        }
    }

    public void joinRooms(){
        if (isConnected() && connection.isAuthenticated()) {

            try {
                Set<String> keys = chatMap.keySet();

                for(String key : keys){

                        MultiUserChat muc = (MultiUserChat) chatMap.get(key);
                        String nickName = nickNameMap.get(key);

                        if (!muc.isJoined()) {
                            muc.leave();
                            muc.join(nickName);
                        }
                }

            } catch (Exception e) {

            }
        }
    }

    public void leaveMUC(String roomJID) {
        MultiUserChat muc = (MultiUserChat) chatMap.get(roomJID);

        if (muc != null) {
            leaveMUC(roomJID, muc.getNickname());
        }
    }

    public void leaveMUC(String roomJID, String nickname) {
        if (isConnected()) {
            try {
                MultiUserChat muc = (MultiUserChat) chatMap.get(roomJID);
                if (muc == null)
                    muc = new MultiUserChat(connection, roomJID);

                if (muc.isJoined() == false)
                    muc.join(nickname);

                muc.leave();
            } catch (Exception e) {
                if(Define.LOG_YN) {
                    Log.d(Define.LOG_TAG, "leaveMUC exception=" + e);
                }
            }
        }
    }

    public void leaveAll() {

        Set<String> keys = chatMap.keySet();

        for (String key : keys) {
            MultiUserChat muc = (MultiUserChat) chatMap.get(key);
            muc.leave();
        }
    }

    public void createGroupChat(String roomJID, String nickname) throws XMPPException {
        if (isConnected()) {
            MultiUserChat muc = new MultiUserChat(connection, roomJID);
            if(!chatMap.containsKey(roomJID)){
                chatMap.put(roomJID, muc);
                nickNameMap.put(roomJID,nickname);
            }
        }
    }

    public void invite(String roomJID, String JID) {
        if (isConnected()) {
            try {
                MultiUserChat muc = (MultiUserChat) chatMap.get(roomJID);

                if (muc == null) {
                    return;
                }

                muc.invite(JID + "@" + serviceName, "invite you in the room," + roomJID);

                if(Define.LOG_YN) {
                    Log.d(Define.LOG_TAG, "intvite " + roomJID + "JID" + JID);
                }
            } catch (Exception e) {
                if(Define.LOG_YN) {
                    Log.d(Define.LOG_TAG, "intvite exception=" + e);
                }
            }
        }
    }

    ParsingExceptionCallback callback = new ParsingExceptionCallback() {

        public void handleUnparsablePacket(UnparsablePacket stanzaData) throws Exception {

            if(Define.LOG_YN) {
                Log.d(Define.LOG_TAG, "callback[" + stanzaData.getContent());
                Log.d(Define.LOG_TAG, "callback[" + stanzaData.getParsingException().getMessage());
            }
        }
    };


    public String getStringWithDecode(String src) {

        if (src == null)
            return null;

        String data = null;

        try {
            data = URLDecoder.decode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    private SSLContext trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
//            sc = SSLContext.getInstance("TLS","AndroidOpenSSL");
            sc = SSLContext.getInstance("TLSv1", "AndroidOpenSSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sc;
    }

    public String getNodeItem(String str, String element_name) {

        if (str == null)
            return "";

        byte[] buffer = str.getBytes();
        ByteArrayInputStream bai = new ByteArrayInputStream(buffer);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = builder.parse(bai);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node node = doc.getDocumentElement();

        NodeList nl = node.getChildNodes();

        String strTemp = "";

        for (int i = 0; i < nl.getLength(); i++) {
            Node nd = nl.item(i);

            if (nd.getNodeName().equals(element_name)) {
                strTemp = nd.getNodeValue();
            }
        }
        return strTemp;
    }

}
