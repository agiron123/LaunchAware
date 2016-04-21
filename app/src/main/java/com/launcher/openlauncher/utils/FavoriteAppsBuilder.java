package com.launcher.openlauncher.utils;


import android.content.Context;

import java.util.List;

public class FavoriteAppsBuilder {

    private static final String PREFS_NAME = "favorite_apps_prefs";

    private static final String FAVORITE_ONE = "favorite1";
    private static final String FAVORITE_TWO = "favorite2";
    private static final String FAVORITE_THREE = "favorite3";
    private static final String FAVORITE_FOUR = "favorite4";

    // Replace these with default for the phone, not hardcoded apps
    private static final String DIALER_APP = "com.google.android.dialer";
    private static final String MESSAGING_APP = "com.google.android.apps.messaging";
    private static final String MAPS_APP = "com.google.android.gm";
    private static final String CHROME_APP = "com.android.chrome";

    public void buildFavoriteApps(Context ctx, List<String> favoriteApps){
//        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        if (!settings.contains(FAVORITE_ONE)) {
//            favoriteApps.add(DIALER_APP);
//        }
//        if (!settings.contains(FAVORITE_TWO)) {
//            favoriteApps.add(MESSAGING_APP);
//        }
//        if (!settings.contains(FAVORITE_THREE)) {
//            favoriteApps.add(MAPS_APP);
//        }
//        if (!settings.contains(FAVORITE_FOUR)) {
//            favoriteApps.add(CHROME_APP);
//        }
        favoriteApps.add(DIALER_APP);
        favoriteApps.add(MESSAGING_APP);
        favoriteApps.add(MAPS_APP);
        favoriteApps.add(CHROME_APP);
    }
}
