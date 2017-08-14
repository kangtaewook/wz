package com.vinetech.ui.custompopup;

import android.content.Context;
import android.view.View;

/**
 * @author 
 * 
 * Custom Popup 의 Base Custom Popup 
 * 객체를 만들때 항상 Custom Popup 상속받아 사용
 */
public abstract class CustomPopup {

	protected View m_View = null;
	
	public CustomPopup(Context context, int resouceId) {
		m_View = View.inflate(context, resouceId, null);
	}
	
	public View findViewById(int id) {
		return m_View.findViewById(id);
	}
	
	public void onDestory(){
		if(m_View != null) {
			m_View.destroyDrawingCache();			
		}
	}
	
	public View getView(){
		return m_View;
	}

	public abstract void onStartAnimation(); // Animation 이 있을경우 Animation을 시작한다.
	public abstract void onStopAnimation();  // Animation 이 있을 경우 Animation을 멈춘다.
}
