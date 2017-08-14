package com.vinetech.wezone.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vinetech.wezone.R;
import com.vinetech.wezone.Wezone.WezoneManagerActivity;


public class EventFragment extends Fragment {

    private MainActivity activity;

    private CardView cardView;

    public static Fragment getInstance(int position) {
        EventFragment f = new EventFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        f.setArguments(args);

        return f;
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.event_fragment, container, false);
        cardView = (CardView) view.findViewById(R.id.cardView);

        LinearLayout linearlayout_btn_bg = (LinearLayout) view.findViewById(R.id.linearlayout_btn_bg);
        linearlayout_btn_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.moveActivity(new Intent(activity, WezoneManagerActivity.class));
            }
        });

        ImageView imageview_icon = (ImageView) view.findViewById(R.id.imageview_icon);
        ImageView imageview_entry_icon = (ImageView) view.findViewById(R.id.imageview_entry_icon);


        int pos = getArguments().getInt("position");

        int result = pos % 3;

        switch (result){
            case 0:
                imageview_icon.setImageResource(R.drawable.im_noti_one);
                break;
            case 1:
                imageview_icon.setImageResource(R.drawable.im_noti_two);
                break;
            case 2:
                imageview_icon.setImageResource(R.drawable.im_noti_three);
                break;

            default:
                imageview_icon.setImageResource(R.drawable.im_noti_three);
                break;
        }

        if(pos % 2 == 0){
            imageview_entry_icon.setVisibility(View.VISIBLE);
        }else{
            imageview_entry_icon.setVisibility(View.GONE);
        }

        return view;
    }

    public CardView getCardView() {
        return cardView;
    }
}
