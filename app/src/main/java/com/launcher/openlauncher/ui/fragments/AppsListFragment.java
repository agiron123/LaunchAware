package com.launcher.openlauncher.ui.fragments;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.launcher.openlauncher.ui.adapters.GridAdapter;
import com.launcher.openlauncher.utils.PrefsKey;
import com.launcher.openlauncher.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppsListFragment extends Fragment implements TextWatcher,
        View.OnFocusChangeListener, View.OnKeyListener {

    @Bind(R.id.apps_list_view)
    ViewGroup mAppsListView;
    @Bind(R.id.search_edit_text)
    EditText mSearchEditText;
    @Bind(R.id.grid_view)
    GridView mGridView;

//    private static final String LOG_TAG = AppsListFragment.class.getSimpleName();
//    private LocationManager locationManager;
//    private Location currentLocation;
//    private LocationListener locationListener;

    private boolean isSearching;

//    private static final int LOCATION_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(PrefsKey.SEARCHING_IN_GRID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isSearching) {
            mSearchEditText.requestFocus();
        }
        mSearchEditText.addTextChangedListener(this);
        mSearchEditText.setOnFocusChangeListener(this);
        mSearchEditText.setOnKeyListener(this);

        final Drawable wallpaperDrawable = WallpaperManager.getInstance(getContext()).getDrawable();
        mAppsListView.setBackground(wallpaperDrawable);

//        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(
//                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_PERMISSION);
//            return;
//        }
//        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                currentLocation = location;
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//            }
//        };

        reorderApps("");
    }

//    public void getLocationUpdate() {
//        //TODO: Get location from best provider (network or GPS)
//        //Might also want to do something like poll location every 15 minutes or so, so as not to be
//        //so harsh on the battery.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
//            return;
//        }
//        //TODO: Consider polling every x minutes to save battery.
//        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
//    }

    //Source: http://stackoverflow.com/questions/8811315/how-to-get-current-wifi-connection-info-in-android
//    public static String getCurrentSsid(Context context) {
//        String ssid = null;
//        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
//        if (networkInfo.isConnected()) {
//            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
//            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
//                ssid = connectionInfo.getSSID();
//            }
//        }
//        return ssid;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        reorderApps(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            hideKeyboard(v);
            return true;
        }
        return false;
    }

    //http://stackoverflow.com/questions/12692870/filter-out-non-launchable-apps-when-getting-all-installed-apps
    public static List<ApplicationInfo> getAllInstalledApplications(Context context,
                                                                    final String searchText) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApps =
                pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);

        List<ApplicationInfo> launchableInstalledApps = new ArrayList<>();
        for (int i = 0; i < installedApps.size(); i++) {
            if (pm.getLaunchIntentForPackage(installedApps.get(i).packageName) != null) {
                launchableInstalledApps.add(installedApps.get(i));
            }
        }

        if (StringUtils.isEmpty(searchText)) {
            Collections.sort(launchableInstalledApps, new Comparator<ApplicationInfo>() {
                @Override
                public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                    return pm.getApplicationLabel(lhs).toString()
                            .compareToIgnoreCase(pm.getApplicationLabel(rhs).toString());
                }
            });
        } else {
            Collections.sort(launchableInstalledApps, new Comparator<ApplicationInfo>() {
                @Override
                public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                    String leftString = pm.getApplicationLabel(lhs).toString();
                    String rightString = pm.getApplicationLabel(rhs).toString();

                    if (StringUtils.containsIgnoreCase(leftString, searchText) &&
                            StringUtils.containsIgnoreCase(rightString, searchText))
                        return leftString.compareToIgnoreCase(rightString);
                    else if (StringUtils.containsIgnoreCase(leftString, searchText) &&
                            !StringUtils.containsIgnoreCase(rightString, searchText))
                        return -1;
                    else if (!StringUtils.containsIgnoreCase(leftString, searchText) &&
                            StringUtils.containsIgnoreCase(rightString, searchText))
                        return 1;
                    else
                        return leftString.compareToIgnoreCase(rightString);
                }
            });
            // Hide unmatched apps
            List<ApplicationInfo> appsToRemove = new ArrayList<>();
            for (ApplicationInfo applicationInfo : launchableInstalledApps) {
                if (!StringUtils.containsIgnoreCase(pm.getApplicationLabel(applicationInfo).toString(), searchText))
                    appsToRemove.add(applicationInfo);
            }
            launchableInstalledApps.removeAll(appsToRemove);
        }
        return launchableInstalledApps;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void reorderApps(String searchText) {
        final PackageManager packageManager = getContext().getPackageManager();
        final List<ApplicationInfo> installedApps =
                getAllInstalledApplications(getContext(), searchText);

        mGridView.setVerticalScrollBarEnabled(false);
        mGridView.setAdapter(new GridAdapter(getContext(), installedApps));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                  String packageName = installedApps.get(position).packageName;

//                getLocationUpdate();
//                Date now = new Date();
//                Log.d(LOG_TAG, "Now: " + now.toString());
//                String ssid = getCurrentSsid(getApplicationContext());
//                if (ssid != null) {
//                    ssid = ssid.replace("\"", "");
//                } else {
//                    ssid = "null";
//                }
//
//                Log.d(LOG_TAG, "Ssid: " + ssid);
//                AppInfo info;
//                if (currentLocation != null) {
//                    info = new AppInfo(packageName, currentLocation.getLatitude(), currentLocation.getLongitude(), ssid);
//                } else {
//                    info = new AppInfo(packageName);
//                }
//                info = new AppInfo(packageName);
//
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                String infoPrettyPrint = gson.toJson(info);
//                Log.d(LOG_TAG, "AppInfo: " + infoPrettyPrint);
//
//                //TODO: Close database connection based on application lifecycle.
//                //Insert the app launch information into database:
//                AppInfoDbHelper appInfoDbHelper = new AppInfoDbHelper(getApplicationContext());
//                SQLiteDatabase db = appInfoDbHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();
//
//                try {
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_PACKAGE_NAME, info.getPackageName());
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_NAME, info.getName());
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_WIFI_SSID, info.getWifiSSId());
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_BLUETOOTH, info.getBluetoothNetwork());
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_LATITUDE, currentLocation.getLatitude());
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_LONGITUDE, currentLocation.getLongitude());
//                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_LAUNCH_TIME, new Date().getTime());
//
//                    // Insert the new row, returning the primary key value of the new row
//                    long newRowId;
//                    newRowId = db.insert(
//                            AppInfoContract.AppInfoEntry.TABLE_NAME,
//                            null,
//                            values);
//                } catch (Exception e) {
//                    Log.d("Exception: ", e.getMessage());
//                }
//
//                //Read back out from the database:
//                AppInfoDbHelper dbReaderHelper = new AppInfoDbHelper(getApplicationContext());
//                SQLiteDatabase dbReadable = appInfoDbHelper.getReadableDatabase();
//
//                String[] columns = {
//                        AppInfoContract.AppInfoEntry.COL_NAME_PACKAGE_NAME,
//                        AppInfoContract.AppInfoEntry.COL_NAME_NAME,
//                        AppInfoContract.AppInfoEntry.COL_NAME_WIFI_SSID,
//                        AppInfoContract.AppInfoEntry.COL_NAME_BLUETOOTH,
//                        AppInfoContract.AppInfoEntry.COL_NAME_LATITUDE,
//                        AppInfoContract.AppInfoEntry.COL_NAME_LONGITUDE,
//                        AppInfoContract.AppInfoEntry.COL_NAME_LAUNCH_TIME
//                };
//
//                try {
//                    Cursor c = db.query(
//                            AppInfoContract.AppInfoEntry.TABLE_NAME,  // The table to query
//                            columns,                               // The columns to return
//                            null,                                // The columns for the WHERE clause
//                            null,                            // The values for the WHERE clause
//                            null,                                     // don't group the rows
//                            null,                                     // don't filter by row groups
//                            null                                 // The sort order
//                    );
//                    if (c != null) {
//                        //Cursors are lazy loaded, get the first item in the cursor.
//                        c.moveToFirst();
//                        //Reading out all entries from the database.
//                        while (!c.isAfterLast()) {
//                            String appPackageName = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_PACKAGE_NAME));
//                            Log.d(LOG_TAG, "DB Read: PackageName: " + appPackageName);
//                            String appName = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_NAME));
//                            Log.d(LOG_TAG, "DB Read: App Name: " + appName);
//                            String wifiSSID = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_WIFI_SSID));
//                            Log.d(LOG_TAG, "DB Read: SSID: " + wifiSSID);
//                            String bluetoothNetwork = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_BLUETOOTH));
//                            Log.d(LOG_TAG, "DB Read: Bluetooth: " + bluetoothNetwork);
//                            String appLatitude = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_LATITUDE));
//                            Log.d(LOG_TAG, "DB Read: Latitude " + appLatitude);
//                            String appLongitude = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_LONGITUDE));
//                            Log.d(LOG_TAG, "DB Read: Longitude: " + appLongitude);
//
//                            long launchTime = c.getLong(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_LAUNCH_TIME));
//                            Date launchDateTime = new Date(launchTime);
//                            Log.d(LOG_TAG, "DB Read: Launch Time: " + launchDateTime.toString());
//
//                            c.moveToNext();
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d("Exception: ", e.getMessage().toString());
//                }

                startActivity(packageManager.getLaunchIntentForPackage(packageName));
            }
        });
    }
}
