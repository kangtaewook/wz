package com.vinetech.wezone.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconManageActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.R;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BeaconFragment extends Fragment {

    private static final String POSITON = "position";
    private static final String BEACON_DATA = "beacon_data";

    private CardView cardView;

    private BaseActivity mActivity;

    public static Fragment newInstance(Context context, int pos, Data_Beacon beaconInfo) {
        Bundle b = new Bundle();
        b.putInt(POSITON, pos);
        b.putSerializable(BEACON_DATA,beaconInfo);
        return Fragment.instantiate(context, BeaconFragment.class.getName(), b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        mActivity = (BaseActivity) getActivity();

        final Data_Beacon beaconInfo = (Data_Beacon)this.getArguments().getSerializable(BEACON_DATA);

        View view = inflater.inflate(R.layout.beacon_fragment, container, false);

        cardView = (CardView) view.findViewById(R.id.cardView);
//        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

//        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
//        cardView.setContentPadding(-padding,-padding,-padding,-padding);

//        RelativeLayout relativelayout_body = (RelativeLayout) view.findViewById(relativelayout_body);

        TextView textview_beacon_name = (TextView) view.findViewById(R.id.textview_beacon_name);
        TextView textview_desc = (TextView) view.findViewById(R.id.textview_desc);

        if(beaconInfo.name == null){
            textview_beacon_name.setText("새로운 WeCON");
            textview_desc.setText("추가해 주세요");
        }else{
            textview_beacon_name.setText(beaconInfo.name);
            textview_desc.setText(beaconInfo.mac);
        }

        ImageView imageview_icon = (ImageView) view.findViewById(R.id.imageview_icon);
        if(WezoneUtil.isEmptyStr(beaconInfo.img_url) == false){
            mActivity.showImageFromRemote(beaconInfo.img_url,R.drawable.im_beacon_add,imageview_icon);
        }else{
            imageview_icon.setImageResource(R.drawable.im_beacon_add);
        }

        TextView textview_distance = (TextView) view.findViewById(R.id.textview_distance);
        if(beaconInfo.distance != 0){
            double b = Math.round(beaconInfo.distance*100d) / 100d;
            textview_distance.setText(b+" m");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeaconManageActivity.startActivityWithEdit((BaseActivity) getActivity(),beaconInfo,beaconInfo.mac);
            }
        });

        LinearLayout linearlayout_plus = (LinearLayout) view.findViewById(R.id.linearlayout_plus);
        if(beaconInfo.isSearch){
            linearlayout_plus.setVisibility(View.VISIBLE);
        }else{
            linearlayout_plus.setVisibility(View.GONE);
        }
//        //handling click event
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ImageDetailsActivity.class);
//                intent.putExtra(DRAWABLE_RESOURE, imageArray[postion]);
//                startActivity(intent);
//            }
//        });

//        root.setScaleBoth(scale);

        return view;
    }

    public CardView getCardView() {
        return cardView;
    }
}
