package net.parodycheck.rboots;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.view.*;
import android.media.*;

public class RBootsActivity extends Activity
{
    java.util.HashMap imageMap;
    RBootsView _v;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
			     WindowManager.LayoutParams.FLAG_FULLSCREEN);
	_v = new RBootsView(this);
	_v.setFocusable(true);
	_v.requestFocus();
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	setVolumeControlStream(AudioManager.STREAM_MUSIC);
	setContentView(_v);
	
    }
    public void onPause()
    {
	super.onPause();
	_v.pauseDrawing();
    }

    public void onResume()
    {
	super.onResume();
	_v.resumeDrawing();
    }
    public boolean onSearchRequested()
    {
	if(_v.isPaused()) {
	    _v.unpauseGame();
	}
	else {
	    _v.pauseGame();
	}
	return true;
    }

}
