package slappahoe.kappa.launcherhelloworld;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


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

        ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);
        Drawable appIcon = packageManager.getApplicationIcon(packages.get(position));
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));

        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
        appName.setText(packageManager.getApplicationLabel(packages.get(position)));

        return convertView;
    }
}