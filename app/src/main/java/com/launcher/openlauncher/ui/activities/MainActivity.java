package com.launcher.openlauncher.ui.activities;

import android.os.Bundle;

import com.launcher.openlauncher.R;
import com.launcher.openlauncher.core.BaseActivity;
import com.launcher.openlauncher.ui.fragments.HomeFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_home);
    }
}
