package com.vinetech.ui.custompopup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.vinetech.wezone.R;

public class CustomPopupManager {
	////////////////////////////////////////////////////////////
	// Define
	public static final int			POPUP_STYLE_DISMISS			= 0x00;
	public static final int			POPUP_STYLE_PROGRESS		= 0x01;
	
	public static final int			POPUP_SELECT_NONE			= 0x00;
	public static final int			POPUP_SELECT_ONE			= 0x01;
	public static final int			POPUP_SELECT_OK				= 0x02;
	public static final int			POPUP_SELECT_CANCEL			= 0x03;
	public static final int			POPUP_SELECT_CALL			= 0x04;
	public static final int			POPUP_SELECT_SMS			= 0x05;
	
	
	////////////////////////////////////////////////////////////
	// Context
	private Context m_Context					= null;
	
	private Dialog m_Dialog					= null;
	private CustomPopup 			m_CustomPopup				= null;
	
	////////////////////////////////////////////////////////////
	// Popup Window
	private int						m_nPopupStyle				= POPUP_STYLE_DISMISS;

	private OnDismissListener m_DismissListener			= null;
	
	
	public static CustomPopupManager show(Context context)
	{
		CustomPopupManager customPopup  = new CustomPopupManager(context);
		customPopup.onCreatePopup(new Progress_FrameAnim(context, R.layout.custom_popup_progress, R.drawable.loading_ani_coin));
		customPopup.onShow();
		return customPopup;
	}
	
	public CustomPopupManager(Context context) {
		m_Context 			= context;
	}

	public Context getContext(){
		return m_Context;
	}

	public void setOnDissmissListener(OnDismissListener dismissListener){
		m_DismissListener  = dismissListener;
	}
	
	public boolean onCreatePopup(CustomPopup customPopupItem) {
		//////////////////////////////////////
		// Check Object
		if(m_CustomPopup != null){
			m_CustomPopup.onStopAnimation();
			m_CustomPopup.onDestory();
		}
		
		//////////////////////////////////////
		// Set CustomPopup
		m_CustomPopup    = customPopupItem;
		
		if(m_CustomPopup == null){
			throw new NullPointerException("CustomPopup Object is Null");
		}
		
		View view 		 = m_CustomPopup.getView();
		
		if(view == null){
			throw new NullPointerException("popupView Object is Null");
		}
		
		////////////////////////////////////////////////////////
		// Popup Create
		m_Dialog	    = new Dialog(m_Context);
		
		if(m_DismissListener != null){
			m_Dialog.setOnDismissListener(m_DismissListener);		
		}
		
		m_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		m_Dialog.setContentView(view);
		m_Dialog.setCancelable(false);

		return true;
	}

	
	public void setMessage(String message)
	{
		if( m_CustomPopup != null && m_CustomPopup instanceof Progress_CustomCircle )
		{
			((Progress_CustomCircle)m_CustomPopup).setMessage(message);
		}
	}
	
	public void onShow(){
			
		////////////////////////////
		// Dialog Show

		if(m_Dialog != null && m_Dialog.isShowing())
			m_Dialog.dismiss();


		if(m_Dialog != null)
			m_Dialog.show();
		
		////////////////////////////
		// Start Animation
		if(m_CustomPopup != null)
			m_CustomPopup.onStartAnimation();
	}

	public boolean isShow(){
		return m_Dialog.isShowing();
	}

	public void onDestroy() {

		try{
			onDismiss();

			if(m_CustomPopup != null) {
				m_CustomPopup.onDestory();
			}
		}catch (Exception e){
			m_Dialog = null;
			m_nPopupStyle = POPUP_STYLE_DISMISS;
			m_DismissListener = null;
		}
	}
	
	public void onDismiss() {
		
		if(m_CustomPopup != null){
			m_CustomPopup.onStopAnimation();
		}
		
		if(m_Dialog != null && m_Dialog.isShowing()) {
			m_Dialog.dismiss();				
		}
		
		m_nPopupStyle = POPUP_STYLE_DISMISS;
		
		if(m_DismissListener != null){
			m_DismissListener = null;
		}
	}
}
