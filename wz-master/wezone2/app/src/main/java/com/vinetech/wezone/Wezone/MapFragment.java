package com.vinetech.wezone.Wezone;

/**
 * Created by galuster on 2017-02-14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.vinetech.wezone.R;

//import static com.vinetech.wezone.Wezone.RangeSliderView.OnSlideListener;

public class MapFragment extends Fragment {

    private static final String LOG_TAG = "NMapViewer";
    private static final String CLIENT_ID = "ErSpklJXivadgvI7urOb";// 애플리케이션 클라이언트 아이디 값
    private static final boolean DEBUG = true;

    //private RangeSliderView smallSlider;
    private NMapController mNMapController;
    private NMapContext mMapContext;
    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;
    private GpsInfo gps;
    private NMapPOIitem mFloatingPOIitem;
    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private RelativeLayout linearlayout_fragment;
    private OnViewerListener listener;
    private WezoneDistanceActivity mAcvitity;

    private int zoomLevels[] = {14, 13, 11, 10};
    private int mCurrentIdx;

    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAcvitity = (WezoneDistanceActivity) getActivity();
        linearlayout_fragment = (RelativeLayout) getView().findViewById(R.id.linearlayout_fragment);

        NMapView mapView = (NMapView) getView().findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mMapContext.setupMapView(mapView);

        linearlayout_fragment.setVisibility(View.GONE);

        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        mapView.requestFocus();

        mapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mNMapController = mapView.getMapController();

        // use built in zoom controls
        NMapView.LayoutParams lp = new NMapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
        //mapView.setBuiltInZoomControls(true, lp);

        // 지도의 최대, 최소 축척 레벨을 설정한다.
        mNMapController.setZoomLevelConstraint(10, 14);

    }


    private void restoreInstanceState() {

//        mylocation = new WezoneDistanceActivity();
//
        latitude = mAcvitity.getLatitude();
        longitude = mAcvitity.getLongitude();

        mNMapController.setMapCenter(new NGeoPoint(longitude, latitude), mAcvitity.getLevelFromDistance());
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
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

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

        @Override
        public boolean isLocationTracking() {
            if (mMapLocationManager != null) {
                if (mMapLocationManager.isMyLocationEnabled()) {
                    return mMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }

    };


    /* MapView State Change Listener*/
    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {


        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                // restore map view state such as map center position and zoom level.
                restoreInstanceState();

            } else { // fail

                //Toast.makeText(MainActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.i("MAP", "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.i("MAP", "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int level) {

            Log.i(LOG_TAG, "level" + level + "");

            if (listener != null) {
                listener.onZoomLevelChange(nMapView, level);
            }
        }

    };


    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

        @Override
        public void onLongPress(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressCanceled(NMapView mapView) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTouchDown(NMapView mapView, MotionEvent ev) {

            mNMapController.setMapCenter(longitude, latitude);
        }

        @Override
        public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
        }

        @Override
        public void onTouchUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub
            mNMapController.setMapCenter(longitude, latitude);

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

    public void setCurrentIndex(int index) {
        mCurrentIdx = index;
    }

    public void onChangeIndex(int index) {

        if (mCurrentIdx >= index) {
            mNMapController.zoomIn();

            mNMapController.setZoomLevel(zoomLevels[index]);
            mCurrentIdx = index;

        } else if (mCurrentIdx < index) {
            mNMapController.zoomOut();
            mNMapController.setZoomLevel(zoomLevels[index]); // index 3
            mCurrentIdx = index;
        }
    }

    public void setOnViewer(OnViewerListener listener) {
        this.listener = listener;
    }

    public interface OnViewerListener {
        void onZoomLevelChange(NMapView nMapView, int level);
    }
}

