package slappahoe.kappa.launcherhelloworld;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import slappahoe.kappa.launcherhelloworld.utils.FavoriteAppsBuilder;

public class LauncherHomeActivity extends Activity implements AdapterView.OnItemClickListener{
    private final String DEBUG_TAG = "LauncherHomeActiviy";
    private float xInitial = 0;
    private float yInitial = 0;

    private List<String> favoriteApps;
    private List<ApplicationInfo> applicationInfos;

    //Y distance threshold for swipe down gesture to be recognized.
    private final float swipeThreshold = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_home);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        applicationInfos = new ArrayList<>();
        favoriteApps = new ArrayList<>();
        FavoriteAppsBuilder favoriteAppsBuilder = new FavoriteAppsBuilder();
        favoriteAppsBuilder.buildFavoriteApps(this, favoriteApps);
        initApplicationInfos();

        GridAdapter gridAdapter = new GridAdapter(this, applicationInfos);

        /* Views */
        View mainLayoutView = findViewById(R.id.home_view);
        GridView gridView = (GridView) findViewById(R.id.favorites_view);
        ImageView appGridImage = (ImageView) findViewById(R.id.app_grid_image);
        appGridImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppsList();
            }
        });

        mainLayoutView.setBackground(wallpaperDrawable);

        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);
    }

    private void initApplicationInfos() {
        try {
            for (int i = 0; i < favoriteApps.size(); i++) {
                applicationInfos.add(getPackageManager().getApplicationInfo(favoriteApps.get(i), 0));
            }
        }
        catch (PackageManager.NameNotFoundException e){
            Log.d("LauncherHomeActivity", e.getMessage());
        }
        catch (Exception e) {
            Log.d("LauncherHomeActivity", e.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String packageName = favoriteApps.get(position);
            startActivity(getApplicationContext()
                .getPackageManager().getLaunchIntentForPackage(packageName));
    }

    private void showAppsList(){
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
}
