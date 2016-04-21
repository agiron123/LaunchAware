package com.launcher.openlauncher.ui.fragments;


import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.launcher.openlauncher.utils.PrefsKey;
import com.launcher.openlauncher.R;
import com.launcher.openlauncher.ui.views.AppItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment implements View.OnTouchListener {

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

    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";

    private float yInitial = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Drawable wallpaperDrawable = WallpaperManager.getInstance(getContext()).getDrawable();
        mainLayoutView.setBackground(wallpaperDrawable);

        loadApps();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        view.setOnTouchListener(this);
        return view;
    }

    private void loadApps() {
        final PackageManager pm = getContext().getPackageManager();

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
        String defaultApplication = Settings.Secure.getString(getContext().
                getContentResolver(), SMS_DEFAULT_APPLICATION);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
                    showAppsGrid(true);
                }
                return true;
            }

            default:
                return false;
        }
    }

    @OnClick(R.id.app_grid_image)
    public void showAppsGrid(){
        showAppsGrid(false);
    }

    private void showAppsGrid(boolean searchingInGrid){
        final AppsListFragment appsListFragment = new AppsListFragment();
        if(searchingInGrid){
            Bundle bundle = new Bundle();
            bundle.putBoolean(PrefsKey.SEARCHING_IN_GRID, true);
            appsListFragment.setArguments(bundle);
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AppsListFragment())
                .addToBackStack(null).commit();
    }
}
