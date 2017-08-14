package com.vinetech.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by galuster3 on 2016-05-19.
 */
public class KeyboardUtil {
    public interface KeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean keyboardVisible, int totalHeight, int keyboardHeight);
    }

    public static void setKeyboardVisibilityListener(Activity activity, final KeyboardVisibilityListener keyboardVisibilityListener) {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;

            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();
                if (mPreviousHeight != 0) {
                    if (mPreviousHeight > newHeight) {
                        // Height decreased: keyboard was shown
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(true,mPreviousHeight,mPreviousHeight - newHeight);
                    } else if (mPreviousHeight < newHeight) {
                        // Height increased: keyboard was hidden
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(false,newHeight, newHeight - mPreviousHeight);
                    } else {
                        // No change
                    }
                }
                mPreviousHeight = newHeight;
            }
        });
    }
}
