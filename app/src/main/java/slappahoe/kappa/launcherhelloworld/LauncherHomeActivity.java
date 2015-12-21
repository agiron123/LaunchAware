package slappahoe.kappa.launcherhelloworld;

import android.app.Activity;
import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LauncherHomeActivity extends Activity {
    public static final String PREFS_NAME = "LaunchAwarePrefs";
    private final String DEBUG_TAG = "LauncherHomeActiviy";
    private float xInitial = 0;
    private float yInitial = 0;

    private String[] favorites = new String[4];

    //Y distance threshold for swipe down gesture to be recognized.
    private final float swipeThreshold = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //TODO: Get the current wallpaper to show in the background.
        /*
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Drawable wallpaper = wallpaperManager.peekDrawable();

        Toast.makeText(getApplicationContext(), "LauncherHomeActivity onCreate", Toast.LENGTH_LONG);

        if (wallpaper != null) {
            Toast.makeText(getApplicationContext(), "Wallpaper NOT NULL", Toast.LENGTH_SHORT);
            RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.home_view);
            rLayout.setBackground(wallpaper);
        } else {
            Toast.makeText(getApplicationContext(), "Wallpaper NULL", Toast.LENGTH_SHORT);
        }
        */


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (!settings.contains("favorite0")) {
            favorites[0] = "com.google.android.dialer";
        }

        if (!settings.contains("favorite1")) {
            favorites[1] = "com.google.android.apps.messaging";
        }

        if (!settings.contains("favorite2")) {
            favorites[2] = "com.google.android.gm";
        }

        if (!settings.contains("favorite3")) {
            favorites[3] = "com.android.chrome";
        }

        //Requires a change in the minimum sdk
        setContentView(R.layout.activity_launcher_home);
        final List<ApplicationInfo> favorites = getFavoriteApps(getApplicationContext());

        GridView gridView = (GridView) findViewById(R.id.favoritesView);

        gridView.setVerticalScrollBarEnabled(false);
        gridView.setAdapter(new GridAdapter(this, favorites));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "Clicked: " + position, Toast.LENGTH_SHORT);

                String packageName = favorites.get(position).packageName;
                startActivity(getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName));
            }
        });

        final Button appMenuButton = (Button) findViewById(R.id.appMenuButton);
        appMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                showApps(v);
            }
        });

    }

    //Load Favorite applications. For now, just show the first four apps in the package manager list.
    public List<ApplicationInfo> getFavoriteApps(Context context) {
        ArrayList<ApplicationInfo> favoritesList = new ArrayList<>();
        try {
            for (int i = 0; i < favorites.length; i++) {
                favoritesList.add(context.getPackageManager().getApplicationInfo(favorites[i], 0));
            }
        } catch (Exception e) {
            Log.d("LauncherHomeActivity", e.getStackTrace().toString());
        }

        return favoritesList;
    }

    public void showApps(View view) {
        Intent i = new Intent(this, AppsListActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();

        switch (action) {
            //User just tapped on the screen
            case (MotionEvent.ACTION_DOWN): {
                xInitial = event.getX();
                yInitial = event.getY();

                Log.d(DEBUG_TAG, "Down, XInit: " + xInitial + " YInit: " + yInitial);
                return true;
            }

            //User moved finger
            case (MotionEvent.ACTION_MOVE): {

                return true;
            }

            //User just lifted finger off of screen
            case (MotionEvent.ACTION_UP): {
                if (event.getY() - yInitial >= swipeThreshold) {
                    Log.d(DEBUG_TAG, "yDist: " + (event.getY() - yInitial));
                    Intent i = new Intent(this, AppsListActivity.class);
                    startActivity(i);
                }
                return true;
            }

            default:
                return super.onTouchEvent(event);
        }
    }

    public View.OnClickListener showAppsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showApps(view);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher_home, menu);
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
}
