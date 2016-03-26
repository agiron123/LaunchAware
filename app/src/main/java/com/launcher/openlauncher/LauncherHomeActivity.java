package com.launcher.openlauncher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import slappahoe.kappa.openlauncher.R;

public class LauncherHomeActivity extends Activity implements AdapterView.OnItemClickListener {

    @Bind(R.id.home_view)
    View mainLayoutView;
    @Bind(R.id.favorites_view_one)
    GridView gridView;


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

        GridAdapter gridAdapter = new GridAdapter(this, applicationInfos);

        /* Views */
        ButterKnife.bind(this);

        mainLayoutView.setBackground(wallpaperDrawable);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);

        loadApps();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String packageName = favoriteApps.get(position);
        startActivity(getApplicationContext()
                .getPackageManager().getLaunchIntentForPackage(packageName));
    }

    @OnClick(R.id.app_grid_image)
    public void showAppsList() {
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

    private void loadApps() {
        Intent i = (new Intent(Intent.ACTION_DIAL));
        PackageManager pm = getPackageManager();
        final ResolveInfo mInfo = pm.resolveActivity(i, 0);

        View view = View.inflate(this, R.layout.grid_item, null);
//        ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
//        imageView.setImageDrawable(mInfo.loadIcon(pm));
//        TextView textView = (TextView) view.findViewById(R.id.app_name);
//        textView.setText(mInfo.loadLabel(pm));
//
//        gridView.addView(view);
//        Toast.makeText(this, pm.getApplicationLabel(mInfo.activityInfo.applicationInfo), Toast.LENGTH_LONG).show();
    }
}
