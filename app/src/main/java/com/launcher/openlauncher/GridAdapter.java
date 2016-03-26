package com.launcher.openlauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import slappahoe.kappa.openlauncher.R;


public class GridAdapter extends BaseAdapter {

    private Context context;
    private List<ApplicationInfo> packages;
    private PackageManager packageManager;

    public GridAdapter(Context context, List<ApplicationInfo> packages) {
        this.context = context;
        this.packages = packages;
        this.packageManager = context.getPackageManager();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return packages.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);
        }

        ApplicationInfo applicationInfo = packages.get(position);
        ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);
        icon.setImageDrawable(applicationInfo.loadIcon(packageManager));

        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
        appName.setText(packageManager.getApplicationLabel(applicationInfo));

        return convertView;
    }
}