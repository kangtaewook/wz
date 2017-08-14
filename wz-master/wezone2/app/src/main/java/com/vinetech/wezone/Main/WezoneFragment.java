package com.vinetech.wezone.Main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_WezoneList;
import com.vinetech.wezone.Wezone.GpsInfo;
import com.vinetech.wezone.Wezone.WezoneActivity;
import com.vinetech.wezone.Wezone.WezoneManagerActivity;
import com.vinetech.wezone.Wezone.WezoneSearchActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class WezoneFragment extends Fragment implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener {


    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String DELETE = "DELETE";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String WEZONE_FRAGMENT = "WEZONE_FRAGMENT";
    public static final String ENTRY_WAIT = "ENTRY_WAIT";
    public static final String ENTRY = "ENTRY";
    public static final String REMOVE = "REMOVE";
    public static final String NOTICE = "NOTICE";

    public MainActivity mActivity;
    public StickyGridHeadersGridView mGridView;
    public ArrayList<Data_WeZone> mWezoneInfos;
    public WezoneMainListAdapter mWezoneMainListAdapter;
    public LinearLayout list_noresult;
    public Data_WeZone mWezone;
    public FloatingActionMenu fab_menu;
    public FloatingActionButton fab_btn_search;
    public FloatingActionButton fab_btn_regist_wezone;
    public enum Direction {UP, DOWN}
    public Direction direction;
    public int mTotalCount = 0;
    public int mMyWezone_TotalCount = 0;
    public boolean mListViewLocked = true;

    private boolean mHasRequestedMore;
    private int down_value = 0;

    int mOffset = 0;
    int mLimit = 10;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    Double longitude;
    Double latitude;

    GpsInfo mGpsInfo;

    ImageView ImageView_wezone_gps_check;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();


        setting = mActivity.getSharedPreferences("setting", 0);
        editor = setting.edit();

        View view = getView();
        mGridView = (StickyGridHeadersGridView) view.findViewById(R.id.grid_view);

        view.setBackgroundColor(getContext().getResources().getColor(R.color.bright_foreground_material_dark));

        list_noresult = (LinearLayout) view.findViewById(R.id.list_noresult);

        mWezoneInfos = new ArrayList<>();
        mWezoneMainListAdapter = new WezoneMainListAdapter(mActivity, mWezoneInfos,"");
        mGridView.setAdapter(mWezoneMainListAdapter);
        mGridView.setOnScrollListener(this);

//        ImageView_wezone_gps_check = (ImageView) view.findViewById(R.id.ImageView_wezone_gps_check);

        fab_menu = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fab_menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    fab_menu.getMenuIconView().setImageResource(R.drawable.ic_add);
                    //새로운 액티비티로 dim 처리
//                    Intent intent = new Intent(getContext(), TransparentActivity.class);
//                    startActivity(intent);
                } else {
                    fab_menu.getMenuIconView().setImageResource(R.drawable.btn_more_black);
                }
            }
        });

        fab_btn_search = (FloatingActionButton) view.findViewById(R.id.fab_btn_search);
        fab_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mActivity.moveActivity(new Intent(mActivity,WezoneSearchActivity.class));
                WezoneSearchActivity.startActivityWith(mActivity);
                fab_menu.toggle(true);
            }
        });

        fab_btn_regist_wezone = (FloatingActionButton) view.findViewById(R.id.fab_btn_regist_wezone);
        fab_btn_regist_wezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WezoneManagerActivity.startActivityWithRegist(mActivity);  //wezone 생성(등록)
                // BunnyZoneManageActivity.startActivityWithRegist(mActivity,null);
                fab_menu.toggle(true);

            }
        });
        mGridView.setOnItemClickListener(this);

        mWezoneInfos.clear();
        gps(0);
//        getMyWezoneList();
//        getNearWezoneList();
    }

    public void gps(int value) {
        mGpsInfo = new GpsInfo(getContext());
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            //GPS ON
            longitude = mGpsInfo.getLongitude();
            latitude = mGpsInfo.getLatitude();

            editor.putString(LONGITUDE, String.valueOf(longitude));
            editor.putString(LATITUDE, String.valueOf(latitude));
            editor.commit();
            if(value != 0){
                //내 주변만 더보기
                getWezoneSearchList(true, String.valueOf(longitude), String.valueOf(latitude), mOffset, mLimit, "2","");
            }else{
                getMyWezoneList();
                getWezoneSearchList(true, String.valueOf(longitude), String.valueOf(latitude), mOffset, mLimit, "2","");
            }
        } else {
            //GPS OFF
            if(value != 0){
                //내 주변만 더보기
                getWezoneSearchListNOTGPS(true, mOffset, mLimit, "2","");
            }else{
                getMyWezoneListNotGPS();
                getWezoneSearchListNOTGPS(true, mOffset, mLimit, "2","");
            }

        }
    }

    public void getMyWezoneListNotGPS() {
        Call<Rev_WezoneList> wezoneListCall = mActivity.wezoneRestful.getMyWezoneList();

        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
            @Override
            public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                Rev_WezoneList resultData = response.body();
                if (mActivity.isNetSuccess(resultData)) {
                    if (resultData.list != null) {
                        mWezoneInfos.clear();
                        mWezoneInfos.addAll(resultData.list);

                        mMyWezone_TotalCount = Integer.valueOf(resultData.total_count);
                        if (mWezoneInfos.size() > 0) {
                            list_noresult.setVisibility(View.GONE);
                            mWezoneMainListAdapter.notifyDataSetChanged();
                        } else {
                            list_noresult.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (mWezoneInfos.size() > 0) {
                        list_noresult.setVisibility(View.GONE);
                        mWezoneMainListAdapter.notifyDataSetChanged();
                    } else {
                        list_noresult.setVisibility(View.VISIBLE);
                    }
                }
                for (int i = 0; i < mWezoneInfos.size(); i++) {
                    int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE, mWezoneInfos.get(i).wezone_id);
                    if (idx != -1) {
                        if ("G".equals(mWezoneInfos.get(i).location_type)) {
                            if (Define.TYPE_INVITED.equals(mWezoneInfos.get(i).manage_type)) {
                                //위치인식방법이 B 비콘에서 G GPS로 변경했을 때 manage_type = W 인 위존을 내 위존 리스트에서 삭제
                                mWezoneInfos.remove(i);
                            }
                        }
                    }
                }
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Rev_WezoneList> call, Throwable t) {
                if (mWezoneInfos.size() > 0) {
                    list_noresult.setVisibility(View.GONE);
                    mWezoneMainListAdapter.notifyDataSetChanged();
                } else {
                    list_noresult.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void getMyWezoneList() {
        //zone_possible 안내려옴
        Call<Rev_WezoneList> wezoneListCall = mActivity.wezoneRestful.getMyWezoneList(String.valueOf(longitude), String.valueOf(latitude));

        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
            @Override
            public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                Rev_WezoneList resultData = response.body();
                if (mActivity.isNetSuccess(resultData)) {
                    if (resultData.list != null) {
                        mWezoneInfos.clear();
                        mMyWezone_TotalCount = Integer.valueOf(resultData.total_count);
                        for (Data_WeZone wezone : resultData.list) {
                            wezone.headerId = Data_WeZone.HEADER_MYZONE;
                            mWezoneInfos.add(wezone);
                        }

                        if (mWezoneInfos.size() > 0) {
                            list_noresult.setVisibility(View.GONE);
                            mWezoneMainListAdapter.notifyDataSetChanged();
                        } else {
                            list_noresult.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (mWezoneInfos.size() > 0) {
                        list_noresult.setVisibility(View.GONE);
                        mWezoneMainListAdapter.notifyDataSetChanged();
                    } else {
                        list_noresult.setVisibility(View.VISIBLE);
                    }
                }
                for (int i = 0; i < mWezoneInfos.size(); i++) {
                    int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE, mWezoneInfos.get(i).wezone_id);
                    if (idx != -1) {
                        if ("G".equals(mWezoneInfos.get(i).location_type)) {
                            if (Define.TYPE_INVITED.equals(mWezoneInfos.get(i).manage_type)) {
                                //위치인식방법이 B 비콘에서 G GPS로 변경했을 때 manage_type = W 인 위존을 내 위존 리스트에서 삭제
                                mWezoneInfos.remove(i);
                            }
                        }
                    }
                }
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Rev_WezoneList> call, Throwable t) {
                if (mWezoneInfos.size() > 0) {
                    list_noresult.setVisibility(View.GONE);
                    mWezoneMainListAdapter.notifyDataSetChanged();
                } else {
                    list_noresult.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void getWezoneSearchListNOTGPS(boolean isProgress, final int offset, int limit, String search_type, final String keyword) {

        if (isProgress) {
            mActivity.showProgressPopup();
        }

        Call<Rev_WezoneList> wezoneListCall = mActivity.wezoneRestful.getWezoneSearchList(String.valueOf(offset), String.valueOf(limit), search_type, keyword);
        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
                                   @Override
                                   public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                                       Rev_WezoneList resultData = response.body();
                                       if (mActivity.isNetSuccess(resultData)) {
                                           if (resultData.list != null) {
//                        mWezoneInfos.clear();
                                               mListViewLocked = true;
                                               mTotalCount = Integer.valueOf(resultData.total_count);
                                               for (Data_WeZone wezone : resultData.list) {
                                                   wezone.headerId = Data_WeZone.HEADER_NEARZONE;
                                                   mWezoneInfos.add(wezone);
                                               }

                                               if (mWezoneInfos.size() > 0) {
                                                   list_noresult.setVisibility(View.GONE);
                                                   mWezoneMainListAdapter.notifyDataSetChanged();
                                               } else {
                                                   list_noresult.setVisibility(View.VISIBLE);
                                               }
                                           }
                                       } else {
                                           if (mWezoneInfos.size() > 0) {
                                               list_noresult.setVisibility(View.GONE);
                                               mWezoneMainListAdapter.notifyDataSetChanged();
                                           } else {
                                               list_noresult.setVisibility(View.VISIBLE);
                                           }
                                       }
                                       mWezoneMainListAdapter.notifyDataSetChanged();
                                       mActivity.hidePorgressPopup();
                                   }

                                   @Override
                                   public void onFailure(Call<Rev_WezoneList> call, Throwable t) {
                                       if (mWezoneInfos.size() > 0) {
                                           list_noresult.setVisibility(View.GONE);
                                           mWezoneMainListAdapter.notifyDataSetChanged();
                                       } else {
                                           list_noresult.setVisibility(View.VISIBLE);
                                       }
                                       mActivity.hidePorgressPopup();
                                   }
                               }
        );
    }

    public void getWezoneSearchList(boolean isProgress, String longitude, String latitude, final int offset, int limit, String search_type, final String keyword) {

        if (isProgress) {
            mActivity.showProgressPopup();
        }

        Call<Rev_WezoneList> wezoneListCall = mActivity.wezoneRestful.getWezoneSearchList(longitude, latitude, String.valueOf(offset), String.valueOf(limit), search_type, keyword);
        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
                                   @Override
                                   public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                                       Rev_WezoneList resultData = response.body();
                                       if (mActivity.isNetSuccess(resultData)) {
                                           if (resultData.list != null) {
                                               mListViewLocked = true;
                                               mTotalCount = Integer.valueOf(resultData.total_count);
                                               for (Data_WeZone wezone : resultData.list) {
                                                   wezone.headerId = Data_WeZone.HEADER_NEARZONE;
                                                   mWezoneInfos.add(wezone);
                                               }

                                               if (mWezoneInfos.size() > 0) {
                                                   list_noresult.setVisibility(View.GONE);
                                                   mWezoneMainListAdapter.notifyDataSetChanged();
                                               } else {
                                                   list_noresult.setVisibility(View.VISIBLE);
                                               }
                                           }
                                       } else {
                                           if (mWezoneInfos.size() > 0) {
                                               list_noresult.setVisibility(View.GONE);
//                                               mWezoneMainListAdapter.notifyDataSetChanged();
                                           } else {
                                               list_noresult.setVisibility(View.VISIBLE);
                                           }
                                       }
                                       mWezoneMainListAdapter.notifyDataSetChanged();
                                       mActivity.hidePorgressPopup();

                                   }

                                   @Override
                                   public void onFailure(Call<Rev_WezoneList> call, Throwable t) {
                                       if (mWezoneInfos.size() > 0) {
                                           list_noresult.setVisibility(View.GONE);
                                           mWezoneMainListAdapter.notifyDataSetChanged();
                                       } else {
                                           list_noresult.setVisibility(View.VISIBLE);
                                       }
                                       mActivity.hidePorgressPopup();
                                   }
                               }
        );
    }

    @Override
    public void onPause() {

        mGpsInfo = new GpsInfo(getContext());
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            longitude = mGpsInfo.getLongitude();
            latitude = mGpsInfo.getLatitude();

            editor.putString(LONGITUDE, String.valueOf(longitude));
            editor.putString(LATITUDE, String.valueOf(latitude));
            editor.commit();
//            ImageView_wezone_gps_check.setVisibility(View.GONE);
            mWezoneMainListAdapter.notifyDataSetChanged();
        } else {
            // GPS 를 사용할수 없으므로
//            mGpsInfo.showSettingsAlert();
//            ImageView_wezone_gps_check.setVisibility(View.VISIBLE);
            mWezoneMainListAdapter.notifyDataSetChanged();
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wezone_fragment, null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Data_WeZone info = mWezoneInfos.get(position);
        for(int j=0; j<mMyWezone_TotalCount; j++){
            if(info.zone_possible == null) {
                for(int i=0; i<mWezoneInfos.size(); i++) {
                    int neadrIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE, mWezoneInfos.get(i).wezone_id);
                    if (neadrIdx != -1) {
                        info.zone_possible = mWezoneInfos.get(neadrIdx).zone_possible;
                        if("G".equals(mWezoneInfos.get(neadrIdx).location_type)){
                            if(Define.TYPE_INVITED.equals(mWezoneInfos.get(neadrIdx).manage_type)){
                                //위치인식방법이 B 비콘에서 G GPS로 변경했을 때 manage_type을 W -> null로 변경
                                mWezoneInfos.get(neadrIdx).manage_type = null;
//                                info.manage_type =  mWezoneInfos.get(neadrIdx).manage_type;
                            }
                        }

                    }
                }
                mWezoneMainListAdapter.notifyDataSetChanged();
            }
        }
        WezoneActivity.startActivit(mActivity, info, null);
    }

    public void editWezoneData(Data_WeZone weZone, String mput_tpye) {
        // 수정,탈퇴,입장,공지 시 오는 데이터

        if (PUT.equals(mput_tpye)) {
            //수정
            //마이존 데이터 찾아서 추가한다
            int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE,weZone.wezone_id);
            if (idx != -1) {
                Data_WeZone myweZone = new Data_WeZone();
                mWezoneInfos.remove(idx);
                //마이존 헤더 명시
                myweZone = paste_data(myweZone,weZone,Data_WeZone.HEADER_MYZONE);
                mWezoneInfos.add(idx, myweZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

            int neadrIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE,weZone.wezone_id);
            if (neadrIdx != -1) {
                Data_WeZone nearWeZone = new Data_WeZone();
                nearWeZone = paste_data(nearWeZone,weZone,mWezoneInfos.get(neadrIdx).headerId);
                mWezoneInfos.remove(neadrIdx);
                //주변존 헤더 명시
                mWezoneInfos.add(neadrIdx, nearWeZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

        } else if (POST.equals(mput_tpye)) {
            //등록
            gps(0);
        } else if (DELETE.equals(mput_tpye)) { //탈퇴
            //마이존 탈퇴
            int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE,weZone.wezone_id);
            if (idx != -1) {
                mWezoneInfos.remove(idx);
                weZone.headerId = Data_WeZone.HEADER_MYZONE;
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

            //주변존 탈퇴
            int neadrIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE,weZone.wezone_id);
            if (neadrIdx != -1) {
                weZone.zone_possible = mWezoneInfos.get(neadrIdx).zone_possible;
                mWezoneInfos.remove(neadrIdx);
                weZone.headerId = Data_WeZone.HEADER_NEARZONE;
                mWezoneInfos.add(neadrIdx, weZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

        } else if (ENTRY.equals(mput_tpye)) {
            //입장
            //마이존 데이터 찾아서 추가한다
            int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE,weZone.wezone_id);
            if (idx != -1) {
                Data_WeZone myweZone = new Data_WeZone();
                mWezoneInfos.remove(idx);
                //마이존 헤더 명시
                myweZone = paste_data(myweZone,weZone,Data_WeZone.HEADER_MYZONE);
                mWezoneInfos.add(idx, myweZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }else {

                Data_WeZone myweZone = new Data_WeZone();
                //마이존 헤더 명시
                myweZone = paste_data(myweZone, weZone, Data_WeZone.HEADER_MYZONE);
                mWezoneInfos.add(0, myweZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }
            int neadrIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE,weZone.wezone_id);
            if (neadrIdx != -1) {
                Data_WeZone nearWeZone = new Data_WeZone();
                nearWeZone = paste_data(nearWeZone,weZone,mWezoneInfos.get(neadrIdx).headerId);
                mWezoneInfos.remove(neadrIdx);
                //주변존 헤더 명시
                mWezoneInfos.add(neadrIdx, nearWeZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

        } else if (ENTRY_WAIT.equals(mput_tpye)) {
            //입장
            //마이존 데이터 찾아서 추가한다
            Data_WeZone myweZone = new Data_WeZone();
            //마이존 헤더 명시
            myweZone = paste_data(myweZone,weZone,Data_WeZone.HEADER_MYZONE);
            mWezoneInfos.add(0, myweZone);
            mWezoneMainListAdapter.notifyDataSetChanged();

            int neadrIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE,weZone.wezone_id);
            if (neadrIdx != -1) {
                Data_WeZone nearWeZone = new Data_WeZone();
                nearWeZone = paste_data(nearWeZone,weZone,mWezoneInfos.get(neadrIdx).headerId);
                mWezoneInfos.remove(neadrIdx);
                //주변존 헤더 명시
                mWezoneInfos.add(neadrIdx, nearWeZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }
        } else if (REMOVE.equals(mput_tpye)) { //삭제
            int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE,weZone.wezone_id);
            if (idx != -1) {
                mWezoneInfos.remove(idx);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

            int nearIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE,weZone.wezone_id);
            if (nearIdx != -1) {
                mWezoneInfos.remove(nearIdx);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

        } else if (NOTICE.equals(mput_tpye)) {
            //공지지
           //마이존 데이터 찾아서 추가한다
            int idx = getWezoneIdx(Data_WeZone.HEADER_MYZONE, weZone.wezone_id);
            if (idx != -1) {
                Data_WeZone myweZone = new Data_WeZone();
                mWezoneInfos.remove(idx);
                //마이존 헤더 명시
                myweZone = paste_data(myweZone, weZone, mWezoneInfos.get(idx).headerId);
                mWezoneInfos.add(idx, myweZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }

            int neadrIdx = getWezoneIdx(Data_WeZone.HEADER_NEARZONE, weZone.wezone_id);
            if (neadrIdx != -1) {
                Data_WeZone nearWeZone = new Data_WeZone();
                nearWeZone = paste_data(nearWeZone, weZone, mWezoneInfos.get(neadrIdx).headerId);
                mWezoneInfos.remove(neadrIdx);
                //주변존 헤더 명시
                mWezoneInfos.add(neadrIdx, nearWeZone);
                mWezoneMainListAdapter.notifyDataSetChanged();
            }
        }

    }

    public int getWezoneIdx(int headerId, String wezoneId) {
        int idx = -1;
        for (int i = 0; i < mWezoneInfos.size(); i++) {
            if (headerId == mWezoneInfos.get(i).headerId && wezoneId.equals(mWezoneInfos.get(i).wezone_id)) {
                idx = i;
            }
        }
        return idx;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {

                mHasRequestedMore = true;
            }
        }
        lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
        //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag && mListViewLocked) {
            //TODO 화면이 바닦에 닿을때 처리
            if (mWezoneInfos.size() < mTotalCount) {
                mOffset = mWezoneInfos.size();

                direction = Direction.DOWN;

                //내 주변 위존 더보기
                gps(1);
                down_value = 1;

                mListViewLocked = false;
            }else{
                mListViewLocked = false;
            }
        }
    }

    public Data_WeZone paste_data(Data_WeZone data_weZone, Data_WeZone weZone,int header_id){
        data_weZone.location_data = weZone.location_data;
        data_weZone.location_type = weZone.location_type;
        data_weZone.push_flag = weZone.push_flag;
        data_weZone.address = weZone.address;
        data_weZone.beacons = weZone.beacons;
        data_weZone.bg_img_url = weZone.bg_img_url;
        data_weZone.board_count = weZone.board_count;
        data_weZone.create_datetime = weZone.create_datetime;
        data_weZone.delete_datetime = weZone.delete_datetime;
        data_weZone.distance = weZone.distance;
        data_weZone.delete_flag = weZone.delete_flag;
        data_weZone.hashtag = weZone.hashtag;
        data_weZone.id = weZone.id;
        data_weZone.img_url = weZone.img_url;
        data_weZone.introduction = weZone.introduction;
        data_weZone.title = weZone.title;
        data_weZone.latitude = weZone.latitude;
        data_weZone.longitude = weZone.longitude;
        data_weZone.join_datetime = weZone.join_datetime;
        data_weZone.manage_type = weZone.manage_type;
        data_weZone.member_count = weZone.member_count;
        data_weZone.member_limit = weZone.member_limit;
        data_weZone.members = weZone.members;
        data_weZone.short_url = weZone.short_url;
        data_weZone.uuid = weZone.uuid;
        data_weZone.update_datetime = weZone.update_datetime;
        data_weZone.wait_datetime = weZone.wait_datetime;
        data_weZone.wezone_type = weZone.wezone_type;
        data_weZone.wezone_id = weZone.wezone_id;
        data_weZone.zone_id = weZone.zone_id;
        data_weZone.zone_in = weZone.zone_in;
        data_weZone.zone_out = weZone.zone_out;
        data_weZone.zone_possible = weZone.zone_possible;
        data_weZone.headerId = header_id;

        return data_weZone;
    }

}
