package com.launcher.openlauncher.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import slappahoe.kappa.openlauncher.R;


public class AppItem extends FrameLayout {

    @Bind(R.id.app_icon)
    ImageView mAppIcon;
    @Bind(R.id.app_name)
    TextView mAppName;

    public AppItem(Context context) {
        super(context);
        init();
    }

    public AppItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.app_item, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(Drawable appDrawable, String appName) {
        mAppIcon.setImageDrawable(appDrawable);
        mAppName.setText(appName);
    }
}
