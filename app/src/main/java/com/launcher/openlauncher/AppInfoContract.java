package com.launcher.openlauncher;

import android.provider.BaseColumns;

/**
 * Created by andre on 1/12/16.
 */

//TODO: Move to models package when code is refactored.
public final class AppInfoContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public AppInfoContract() {}

    /* Inner class that defines the table contents */
    public static abstract class AppInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "appinfo";
        public static final String COL_NAME_PACKAGE_NAME = "package_name";
        public static final String COL_NAME_NAME = "name";
        public static final String COL_NAME_WIFI_SSID = "wifi_ssid";
        public static final String COL_NAME_BLUETOOTH = "bluetooth_network";
        public static final String COL_NAME_LAUNCH_TIME = "launch_time";

        //Location Info:
        public static final String COL_NAME_LATITUDE = "latitude";
        public static final String COL_NAME_LONGITUDE = "longitude";

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        public static final String REAL_TYPE = " REAL";
        private static final String COMMA_SEP = ", ";

        public static final String SQL_CREATE_APPINFO_TABLE =
                "CREATE TABLE " + AppInfoEntry.TABLE_NAME + " (" +
                        AppInfoEntry._ID + " INTEGER PRIMARY KEY, " +
                        AppInfoEntry.COL_NAME_PACKAGE_NAME + TEXT_TYPE + COMMA_SEP +
                        AppInfoEntry.COL_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        AppInfoEntry.COL_NAME_WIFI_SSID + TEXT_TYPE + COMMA_SEP +
                        AppInfoEntry.COL_NAME_BLUETOOTH + TEXT_TYPE + COMMA_SEP +
                        AppInfoEntry.COL_NAME_LAUNCH_TIME + INTEGER_TYPE + COMMA_SEP +
                        AppInfoEntry.COL_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                        AppInfoEntry.COL_NAME_LONGITUDE + REAL_TYPE + ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + AppInfoEntry.TABLE_NAME;
    }
}