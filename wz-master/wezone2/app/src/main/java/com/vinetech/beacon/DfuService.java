package com.vinetech.beacon;

import android.app.Activity;

import com.vinetech.wezone.Beacon.BeaconDFUActivity;

import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * Created by ShinSuMin on 2017-05-22.
 *
 * 비콘 펌웨어 담당 서비스
 *
 * nRF 라이브러리 참고하여 구성
 *
 */

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return BeaconDFUActivity.class;
    }

    @Override
    protected boolean isDebug() {
        return true;
    }
}
