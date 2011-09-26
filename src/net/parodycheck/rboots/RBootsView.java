package net.parodycheck.rboots;

import java.util.HashMap;

import android.content.Context;
import android.util.*;
import android.view.*;
import android.graphics.*;

import net.parodycheck.spritecorehc.*;

public class RBootsView extends SpriteCoreView implements SpriteCoreEventAgent
{
    

    public enum GameState {
	TITLE_SCREEN, RUNNING
    }

    RBootsActivity myActivity;
    GameState state;
    Sprite bg;
    HashMap imageMap;

    public RBootsView(RBootsActivity a)
    {
	super(a);
	myActivity = a;
	initTitle();
	setEventAgent(this);
    }
    
    public void initTitle()
    {
	Bitmap title = BitmapFactory.decodeResource(myActivity.getResources(),R.drawable.title);
	bg = new Sprite(this,title);
	state = GameState.TITLE_SCREEN;
    }

    public void initGame()
    {
	remove(bg);
	initBackground();
	state = GameState.RUNNING;
    }

    public void initBackground()
    {
	Bitmap bgbmp = BitmapFactory.decodeResource(myActivity.getResources(),R.drawable.bg1);
	bg = new Sprite(this,bgbmp);
	bg.setAppearanceAgent(new RBootsBackgroundAppearanceAgent());
	bg.setVel(-0.5f,0.0f);
    }

    public void handleEvent(InputEvent e)
    {
	    Log.e("RBootsView","got event");
	switch(state)
	    {
	    case TITLE_SCREEN:
		handleEventTitle(e);
		break;
	    case RUNNING:
		handleEventRunning(e);
		break;
	    }
    }

    public void handleEventTitle(InputEvent e)
    {
	if(e instanceof MotionEvent) {
	    Log.e("RBootsView","got motion event");
	    MotionEvent me = (MotionEvent)e;
	    if(me.getAction() == MotionEvent.ACTION_DOWN) {
		Log.e("RBootsView","action down received");
		initGame();
	    }
	}
    }

    public void handleEventRunning(InputEvent e)
    {
    }
}