package com.launcher.openlauncher.core;

import android.app.Application;
import android.content.Context;


public class LaunchAwareApplication extends Application {

    private static final String BASE_URL = "";

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this)).netModule(new NetModule(BASE_URL)).build();
    }

    public static AppComponent getComponent(Context context) {
        return ((LaunchAwareApplication) context.getApplicationContext()).appComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
