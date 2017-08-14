package com.vinetech.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vinetech.wezone.R;


/**
 * Created by galuster3 on 2016-05-16.
 */
public class LoadingFooterView extends LinearLayout {

    private ImageView imageview_footer_loading;
    private AnimationDrawable m_FrameAnim;

    public LoadingFooterView(Context context) {
        super(context);

        initView();
    }

    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.list_footer, this, false);
        addView(v);

        imageview_footer_loading = (ImageView) findViewById(R.id.imageview_footer_loading);
        imageview_footer_loading.setBackgroundResource(R.drawable.loading_ani_coin);

        m_FrameAnim	  		= (AnimationDrawable)imageview_footer_loading.getBackground();
    }

    public void onStartAnimation() {
        if(m_FrameAnim != null){
            if(m_FrameAnim.isRunning()){
                m_FrameAnim.stop();
            }

            m_FrameAnim.start();
        }
    }

    public void onStopAnimation() {
        if(m_FrameAnim != null){
            if(m_FrameAnim.isRunning()){
                m_FrameAnim.stop();
            }
        }
    }

    public void onDestory() {
        onStopAnimation();
        if(imageview_footer_loading != null){
            imageview_footer_loading.destroyDrawingCache();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onStartAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestory();
    }
}
