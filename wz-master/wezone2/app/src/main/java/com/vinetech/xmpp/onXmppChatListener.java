package com.vinetech.xmpp;

import com.vinetech.wezone.Data.Data_MsgKey;

import java.util.ArrayList;

public interface onXmppChatListener {

	enum MassegeType{
		chat,
		groupchat,
		image,
		video,
		normal
	}
	
	void onXmppConnected(); //cky
	
	void onXmppLoginOK(); //cky
	
	void onXmppClosedOnError(Exception e); //cky
	
	void onXmppClosed(); //mhkim

	void onXmppReceiveCommand(MassegeType type, String fromID, ArrayList<Data_MsgKey> msgkeys);

	//only message
	void onXmppReceiveMessege(MassegeType type, String roomJID, String fromID, String messege);

	void onXmppReceiveMessage(MassegeType type, String fromID, String sfrom ,String other_user_uuid, String other_user_name, String other_user_img_url, String message, String msgkey);

	void onXmppReceiveError(String body);
	
}
