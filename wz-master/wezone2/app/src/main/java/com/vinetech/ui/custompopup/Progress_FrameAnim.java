package com.vinetech.ui.custompopup;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import com.vinetech.wezone.R;


/**
 * @author 
 * 
 * 트윈 애니메이션 팝업
 */
public class Progress_FrameAnim extends CustomPopup {

	private ImageView m_ImageViewProgress		   = null;
	private AnimationDrawable m_FrameAnim				   = null;

	public Progress_FrameAnim(Context context, int resouceId, int animId) {
		super(context, resouceId);
		
		m_ImageViewProgress = (ImageView)findViewById(R.id.ImageView_Progress_Rotate);
		m_ImageViewProgress.setBackgroundResource(animId);
		
		m_FrameAnim	  		= (AnimationDrawable)m_ImageViewProgress.getBackground();
	}

	@Override
	public void onStartAnimation() {
		if(m_FrameAnim != null){
			if(m_FrameAnim.isRunning()){
				m_FrameAnim.stop();
			}
			
			m_FrameAnim.start();			
		}
	}

	@Override
	public void onStopAnimation() {
		if(m_FrameAnim != null){
			if(m_FrameAnim.isRunning()){
				m_FrameAnim.stop();
			}
		}
	}
	
	@Override
	public void onDestory() {
		onStopAnimation();

//		int frameCnt =  m_FrameAnim.getNumberOfFrames();
//		for(int i=0; i<frameCnt; i++){
//			Drawable frame = m_FrameAnim.getFrame(i);
//			if (frame instanceof BitmapDrawable) {
//				((BitmapDrawable)frame).getBitmap().recycle();
//			}
//			frame.setCallback(null);
//		}


		if(m_ImageViewProgress != null){
			m_ImageViewProgress.destroyDrawingCache();			
		}
		
		super.onDestory();
	}
}
