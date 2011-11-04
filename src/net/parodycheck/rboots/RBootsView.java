package net.parodycheck.rboots;

import java.util.*;


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

    RBootsActivity _myActivity;
    GameState _state;
    Sprite _bg;
    Sprite _hearts[];
    ArrayList _heartshape;
    RBootsWallSprite _walls;
    RBootsPlayer _player;

    public RBootsView(RBootsActivity a)
    {
	super(a);
	_myActivity = a;
	initTitle();
	setEventAgent(this);
	setViewportSize(480,320);
    }
    
    public void initTitle()
    {
	Bitmap title = BitmapFactory.decodeResource(_myActivity.getResources(),R.drawable.title);
	_bg = new Sprite(this,title);
	_state = GameState.TITLE_SCREEN;
    }

    public void initGame()
    {
	remove(_bg);
	initBackground();
	initWalls();
	initHearts();
	initPlayer();
	_state = GameState.RUNNING;
    }

    public void initBackground()
    {
	Bitmap bgbmp = BitmapFactory.decodeResource(_myActivity.getResources(),R.drawable.bg1);
	_bg = new Sprite(this,bgbmp);
	_bg.setAppearanceAgent(new RBootsBackgroundAppearanceAgent());
    }

    public void initWalls()
    {
	RBootsWallSprite.initWallImg(_myActivity);
	_walls = new RBootsWallSprite(this,_bg);
	_walls.setVel(-1.0f,0.0f);
    }

    public void initHearts()
    {
	_heartshape = new ArrayList<Bitmap>(2);
	_heartshape.add(BitmapFactory.decodeResource(_myActivity.getResources(),R.drawable.redheart));
	_heartshape.add(BitmapFactory.decodeResource(_myActivity.getResources(),R.drawable.whiteheart));
	_hearts = new Sprite[3];
	for(int i=0;i<_hearts.length;i++) {
	    _hearts[i] = new Sprite(this,_heartshape);
	    _hearts[i].moveTo(i * 20.0f,0.0f);
	}
    }

    public void initPlayer()
    {
	RBootsPlayer.initPlayerImgs(_myActivity);
	_player = new RBootsPlayer(this);
	_player.moveTo(40.0f,144.0f);
    }

    public void setLife(int life)
    {
	for(int i=0;i<_hearts.length;i++) {
	    if(i < life) {
		_hearts[i].setCurrentFrame(0);
	    }
	    else {
		_hearts[i].setCurrentFrame(1);
	    }
	}
    }

    public void handleEvent(InputEvent e)
    {
	    Log.e("RBootsView","got event");
	switch(_state)
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
	if(e instanceof MotionEvent) {
	    Log.e("RBootsView","got motion event");
	    MotionEvent me = (MotionEvent)e;
	    if(me.getAction() == MotionEvent.ACTION_DOWN) {
		_player.thrustOn();
	    }
	    else if(me.getAction() == MotionEvent.ACTION_UP ||
		    me.getAction() == MotionEvent.ACTION_CANCEL) {
		_player.thrustOff();
	    }
	}
    }

    public RBootsWallSprite walls()
    {
	return _walls;
    }

    public void signalDeath() {
	_walls.setVel(0.0f,0.0f);
    }

}
