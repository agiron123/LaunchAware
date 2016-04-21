package com.launcher.openlauncher.core;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                NetModule.class,
        }
)
public interface AppComponent {

    void inject(BaseActivity activity);

}
