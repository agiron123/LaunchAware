package slappahoe.kappa.launcherhelloworld;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andre on 6/21/15.
 */
public class GridAdapter extends BaseAdapter {
    private Context context;

    List<ApplicationInfo> packages;
    final PackageManager pm;

    public GridAdapter(Context context, List<ApplicationInfo> packages) {
        this.context = context;
        this.packages = packages;
        this.pm = context.getPackageManager();
    }

    public long getItemId(int position) {
        return 0;
    }

    public int getCount() {
        return packages.size();
    }

    public Object getItem(int i){
        return null;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        //Inflate the layout
        LinearLayout layout = new LinearLayout(context);
        LayoutInflater li = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View MyView = li.inflate(R.layout.grid_item, layout, false);
        // Add The Text!!!

        ImageView iv = (ImageView)MyView.findViewById(R.id.appIcon);
        Drawable appIcon = pm.getApplicationLogo(packages.get(position));
        iv.setImageDrawable(appIcon);

        TextView tv = (TextView)MyView.findViewById(R.id.appName);
        tv.setText(packages.get(position).name );

        TextView text = new TextView(context);
        text.setText("Hello");
        layout.addView(text);
        return layout;
    }
}
