package net.parodycheck.rboots;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.ActivityInfo;

public class RBootsActivity extends Activity
{
    java.util.HashMap imageMap;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	RBootsView v = new RBootsView(this);
	v.setFocusable(true);
	v.requestFocus();
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	setContentView(v);
	
    }
    public void onPause()
    {
	super.onPause();
	
    }
}
