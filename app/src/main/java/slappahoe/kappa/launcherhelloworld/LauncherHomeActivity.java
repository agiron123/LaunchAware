package slappahoe.kappa.launcherhelloworld;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LauncherHomeActivity extends Activity {

    private final String DEBUG_TAG = "LauncherHomeActiviy";
    private float xInitial = 0;
    private float yInitial = 0;

    //Y distance threshold for swipe down gesture to be recognized.
    private final float swipeThreshold = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //TODO: Get the current wallpaper to show in the background.
        /*
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Drawable wallpaper = wallpaperManager.peekDrawable();

        Toast.makeText(getApplicationContext(), "LauncherHomeActivity onCreate", Toast.LENGTH_LONG);

        if (wallpaper != null) {
            Toast.makeText(getApplicationContext(), "Wallpaper NOT NULL", Toast.LENGTH_SHORT);
            RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.home_view);
            rLayout.setBackground(wallpaper);
        } else {
            Toast.makeText(getApplicationContext(), "Wallpaper NULL", Toast.LENGTH_SHORT);
        }
        */

        //Requires a change in the minimum sdk
        setContentView(R.layout.activity_launcher_home);

        final Button appMenuButton = (Button) findViewById(R.id.appMenuButton);
        appMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                showApps(v);
            }
        });

    }

    public void showApps(View view) {
        Intent i = new Intent(this, AppsListActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action  = event.getActionMasked();

        switch(action) {
            //User just tapped on the screen
            case (MotionEvent.ACTION_DOWN) :{
                xInitial = event.getX();
                yInitial = event.getY();

                Log.d(DEBUG_TAG, "Down, XInit: " + xInitial + " YInit: " + yInitial);
                return true;
            }

            //User moved finger
            case (MotionEvent.ACTION_MOVE) :{

                return true;
            }

            //User just lifted finger off of screen
            case (MotionEvent.ACTION_UP) :{
                if(event.getY() - yInitial >= swipeThreshold)
                {
                    Log.d(DEBUG_TAG, "yDist: " + (event.getY() - yInitial));
                    Intent i = new Intent(this, AppsListActivity.class);
                    startActivity(i);
                }
                return true;
            }

            default :
                return super.onTouchEvent(event);
        }
    }

    public View.OnClickListener showAppsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showApps(view);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
