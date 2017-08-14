package com.vinetech.wezone.Wezone;

/**
 * Created by galuster on 2017-03-06.
 */

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * ���ø����̼� ���� Ŭ����
 * @author nohhs
 */
public class AppInfo {
    public static interface AppFilter {
        public void init();
        public boolean filterApp(ApplicationInfo info);
    }

    // ������
    public Drawable mIcon = null;
    // ���ø����̼� �̸�
    public String mAppNaem = null;
    // ��Ű�� ��
    public String mAppPackge = null;

    /**
     * ������Ƽ ����
     */
    public static final AppFilter THIRD_PARTY_FILTER = new AppFilter() {
        public void init() {
        }

        @Override
        public boolean filterApp(ApplicationInfo info) {
            if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return true;
            } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return true;
            }
            return false;
        }
    };

    /**
     * ���ĺ� �̸����� ����
     */
    public static final Comparator<AppInfo> ALPHA_COMPARATOR = new Comparator<AppInfo>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(AppInfo object1, AppInfo object2) {
            return sCollator.compare(object1.mAppNaem, object2.mAppNaem);
        }
    };
}
