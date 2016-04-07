package com.launcher.openlauncher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";


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
                    i.putExtra(PrefsKey.SEARCHING_IN_GRID, true);
                    startActivity(i);
                }
                return true;
            }

            default:
                return super.onTouchEvent(event);
        }
    }

    private void loadApps() {
        final PackageManager pm = getPackageManager();
        ImageView imageView;
        TextView textView;

        // Phone
        final Intent phoneIntent = (new Intent(Intent.ACTION_DIAL));
        ResolveInfo phoneAppInfo = pm.resolveActivity(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (phoneIntent != null && phoneAppInfo != null) {
            View phoneAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) phoneAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(phoneAppInfo.loadIcon(pm));
            textView = (TextView) phoneAppView.findViewById(R.id.app_name);
            textView.setText(phoneAppInfo.loadLabel(pm));

            phoneAppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(phoneIntent);
                }
            });
            gridOne.addView(phoneAppView);
        }

        // Texting
        String defaultApplication = Settings.Secure.getString(getContentResolver(), SMS_DEFAULT_APPLICATION);
        final Intent messagingIntent = pm.getLaunchIntentForPackage(defaultApplication);
        ResolveInfo messagingAppInfo = pm.resolveActivity(messagingIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (messagingIntent != null && messagingAppInfo != null) {
            View messagingAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) messagingAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(messagingAppInfo.loadIcon(pm));
            textView = (TextView) messagingAppView.findViewById(R.id.app_name);
            textView.setText(messagingAppInfo.loadLabel(pm));

            messagingAppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(messagingIntent);
                }
            });
            gridOne.addView(messagingAppView);
        }

        // Contacts
        final Intent contactsIntent = (new Intent(Intent.ACTION_DIAL));
        ResolveInfo contactsAppInfo = pm.resolveActivity(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (messagingIntent != null && contactsAppInfo != null) {
            View contactsAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) contactsAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(contactsAppInfo.loadIcon(pm));
            textView = (TextView) contactsAppView.findViewById(R.id.app_name);
            textView.setText(contactsAppInfo.loadLabel(pm));

            contactsAppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(contactsIntent);
                }
            });
            gridTwo.addView(contactsAppView);
        }

        // Browser
        final Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        final ResolveInfo browserAppInfo = pm.resolveActivity(browserIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (browserIntent != null && browserAppInfo != null) {
            View browserAppView = View.inflate(this, R.layout.grid_item, null);
            imageView = (ImageView) browserAppView.findViewById(R.id.app_icon);
            imageView.setImageDrawable(browserAppInfo.loadIcon(pm));
            textView = (TextView) browserAppView.findViewById(R.id.app_name);
            textView.setText(browserAppInfo.loadLabel(pm));

            browserAppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(browserIntent);
                }
            });
            gridTwo.addView(browserAppView);
        }
//
//        i = (new Intent(Intent.ACTION_SEND));
//        i.setType(TEXT_SERVICES_MANAGER_SERVICE);
//        ResolveInfo messagesAppInfo = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
//        if (messagesAppInfo != null) {
//            View messagesAppView = View.inflate(this, R.layout.grid_item, null);
//            imageView = (ImageView) messagesAppView.findViewById(R.id.app_icon);
//            imageView.setImageDrawable(messagesAppInfo.loadIcon(pm));
//            textView = (TextView) messagesAppView.findViewById(R.id.app_name);
//            textView.setText(messagesAppInfo.loadLabel(pm));
//
//            gridOne.addView(messagesAppView);
//        }
//
//        i = (new Intent(Intent.ACTION_VIEW));
//        ResolveInfo browserAppInfo = pm.resolveActivity(i, 0);
//        if (browserAppInfo != null) {
//            View browserAppView = View.inflate(this, R.layout.grid_item, null);
//            imageView = (ImageView) browserAppView.findViewById(R.id.app_icon);
//            imageView.setImageDrawable(browserAppInfo.loadIcon(pm));
//            textView = (TextView) browserAppView.findViewById(R.id.app_name);
//            textView.setText(browserAppInfo.loadLabel(pm));
//
//            gridTwo.addView(browserAppView);
//        }
//
//        i = (new Intent(Intent.CATEGORY_APP_CONTACTS));
//        ResolveInfo contactsAppInfo = pm.resolveActivity(i, 0);
//        if (contactsAppInfo != null) {
//            View contactsAppView = View.inflate(this, R.layout.grid_item, null);
//            imageView = (ImageView) contactsAppView.findViewById(R.id.app_icon);
//            imageView.setImageDrawable(contactsAppInfo.loadIcon(pm));
//            textView = (TextView) contactsAppView.findViewById(R.id.app_name);
//            textView.setText(contactsAppInfo.loadLabel(pm));
//
//            gridTwo.addView(contactsAppView);
//        }
    }
}
