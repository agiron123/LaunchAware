package slappahoe.kappa.launcherhelloworld;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AppsListActivity extends Activity {

    private ImageView imageView;
    private View homeView;
    private String DEBUG_TAG = "AppsListActivity";
    private LocationManager locationManager;
    private Location currentLocation;
    private LocationListener locationListener;

    //http://stackoverflow.com/questions/12692870/filter-out-non-launchable-apps-when-getting-all-installed-apps
    public static List<ApplicationInfo> getAllInstalledApplications(Context context) {
        List<ApplicationInfo> installedApps = context.getPackageManager().getInstalledApplications(PackageManager.PERMISSION_GRANTED);
        List<ApplicationInfo> launchableInstalledApps = new ArrayList<ApplicationInfo>();
        for(int i =0; i<installedApps.size(); i++){
            if(context.getPackageManager().getLaunchIntentForPackage(installedApps.get(i).packageName) != null){
                //If you're here, then this is a launch-able app
                launchableInstalledApps.add(installedApps.get(i));

                Log.d("AppsListActivity", installedApps.get(i).packageName);

            }
        }
        return launchableInstalledApps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Drawable wallpaper = wallpaperManager.peekDrawable();
        wallpaper.setAlpha(128);
        getWindow().setBackgroundDrawable(wallpaper);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        final String TAG = "AppsListActivity";
        final PackageManager packageManager = getPackageManager();

        final List<ApplicationInfo> installedApps = getAllInstalledApplications(getApplicationContext());

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setVerticalScrollBarEnabled(false);
        gridview.setAdapter(new GridAdapter(this, installedApps));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                getLocationUpdate();
                Date now = new Date();
                Log.d("AppsListActivity", "Now: " + now.toString());
                String ssid = getCurrentSsid(getApplicationContext());
                if (ssid != null) {
                    ssid = ssid.replace("\"", "");
                } else {
                    ssid = "null";
                }

                Log.d("AppsListActivity", "Ssid: " + ssid);
                String packageName = installedApps.get(position).packageName;
                AppInfo info;
                if (currentLocation != null) {
                    info = new AppInfo(packageName,currentLocation.getLatitude(), currentLocation.getLongitude(), ssid);
                } else {
                    info = new AppInfo(packageName);
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String infoPrettyPrint = gson.toJson(info);
                Log.d("AppsListActivity", "AppInfo: " + infoPrettyPrint);

                //TODO: Close database connection based on application lifecycle.
                //Insert the app launch information into database:
                AppInfoDbHelper appInfoDbHelper = new AppInfoDbHelper(getApplicationContext());
                SQLiteDatabase db = appInfoDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                try {
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_PACKAGE_NAME, info.getPackageName());
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_NAME, info.getName());
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_WIFI_SSID, info.getWifiSSId());
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_BLUETOOTH, info.getBluetoothNetwork());
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_LATITUDE, currentLocation.getLatitude());
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_LONGITUDE, currentLocation.getLongitude());
                    values.put(AppInfoContract.AppInfoEntry.COL_NAME_LAUNCH_TIME, new Date().getTime());

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(
                            AppInfoContract.AppInfoEntry.TABLE_NAME,
                            null,
                            values);
                }
                catch (Exception e) {
                    Log.d("Exception: ", e.getMessage());
                }

                //Read back out from the database:
                AppInfoDbHelper dbReaderHelper = new AppInfoDbHelper(getApplicationContext());
                SQLiteDatabase dbReadable = appInfoDbHelper.getReadableDatabase();

                String [] columns = {
                        AppInfoContract.AppInfoEntry.COL_NAME_PACKAGE_NAME,
                        AppInfoContract.AppInfoEntry.COL_NAME_NAME,
                        AppInfoContract.AppInfoEntry.COL_NAME_WIFI_SSID,
                        AppInfoContract.AppInfoEntry.COL_NAME_BLUETOOTH,
                        AppInfoContract.AppInfoEntry.COL_NAME_LATITUDE,
                        AppInfoContract.AppInfoEntry.COL_NAME_LONGITUDE,
                        AppInfoContract.AppInfoEntry.COL_NAME_LAUNCH_TIME
                };

                try {
                    Cursor c = db.query(
                            AppInfoContract.AppInfoEntry.TABLE_NAME,  // The table to query
                            columns,                               // The columns to return
                            null,                                // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            null                                 // The sort order
                    );
                    if (c != null){
                        //Cursors are lazy loaded, get the first item in the cursor.
                        c.moveToFirst();
                        //Reading out all entries from the database.
                        while (!c.isAfterLast()){
                            String appPackageName = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_PACKAGE_NAME));
                            Log.d("AppsListActivity", "DB Read: PackageName: " +  appPackageName);
                            String appName = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_NAME));
                            Log.d("AppsListActivity", "DB Read: App Name: " +  appName);
                            String wifiSSID = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_WIFI_SSID));
                            Log.d("AppsListActivity", "DB Read: SSID: " + wifiSSID);
                            String bluetoothNetwork = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_BLUETOOTH));
                            Log.d("AppsListActivity", "DB Read: Bluetooth: " + bluetoothNetwork);
                            String appLatitude = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_LATITUDE));
                            Log.d("AppsListActivity", "DB Read: Latitude " + appLatitude);
                            String appLongitude = c.getString(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_LONGITUDE));
                            Log.d("AppsListActivity", "DB Read: Longitude: " + appLongitude);

                            long launchTime = c.getLong(c.getColumnIndex(AppInfoContract.AppInfoEntry.COL_NAME_LAUNCH_TIME));
                            Date launchDateTime = new Date(launchTime);
                            Log.d("AppsListActivity", "DB Read: Launch Time: " + launchDateTime.toString());

                            c.moveToNext();
                        }
                    }
                }
                catch(Exception e) {
                    Log.d("Exception: ",  e.getMessage().toString());
                }

                startActivity(packageManager.getLaunchIntentForPackage(packageName));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_apps_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getLocationUpdate() {
        //TODO: Get location from best provider (network or GPS)
        //TODO: Consider polling every x minutes to save battery.
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    //Source: http://stackoverflow.com/questions/8811315/how-to-get-current-wifi-connection-info-in-android
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }
}
