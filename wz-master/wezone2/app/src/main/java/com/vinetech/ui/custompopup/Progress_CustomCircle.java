package com.vinetech.ui.custompopup;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinetech.wezone.R;

import static android.view.animation.Animation.RESTART;

/**
 * @author 
 * 
 * 동그란 이미지가 돌아가는 프로그래스 팝업
 */
public class Progress_CustomCircle extends CustomPopup {

	private final Interpolator ANIMATION_INTERPOLATOR	   = new LinearInterpolator();
	private final long  	   ROTATION_ANIMATION_DURATION = 1200;

	private TextView m_TextViewProgress;
	private ImageView m_ImageViewProgress		   = null;
//	private RotateAnimation    m_RotateAnimation		   = null;

	private ObjectAnimator m_ObjectAnimator = null;
	
	public Progress_CustomCircle(Context context, int resouceId) {
		super(context, resouceId);
		
		m_ImageViewProgress = (ImageView)findViewById(R.id.ImageView_Progress_Rotate);
		m_ImageViewProgress.setBackgroundResource(R.drawable.and_bg_loading);
		
		m_ObjectAnimator = ObjectAnimator.ofFloat(m_ImageViewProgress, "rotationY", 0, 720);
		m_ObjectAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
		m_ObjectAnimator.setDuration(ROTATION_ANIMATION_DURATION);
		m_ObjectAnimator.setRepeatCount(Animation.INFINITE);
        m_ObjectAnimator.setRepeatMode(RESTART);
        
//		m_RotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		m_RotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
//		m_RotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
//		m_RotateAnimation.setRepeatCount(Animation.INFINITE);
//		m_RotateAnimation.setRepeatMode(Animation.RESTART);
	}
	
	public void setMessage(String message)
	{
		if( m_TextViewProgress == null )
			m_TextViewProgress = (TextView)findViewById(R.id.TextView_Progress_Rotate);
		
		if( message != null && message.length() > 0 )
		{
			m_TextViewProgress.setText(message);
			m_TextViewProgress.setVisibility(View.VISIBLE);
		}
		else
			m_TextViewProgress.setVisibility(View.GONE);
			
	}
	
	@Override
	public void onStartAnimation() {
//		if(m_ImageViewProgress != null){
			if(m_ObjectAnimator != null){
//				m_ImageViewProgress.startAnimation(m_RotateAnimation);							
				m_ObjectAnimator.start();
			}
//		}
	}

	@Override
	public void onStopAnimation() {
		if(m_ObjectAnimator.isRunning()){
//			m_ImageViewProgress.clearAnimation();
			m_ObjectAnimator.end();
		}
	}
	
	@Override
	public void onDestory() {
		onStopAnimation();
		
		if(m_ImageViewProgress != null){
			m_ImageViewProgress.destroyDrawingCache();			
		}
		
		if(m_ObjectAnimator != null){
			m_ObjectAnimator.cancel();
		}
		
		super.onDestory();
	}
}
