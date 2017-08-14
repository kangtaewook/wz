package com.vinetech.wezone.Wezone;

/**
 * Created by galuster on 2017-02-14.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.vinetech.util.AddCookiesInterceptor;
import com.vinetech.util.ReceivedCookiesInterceptor;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.WezoneRestful;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vinetech.wezone.Define.LOG_TAG;


public class WezoneMapFragment extends Fragment {

    WezoneActivity mActivity;

    public static final String WEZONE_MAP_FRAGMENT_CLICK = "WEZONE_MAP_FRAGMENT_CLICK";
    public static final String WEZONE_MAP_UPDATE = "WEZONE_MAP_UPDATE";
    public static final String WEZONE = "WEZONE";
    public static final String MAP_UPDATE = "MAP_UPDATE";
    public static final String MANAGE_TYPE = "MANAGE_TYPE";
    public static final String LATITUE = "LATITUE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String WEZONE_ID = "WEZONE_ID";


    public static Fragment getInstance(String longitude, String latitude, String manage_type, String mWezone_id, Data_WeZone mWezone, String map_update) {
        WezoneMapFragment f = new WezoneMapFragment();
        Bundle args = new Bundle();
        args.putString(LONGITUDE, longitude);
        args.putString(LATITUE, latitude);
        args.putString(MANAGE_TYPE, manage_type);
        args.putString(WEZONE_ID, String.valueOf(mWezone_id));
        if(map_update != null){
            args.putString(MAP_UPDATE, map_update);
        }
        if(mWezone != null){
            args.putSerializable(WEZONE, mWezone);
        }
        f.setArguments(args);
        return f;
    }

    private  OnFragmentSetListener onFragmentSetListener;
    private NMapController mNMapController;

    private NMapContext mMapContext;
    private static final boolean DEBUG = true;

    private static final String CLIENT_ID = "ErSpklJXivadgvI7urOb";// 애플리케이션 클라이언트 아이디 값

    private NGeoPoint mNGeoPoint;

    private NMapPOIitem mFloatingPOIitem;
    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private NMapOverlayManager mMapOverlayManager;
    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener;
    private NMapPOIdata poiData;

    private String longitude;
    private String latitude;
    private String wezone_id;
    private String manage_type;
    private String map_update;

    private ImageView imageView;
    private RelativeLayout linearlayout_fragment;
    private GpsInfo mGpsInfo;

    private Data_WeZone data_weZone;
    private Data_WeZone mWezone;
    public WezoneRestful wezoneRestful;
    private int count=0;
    private String myuuid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        longitude = args.getString(LONGITUDE);
        latitude = args.getString(LATITUE);
        manage_type = args.getString(MANAGE_TYPE);
        wezone_id = args.getString(WEZONE_ID);
        map_update = args.getString(MAP_UPDATE);
        data_weZone = (Data_WeZone)args.getSerializable(WEZONE);
        if(longitude == null || latitude == null){
            return;
        }
        mNGeoPoint = new NGeoPoint(Double.valueOf(longitude), Double.valueOf(latitude));
        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (WezoneActivity) getActivity();
        myuuid = mActivity.getShare().getMyInfo().uuid;
        mWezone = new Data_WeZone();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.interceptors().add(new AddCookiesInterceptor(getContext()));
        httpClient.interceptors().add(new ReceivedCookiesInterceptor(getContext()));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Define.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        wezoneRestful = retrofit.create(WezoneRestful.class);

        NMapView mapView = (NMapView) getView().findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mMapContext.setupMapView(mapView);

        mNMapController = mapView.getMapController();

        mapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        linearlayout_fragment = (RelativeLayout) getView().findViewById(R.id.linearlayout_fragment);

        if (manage_type != null) {
            if (manage_type.equals("M")) {
                if(data_weZone != null) {
                    if (myuuid != null && data_weZone.uuid != null){
                        if (myuuid.equals(data_weZone.uuid)) {
                            linearlayout_fragment.setVisibility(View.VISIBLE);
                            linearlayout_fragment.setOnClickListener(new NMapView.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mGpsInfo = new GpsInfo(getContext());
                                    // GPS 사용유무 가져오기
                                    if (mGpsInfo.isGetLocation()) {
                                        longitude = String.valueOf(mGpsInfo.getLongitude());
                                        latitude = String.valueOf(mGpsInfo.getLatitude());
                                        poidata();
                                    } else {
                                        // GPS 를 사용할수 없으므로
                                        Toast.makeText(getContext(), "GPS 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                }
                }
            }
        }

        mMapViewerResourceProvider = new NMapViewerResourceProvider(getContext());
        mMapOverlayManager = new NMapOverlayManager(getContext(), mapView, mMapViewerResourceProvider);

        //(2)NMapPOIdataOverlay 객체 생성 (여러 개의 오버레이 아이템들을 하나의 오버레이 객체에서 관리하기 위함)
        int markerId = NMapPOIflagType.PIN;

        //set POI data
        poiData = new NMapPOIdata(2, mMapViewerResourceProvider);

        poiData.beginPOIdata(2);

        double lat = Double.valueOf(latitude);
        double lon = Double.valueOf(longitude);

        poiData.addPOIitem(lon, lat, "Pizza777-111", markerId, 0);
        poiData.endPOIdata();

        //create POI data overlay

        NMapPOIdataOverlay poIdataOverlay = mMapOverlayManager.createPOIdataOverlay(poiData, null);

        //(3) 해당 오버레이 객체에 포함된 전체 아이템이 화면에 표시되도록 지도 중심 및 축적 레벨을 변경하기 위함
        //show all POI data

        poIdataOverlay.showAllPOIdata(0);

        //(4) 아이템의 선택 상태 or 말풍선 선택되는 경우를 처리하는 이벤트 리스너
        //set event listener to the overlay

        poIdataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

        //(5) 오버레이 아이템 클릭 시 표시되는 말풍선 오버레이 클래스 NMapCalloutOverlay 이벤트 리스너
        //register callout overlay listener to sustomizeit.

      if(mWezone.latitude == null || mWezone.longitude == null){
            mWezone.latitude = String.valueOf(lat);
            mWezone.longitude = String.valueOf(lon);
            onFragmentSetListener.onFragmentPickerSet(mWezone.latitude, mWezone.longitude, "");
        }
    }


    public void poidata(){
        //(2)NMapPOIdataOverlay 객체 생성 (여러 개의 오버레이 아이템들을 하나의 오버레이 객체에서 관리하기 위함)

        //set POI data
        poiData = new NMapPOIdata(1, mMapViewerResourceProvider);

        poiData.beginPOIdata(1);

        double lat = Double.valueOf(latitude);
        double lon = Double.valueOf(longitude);

        NMapPOIdataOverlay poIdataOverlay = mMapOverlayManager.createPOIdataOverlay(poiData, null);

        //(3) 해당 오버레이 객체에 포함된 전체 아이템이 화면에 표시되도록 지도 중심 및 축적 레벨을 변경하기 위함

        //show all POI data
        mWezone.latitude = String.valueOf(lat);
        mWezone.longitude = String.valueOf(lon);
        if(mWezone.longitude == "0.0" || mWezone.latitude == "0.0"){
            Toast.makeText(getContext(),"위치 업데이트 다시 클릭 해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        onFragmentSetListener.onFragmentPickerSet(mWezone.latitude, mWezone.longitude, WEZONE_MAP_FRAGMENT_CLICK);

    }

    private void restoreInstanceState() {
        mNMapController.setMapCenter(mNGeoPoint, 14);
    }

    /* MapView State Change Listener*/
    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {


        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                // restore map view state such as map center position and zoom level.
                restoreInstanceState();

            } else { // fail

            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {

        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {
        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int level) {

        }

    };

    @Override
    public void onStart() {
        super.onStart();
        mMapContext.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }

    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }

    /* NMapDataProvider Listener */
    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));
            }

            if (errInfo != null) {
                Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());


                return;
            }

            if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                if (placeMark != null) {
                    mFloatingPOIitem.setTitle(placeMark.toString());
                }
                mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
            }
        }

    };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OnFragmentSetListener) {
            onFragmentSetListener = (OnFragmentSetListener) context;
        }else{
            throw new RuntimeException(context.toString()
                + "dddd");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentSetListener = null;
    }

    public interface OnFragmentSetListener {
        void onFragmentPickerSet(String lat, String lon, String mtype);
    }
}

