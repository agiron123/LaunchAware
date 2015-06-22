package slappahoe.kappa.launcherhelloworld;

import java.util.ArrayList;

/**
 * Created by andre on 6/21/15.
 */
public class AppInfo {
    private String packageName;
    private String name;

    private int launches;
    private ArrayList<String> categories;

    public AppInfo(String name, String packageName, int launches)
    {
        this.name = name;
        this.packageName = packageName;
        this.launches = launches;
    }

    public AppInfo(String name)
    {
        this.name = name;
        this.launches = 0;
        this.packageName = "";
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public int getLaunches() {
        return launches;
    }

    public void setLaunches(int launches) {
        this.launches = launches;
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
}

