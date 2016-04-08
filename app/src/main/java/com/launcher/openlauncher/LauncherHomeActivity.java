package com.launcher.openlauncher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;

import com.launcher.openlauncher.ui.AppItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LauncherHomeActivity extends Activity {

    @Bind(R.id.main_layout_view)
    View mainLayoutView;
    @Bind(R.id.app_item_one)
    AppItem mAppItemOne;
    @Bind(R.id.app_item_two)
    AppItem mAppItemTwo;
    @Bind(R.id.app_item_three)
    AppItem mAppItemThree;
    @Bind(R.id.app_item_four)
    AppItem mAppItemFour;

    private float yInitial = 0;

    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_home);

        ButterKnife.bind(this);

        final Drawable wallpaperDrawable = WallpaperManager.getInstance(this).getDrawable();
        mainLayoutView.setBackground(wallpaperDrawable);

        loadApps();
    }

    @OnClick(R.id.app_grid_image)
    public void showAppsList() {
        startActivity(new Intent(this, AppsListActivity.class));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();

        switch (action) {
            //User just tapped on the screen
            case (MotionEvent.ACTION_DOWN): {
                yInitial = event.getY();
                return true;
            }

            //User moved finger
            case (MotionEvent.ACTION_MOVE): {

                return true;
            }

            //User just lifted finger off of screen
            case (MotionEvent.ACTION_UP): {
                float swipeThreshold = 100;
                if (event.getY() - yInitial >= swipeThreshold) {
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
        if (phoneAppInfo != null) {
            mAppItemOne.setDisplay(phoneAppInfo.loadIcon(pm),
                    phoneAppInfo.loadLabel(pm).toString());
            mAppItemOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(phoneIntent);
                }
            });
        }

        // Texting
        String defaultApplication = Settings.Secure.getString(getContentResolver(), SMS_DEFAULT_APPLICATION);
        final Intent messagingIntent = pm.getLaunchIntentForPackage(defaultApplication);
        ResolveInfo messagingAppInfo = pm.resolveActivity(messagingIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (messagingAppInfo != null) {
            mAppItemTwo.setDisplay(messagingAppInfo.loadIcon(pm),
                    messagingAppInfo.loadLabel(pm).toString());
            mAppItemTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(messagingIntent);
                }
            });
        }

        // Contacts
        final Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        ResolveInfo contactsAppInfo = pm.resolveActivity(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (contactsAppInfo != null) {
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
        if (browserAppInfo != null) {
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
