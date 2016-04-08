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
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.launcher.openlauncher.ui.AppItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import slappahoe.kappa.openlauncher.R;

public class LauncherHomeActivity extends Activity {

    @Bind(R.id.main_layout_view)
    View mainLayoutView;
    @Bind(R.id.app_item_one)
    AppItem mAppItemOne;
    @Bind(R.id.app_item_two)
    AppItem mAppitemTwo;
    @Bind(R.id.app_item_three)
    AppItem mAppItemThree;
    @Bind(R.id.app_item_four)
    AppItem mAppItemFour;

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

        /* Views */
        ButterKnife.bind(this);
        mainLayoutView.setBackground(wallpaperDrawable);
        loadApps();
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

        // Phone
        final Intent phoneIntent = (new Intent(Intent.ACTION_DIAL));
        final ResolveInfo phoneAppInfo = pm.resolveActivity(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (phoneIntent != null && phoneAppInfo != null) {
            mAppItemOne.setDisplay(phoneAppInfo.loadIcon(pm),
                    phoneAppInfo.loadLabel(pm).toString());
            mAppItemOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(phoneIntent);
//                    startActivity(getApplicationContext()
//                            .getPackageManager().getLaunchIntentForPackage(phoneAppInfo.resolvePackageName));
                }
            });
        }

        // Texting
        String defaultApplication = Settings.Secure.getString(getContentResolver(), SMS_DEFAULT_APPLICATION);
        final Intent messagingIntent = pm.getLaunchIntentForPackage(defaultApplication);
        ResolveInfo messagingAppInfo = pm.resolveActivity(messagingIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (messagingIntent != null && messagingAppInfo != null) {
            mAppitemTwo.setDisplay(messagingAppInfo.loadIcon(pm),
                    messagingAppInfo.loadLabel(pm).toString());
            mAppitemTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(messagingIntent);
                }
            });
        }

        // Contacts
        final Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        contactsIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        ResolveInfo contactsAppInfo = pm.resolveActivity(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (contactsIntent != null && contactsAppInfo != null) {
            mAppItemThree.setDisplay(contactsAppInfo.loadIcon(pm),
                    contactsAppInfo.loadLabel(pm).toString());
            mAppItemThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(contactsIntent);

                }
            });
        }

        // Browser
        final Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        final ResolveInfo browserAppInfo = pm.resolveActivity(browserIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (browserIntent != null && browserAppInfo != null) {
            mAppItemFour.setDisplay(browserAppInfo.loadIcon(pm),
                    browserAppInfo.loadLabel(pm).toString());
            mAppItemFour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(browserIntent);
                }
            });
        }
    }
}
