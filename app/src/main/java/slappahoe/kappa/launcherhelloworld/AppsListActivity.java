package slappahoe.kappa.launcherhelloworld;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

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
            }
        }
        return launchableInstalledApps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

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
                    info = new AppInfo(packageName,currentLocation.getLatitude(), currentLocation.getLongitude(), ssid);
                } else {
                    info = new AppInfo(packageName);
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String infoPrettyPrint = gson.toJson(info);
                Log.d("AppsListActivity", "AppInfo: " + infoPrettyPrint);

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
        //Might also want to do something like poll location every 15 minutes or so, so as not to be
        //so harsh on the battery.
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
