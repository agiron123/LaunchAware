package slappahoe.kappa.launcherhelloworld.models;
import java.util.Date;


//TODO: Move to models package when code is refactored.
public class AppInfo {

    private String packageName;
    private String name;
    private String wifiSSId;
    private String bluetoothNetwork;

    //Private class for serialization in Gson.
    private LocationInfo locationInfo;

    //Time that the application was launched.
    private Date launchTime;

    public AppInfo(String packageName) {
        this.packageName = packageName;
        this.launchTime = new Date();
    }

    public AppInfo(String packageName, double latitude, double longitude, String wifiSSId){
        this(packageName);
        this.locationInfo = new LocationInfo(latitude, longitude);
        this.wifiSSId = wifiSSId;
    }

    public void setBluetoothNetwork(String bluetoothNetwork) {
        this.bluetoothNetwork = bluetoothNetwork;
    }

    public String getBluetoothNetwork() {
        return this.bluetoothNetwork;
    }

    public String getWifiSSId() {
        return this.wifiSSId;
    }

    public void setWifiSSId(String ssid) {
        this.wifiSSId = ssid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private class LocationInfo {
        private double latitude;
        private double longitude;

        public LocationInfo(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

