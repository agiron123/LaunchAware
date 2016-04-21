package com.launcher.openlauncher.core;


import android.app.Application;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    final LaunchAwareApplication mApplication;

    public AppModule(LaunchAwareApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public LaunchAwareApplication providesLaunchAwareApp() {
        return mApplication;
    }

    @Provides
    @Singleton
    public Application providesApplication(LaunchAwareApplication app) {
        return app;
    }
}
