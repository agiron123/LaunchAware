package slappahoe.kappa.launcherhelloworld;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**

 * Created by andre on 6/21/15.
 */
public class GridAdapter extends BaseAdapter {
    private Context context;

    private List<ApplicationInfo> packages;
    private PackageManager packageManager;

    public GridAdapter(Context context, List<ApplicationInfo> packages) {
        this.context = context;
        this.packages = packages;
        this.packageManager = context.getPackageManager();
    }

    public long getItemId(int position) {
        return 0;
    }

    public int getCount() {
        return packages.size();
    }

    public Object getItem(int i) {
        return null;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        //Inflate the layout
        //LinearLayout layout = new LinearLayout(context);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.appIcon);
        Drawable appIcon = packageManager.getApplicationIcon(packages.get(position));
        icon.setImageDrawable(appIcon);

        TextView appName = (TextView) convertView.findViewById(R.id.appName);
        appName.setText(packageManager.getApplicationLabel(packages.get(position)));

        return convertView;

        /*
        ImageView iv = new ImageView(context);
        iv.setId(R.id.appIcon);
        Drawable appIcon = packageManager.getApplicationIcon(packages.get(position));
        iv.setImageDrawable(appIcon);
        */

//        TextView tv = (TextView) view.findViewById(R.id.appName);
//        tv.setText(packages.get(position).name);


        /*
        TextView text = new TextView(context);
        text.setId(R.id.appName);
        text.setText(packageManager.getApplicationLabel(packages.get(position)));

        layout.addView(iv);
        layout.addView(text);

        View view = layoutInflater.inflate(R.layout.grid_item, layout, false);
        layout.addView(view);
        return layout;
        */
    }
}
