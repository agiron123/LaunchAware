package com.launcher.openlauncher.ui.fragments;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.launcher.openlauncher.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingsFragment extends Fragment {

    private static final int SELECT_PHOTO = 100;

    @Bind(R.id.current_background_image)
    ImageView mCurrentBackgroundImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final Drawable wallpaperDrawable = WallpaperManager.getInstance(getContext()).getDrawable();
        mCurrentBackgroundImage.setBackground(wallpaperDrawable);
    }

    @OnClick(R.id.image_picker_layout)
    public void showImagePicker() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    mCurrentBackgroundImage.setImageURI(data.getData());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        startActivity(WallpaperManager.getInstance(getContext())
                                .getCropAndSetWallpaperIntent(data.getData()));
                    }
                }
        }
    }
}
