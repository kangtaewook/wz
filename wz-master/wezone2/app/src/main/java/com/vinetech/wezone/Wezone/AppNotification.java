package com.vinetech.wezone.Wezone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppNotification extends BaseActivity {

    public static final String NOTIFICATION_APP = "APP";
    public static final String NOTIFICATION_APP_NAME = "APP_NAME";

    private String name;
    private String pName;
    private Drawable iconDrawable;

    private final int MENU_DOWNLOAD = 0;
    private final int MENU_ALL = 1;
    private int MENU_MODE = MENU_DOWNLOAD;

    private PackageManager pm;

    private LinearLayout mLoadingContainer;
    private ListView mListView = null;
    private IAAdapter mAdapter = null;


    public static void startActivity(BaseActivity activity, String title) {
        Intent intent = new Intent(activity, AppNotification.class);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_APP);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_notification);

        String titleW = getResources().getString(R.string.App_start);
        setHeaderView(R.drawable.btn_back_white, titleW, 0);

        mLoadingContainer = (LinearLayout) findViewById(R.id.loading_container);
        mListView = (ListView) findViewById(R.id.activity_app_notification);

        mAdapter = new IAAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub
                String app_name = ((TextView) view.findViewById(R.id.app_name)).getText().toString();
                String package_name = ((TextView) view.findViewById(R.id.app_package)).getText().toString();
                Toast.makeText(AppNotification.this, package_name, Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.putExtra(NOTIFICATION_APP, package_name);
                i.putExtra(NOTIFICATION_APP_NAME, app_name);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    // 헤더 뒤로가기 버튼
    @Override
    public void onClickLeftBtn(View v) {
        super.onClickLeftBtn(v);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // �۾� ����
        startTask();
    }

    /**
     * �۾� ����
     */
    private void startTask() {
        new AppTask().execute();
    }

    /**
     * �ε��� ǥ�� ����
     *
     * @param isView
     *            ǥ�� ����
     */
    private void setLoadingView(boolean isView) {
        if (isView) {
            // ȭ�� �ε��� ǥ��
            mLoadingContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            // ȭ�� ���� ����Ʈ ǥ��
            mListView.setVisibility(View.VISIBLE);
            mLoadingContainer.setVisibility(View.GONE);
        }
    }

    /**
     * List Fast Holder
     *
     * @author nohhs
     */
    private class ViewHolder {
        // App Icon
        public ImageView mIcon;
        // App Name
        public TextView mName;
        // App Package Name
        public TextView mPacakge;
    }

    /**
     * List Adapter
     *
     * @author nohhs
     */
    private class IAAdapter extends BaseAdapter {
        private Context mContext = null;

        private List<ApplicationInfo> mAppList = null;
        private ArrayList<AppInfo> mListData = new ArrayList<AppInfo>();

        public IAAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_layout, null);

                holder.mIcon = (ImageView) convertView
                        .findViewById(R.id.app_icon);
                holder.mName = (TextView) convertView
                        .findViewById(R.id.app_name);
                holder.mPacakge = (TextView) convertView
                        .findViewById(R.id.app_package);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo data = mListData.get(position);

            if (data.mIcon != null) {
                holder.mIcon.setImageDrawable(data.mIcon);
            }

            holder.mName.setText(data.mAppNaem);
            holder.mPacakge.setText(data.mAppPackge);

            return convertView;
        }

        public void rebuild() {
            if (mAppList == null) {

                pm = AppNotification.this.getPackageManager();

                mAppList = pm
                        .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            }

            AppInfo.AppFilter filter;
            switch (MENU_MODE) {
                case MENU_DOWNLOAD:
                    filter = AppInfo.THIRD_PARTY_FILTER;
                    break;
                default:
                    filter = null;
                    break;
            }

            if (filter != null) {
                filter.init();
            }

            mListData.clear();

            AppInfo addInfo = null;
            ApplicationInfo info = null;
            for (ApplicationInfo app : mAppList) {
                info = app;

                if (filter == null || filter.filterApp(info)) {

                    addInfo = new AppInfo();
                    // App Icon
                    addInfo.mIcon = app.loadIcon(pm);
                    // App Name
                    addInfo.mAppNaem = app.loadLabel(pm).toString();
                    // App Package Name
                    addInfo.mAppPackge = app.packageName;
                    mListData.add(addInfo);
                }
            }

            Collections.sort(mListData, AppInfo.ALPHA_COMPARATOR);
        }
    }

    private class AppTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            setLoadingView(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mAdapter.rebuild();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // ����� ����
            mAdapter.notifyDataSetChanged();

            // �ε��� ����
            setLoadingView(false);
        }

    };



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (MENU_MODE == MENU_DOWNLOAD) {
            menu.findItem(MENU_DOWNLOAD).setVisible(false);
            menu.findItem(MENU_ALL).setVisible(true);
        } else {
            menu.findItem(MENU_DOWNLOAD).setVisible(true);
            menu.findItem(MENU_ALL).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        if (menuId == MENU_DOWNLOAD) {
            MENU_MODE = MENU_DOWNLOAD;
        } else {
            MENU_MODE = MENU_ALL;
        }

        startTask();

        return true;
    }


}
