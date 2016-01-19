package slappahoe.kappa.launcherhelloworld;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import slappahoe.kappa.launcherhelloworld.models.AppInfo;


public class AppsListActivity extends Activity {

    private ImageView imageView;
    private View homeView;
    private String DEBUG_TAG = "AppsListActivity";
    private LocationManager locationManager;
    private Location currentLocation;
    private LocationListener locationListener;

    private static final int LOCATION_PERMISSION = 1;

    //http://stackoverflow.com/questions/12692870/filter-out-non-launchable-apps-when-getting-all-installed-apps
    public static List<ApplicationInfo> getAllInstalledApplications(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApps =
                pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);
        List<ApplicationInfo> launchableInstalledApps = new ArrayList<ApplicationInfo>();
        for (int i = 0; i < installedApps.size(); i++) {
            if (pm.getLaunchIntentForPackage(installedApps.get(i).packageName) != null) {
                //If you're here, then this is a launch-able app
                launchableInstalledApps.add(installedApps.get(i));

                Log.d("AppsListActivity", installedApps.get(i).packageName);

            }
        }

        Collections.sort(launchableInstalledApps, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                return pm.getApplicationLabel(lhs).toString()
                        .compareToIgnoreCase(pm.getApplicationLabel(rhs).toString());
            }
        });
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION);
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        final String TAG = "AppsListActivity";
        final PackageManager packageManager = getPackageManager();

        final List<ApplicationInfo> installedApps = getAllInstalledApplications(getApplicationContext());

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setVerticalScrollBarEnabled(false);
        gridview.setAdapter(new GridAdapter(this, installedApps));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "Clicked: " + position, Toast.LENGTH_SHORT);

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
                    info = new AppInfo(packageName, currentLocation.getLatitude(), currentLocation.getLongitude(), ssid);
                } else {
                    info = new AppInfo(packageName);
                }
                info = new AppInfo(packageName);


                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String infoPrettyPrint = gson.toJson(info);
                Log.d("AppsListActivity", "AppInfo: " + infoPrettyPrint);

                startActivity(packageManager.getLaunchIntentForPackage(packageName));
            }
        });
    }

    public void getLocationUpdate() {
        //TODO: Get location from best provider (network or GPS)
        //Might also want to do something like poll location every 15 minutes or so, so as not to be
        //so harsh on the battery.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        if (currentLocation != null) {
            Toast.makeText(getApplicationContext(), "lat: " + currentLocation.getLatitude() +
                    " lon: " + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }
    }

    //copied from: http://stackoverflow.com/questions/8811315/how-to-get-current-wifi-connection-info-in-android
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
