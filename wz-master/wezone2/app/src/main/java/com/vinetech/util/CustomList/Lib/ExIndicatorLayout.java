/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.vinetech.util.CustomList.Lib;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import com.vinetech.wezone.R;


public class ExIndicatorLayout implements AnimationListener {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 30;

	private Animation mInAnim, mOutAnim;

	private final Animation mRotateAnimation, mResetRotateAnimation;

	private LinearLayout mLayout;

	public ExIndicatorLayout(Context context, LinearLayout linearLayout) {
		
		mLayout 		 = linearLayout;
		
		int inAnimResId  = R.anim.slide_out_to_top;
		int outAnimResId = R.anim.slide_in_from_top;

		mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
		mInAnim.setAnimationListener(this);

		mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
		mOutAnim.setAnimationListener(this);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mResetRotateAnimation.setInterpolator(interpolator);
		mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);

	}

	public final boolean isVisible() {
		Animation currentAnim = mLayout.getAnimation();
		if (null != currentAnim) {
			return mInAnim == currentAnim;
		}

		return mLayout.getVisibility() == View.VISIBLE;
	}

	public void hide() {
//		mLayout.startAnimation(mOutAnim);
		if(mLayout.getVisibility() != View.GONE){
			mLayout.setVisibility(View.GONE);
		}
	}

	public void show() {
			mLayout.setVisibility(View.VISIBLE);
		
//		mLayout.startAnimation(mInAnim);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == mOutAnim) {
			if(mLayout.getVisibility() != View.GONE){
				mLayout.setVisibility(View.GONE);
			}
		} else if (animation == mInAnim) {
			if(mLayout.getVisibility() != View.VISIBLE){
				mLayout.setVisibility(View.VISIBLE);
			}
		}

		mLayout.clearAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// NO-OP
	}

	@Override
	public void onAnimationStart(Animation animation) {
		mLayout.setVisibility(View.VISIBLE);
	}
}
