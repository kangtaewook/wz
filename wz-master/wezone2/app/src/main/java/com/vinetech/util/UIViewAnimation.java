package com.vinetech.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.vinetech.wezone.R;

public class UIViewAnimation {

	public static void VisibleViewWithAnimation(View v){
		VisibleViewWithAnimation(v, 1000);
	}
	
	public static void VisibleViewWithAnimation(View v, long duration){
		Animation animation = new AlphaAnimation(0, 1);
		animation.setDuration(duration); 
		v.setVisibility(View.VISIBLE);
		v.setAnimation(animation);
	}
	
	
	public static void VisibleViewWithTransAnimationBToU(Context c, View v){
		VisibleViewWithTransAnimationBToU(c,v,null);
	}
	
	public static void VisibleViewWithTransAnimationBToU(Context c, View v, AnimationListener listener){
		Animation ani = new TranslateAnimation(0, 0, 500, 0);
		ani.setInterpolator(AnimationUtils.loadInterpolator(c, android.R.anim.accelerate_interpolator));
		ani.setAnimationListener(listener);
		ani.setDuration(500);
		v.setVisibility(View.VISIBLE);
		v.setAnimation(ani);
	}
	
	public static void VisibleViewWithTransAnimationUToB(Context c, View v){
		VisibleViewWithTransAnimationUToB(c,v,null);
	}
	
	public static void VisibleViewWithTransAnimationUToB(Context c, View v, AnimationListener listener){
		Animation ani = new TranslateAnimation(0, 0, 500, 0);
		ani.setInterpolator(AnimationUtils.loadInterpolator(c, android.R.anim.overshoot_interpolator));
		ani.setAnimationListener(listener);
		ani.setDuration(500);
		v.setVisibility(View.VISIBLE);
		v.setAnimation(ani);
	}
	
	public static void VisibleViewWithTransAnimationRToL(Context c, View v){
		VisibleViewWithTransAnimationRToL(c,v,null);
	}
	
	public static void VisibleViewWithTransAnimationRToL(Context c, View v, AnimationListener listener){
		Animation ani = new TranslateAnimation(1000, 0, 0, 0);
		ani.setInterpolator(AnimationUtils.loadInterpolator(c, android.R.anim.overshoot_interpolator));
		ani.setAnimationListener(listener);
		ani.setDuration(500);
		v.setVisibility(View.VISIBLE);
		v.setAnimation(ani);
	}
	
	public static void GoneViewWithTransAnimationLToR(Context c, View v){
		GoneViewWithTransAnimationLToR(c,v,null);
	}
	
	public static void GoneViewWithTransAnimationLToR(Context c, View v, AnimationListener listener){
		Animation ani = new TranslateAnimation(0,1000, 0, 0);
		ani.setInterpolator(AnimationUtils.loadInterpolator(c, android.R.anim.anticipate_interpolator));
		ani.setAnimationListener(listener);
		ani.setDuration(500);
//		v.setVisibility(View.GONE);
//		v.setAnimation(ani);
		v.startAnimation(ani);
	}
	
	
	public static void GoneViewWithAnimation(View v){
		GoneViewWithAnimation(v,1000);
	}
	
	public static void GoneViewWithAnimation(View v, long duration){
		Animation animation = new AlphaAnimation(0, 1);
		animation.setDuration(1000); 
		v.setVisibility(View.GONE);
		v.setAnimation(animation);
	}
	
	public static void GoneViewWithTransAnimationUToB(Context c, View v){
		GoneViewWithTransAnimationUToB(c,v,null);
	}
	
	public static void GoneViewWithTransAnimationUToB(Context c, View v, AnimationListener listener){
		Animation ani = new TranslateAnimation(0, 0, 0, 1000);
		ani.setInterpolator(AnimationUtils.loadInterpolator(c, android.R.anim.accelerate_interpolator));
		ani.setAnimationListener(listener);
		ani.setDuration(500);
//		v.setVisibility(View.GONE);
		v.startAnimation(ani);
	}
	
	public static void GoneViewWithTransAnimationBToU(Context c, View v){
		GoneViewWithTransAnimationUToB(c,v,null);
	}
	
	public static void GoneViewWithTransAnimationBToU(Context c, View v, AnimationListener listener){
		Animation ani = new TranslateAnimation(0, 0, 0, -500);
		ani.setInterpolator(AnimationUtils.loadInterpolator(c, android.R.anim.accelerate_interpolator));
		ani.setAnimationListener(listener);
		ani.setDuration(500);
//		v.setVisibility(View.GONE);
		v.startAnimation(ani);
	}
	
	public static void ShakeAnimation(Context c, View v){
		Animation shkeAni = AnimationUtils.loadAnimation(c, R.anim.shake);
		v.startAnimation(shkeAni);
	}
}
