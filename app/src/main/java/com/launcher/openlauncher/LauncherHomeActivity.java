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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
    GridLayout gridOne;
    @Bind(R.id.favorites_view_two)
    GridLayout gridTwo;


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
//        gridOne.setAdapter(gridAdapter);
//        gridOne.setOnItemClickListener(this);

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
        PackageManager pm = getPackageManager();
        ImageView imageView;
        TextView textView;

        Intent i = (new Intent(Intent.ACTION_DIAL));
        ResolveInfo phoneAppInfo = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (phoneAppInfo != null) {
            View phoneAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) phoneAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(phoneAppInfo.loadIcon(pm));
            textView = (TextView) phoneAppView.findViewById(R.id.app_name);
            textView.setText(phoneAppInfo.loadLabel(pm));

            gridOne.addView(phoneAppView);
        }

        i = (new Intent(Intent.ACTION_SEND));
        i.setType(TEXT_SERVICES_MANAGER_SERVICE);
        ResolveInfo messagesAppInfo = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (messagesAppInfo != null) {
            View messagesAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) messagesAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(messagesAppInfo.loadIcon(pm));
            textView = (TextView) messagesAppView.findViewById(R.id.app_name);
            textView.setText(messagesAppInfo.loadLabel(pm));

            gridOne.addView(messagesAppView);
        }

        i = (new Intent(Intent.ACTION_VIEW));
        ResolveInfo browserAppInfo = pm.resolveActivity(i, 0);
        if (browserAppInfo != null) {
            View browserAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) browserAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(browserAppInfo.loadIcon(pm));
            textView = (TextView) browserAppView.findViewById(R.id.app_name);
            textView.setText(browserAppInfo.loadLabel(pm));

            gridTwo.addView(browserAppView);
        }

        i = (new Intent(Intent.CATEGORY_APP_CONTACTS));
        ResolveInfo contactsAppInfo = pm.resolveActivity(i, 0);
        if (contactsAppInfo != null) {
            View contactsAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) contactsAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(contactsAppInfo.loadIcon(pm));
            textView = (TextView) contactsAppView.findViewById(R.id.app_name);
            textView.setText(contactsAppInfo.loadLabel(pm));

            gridTwo.addView(contactsAppView);
        }
    }
}
