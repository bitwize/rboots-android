package net.parodycheck.rboots;

import java.util.*;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.*;
import android.view.*;
import android.graphics.*;
import android.media.*;

import net.parodycheck.spritecorehc.*;

public class RBootsView extends SpriteCoreView implements SpriteCoreEventAgent
{
    

    public enum GameState {
	TITLE_SCREEN, INSTRUCTION_SCREEN, RUNNING, PAUSED, GAMEOVER
    }

    RBootsActivity _myActivity;
    GameState _state;
    Sprite _bg;
    Sprite _hearts[];
    Sprite _scoar1;
    Sprite _scoar2;
    Sprite _bestScoar1;
    Sprite _bestScoar2;
    Sprite _scoarDigits[];
    Sprite _bestDigits[];
    Sprite _pauseSprite;
    Sprite _itsoverSprite;
    RBootsWallSprite _walls;
    RBootsPlayer _player;
    RBootsObstacleGenerator _og;
    SpriteAppearanceAgent _nullAppearanceAgent;
    SoundPool _pool;
    int _boost;
    int _hurt;
    int _scoarval;
    ArrayList<Bitmap> _heartshape;
    ArrayList<Bitmap> _scoarshapes;
    ArrayList<Bitmap> _pauseshapes;
    Bitmap _itsoverimg;
    SharedPreferences _prefs;

    public RBootsView(RBootsActivity a)
    {
	super(a);
	_myActivity = a;
	_prefs = a.getPreferences(android.content.Context.MODE_PRIVATE);
	initTitle();
	setEventAgent(this);
	setViewportSize(480,320);
	_nullAppearanceAgent = new SpriteAppearanceAgent() {
		public void renderSpriteOn(Sprite aSprite,Canvas aCanvas) {

		}
	    };
	requestFocus();
    }
    
    public void initTitle()
    {
	Bitmap title = loadBitmap(R.drawable.title);
	Bitmap insns = loadBitmap(R.drawable.instructions);
	ArrayList<Bitmap> bl = new ArrayList<Bitmap>(2);
	bl.add(title);
	bl.add(insns);
	_bg = new Sprite(this,bl);
	_state = GameState.TITLE_SCREEN;
    }

    public void initInstructions()
    {
	_bg.setCurrentFrame(1);
	_state = GameState.INSTRUCTION_SCREEN;
    }

    public void initGame()
    {
	clear();
	initSounds();
	initBackground();
	initWalls();
	initObstacles();
	initHearts();
	initPlayer();
	initScoar();
	initPause();
	_state = GameState.RUNNING;
    }

    public void pauseGame()
    {
	_state = GameState.PAUSED;
    }

    public void unpauseGame()
    {
	_state = GameState.RUNNING;
    }

    public boolean isPaused()
    {
	return _state == GameState.PAUSED;
    }

    public void initBackground()
    {
	Bitmap bgbmp = loadBitmap(R.drawable.bg1);
	_bg = new Sprite(this,bgbmp);
	_bg.setAppearanceAgent(new RBootsBackgroundAppearanceAgent());
    }

    public void initWalls()
    {
	RBootsWallSprite.initWallImg(_myActivity);
	_walls = new RBootsWallSprite(this,_bg);
    }

    public void initHearts()
    {
	_heartshape = new ArrayList<Bitmap>(2);
	_heartshape.add(loadBitmap(R.drawable.redheart));
	_heartshape.add(loadBitmap(R.drawable.whiteheart));
	_hearts = new Sprite[3];
	for(int i=0;i<_hearts.length;i++) {
	    _hearts[i] = new Sprite(this,_heartshape);
	    _hearts[i].moveTo(i * 20.0f,0.0f);
	}
    }

    public void initObstacles()
    {
	_og = new RBootsObstacleGenerator(this);
    }

    public void initPlayer()
    {
	RBootsPlayer.initPlayerImgs(this);
	_player = new RBootsPlayer(this);
	_player.moveTo(40.0f,144.0f);
    }

    public void initScoar()
    {
	_scoarshapes = new ArrayList<Bitmap>(10);
	_scoarshapes.add(loadBitmap(R.drawable.digit0));
	_scoarshapes.add(loadBitmap(R.drawable.digit1));
	_scoarshapes.add(loadBitmap(R.drawable.digit2));
	_scoarshapes.add(loadBitmap(R.drawable.digit3));
	_scoarshapes.add(loadBitmap(R.drawable.digit4));
	_scoarshapes.add(loadBitmap(R.drawable.digit5));
	_scoarshapes.add(loadBitmap(R.drawable.digit6));
	_scoarshapes.add(loadBitmap(R.drawable.digit7));
	_scoarshapes.add(loadBitmap(R.drawable.digit8));
	_scoarshapes.add(loadBitmap(R.drawable.digit9));
	_scoarshapes.add(loadBitmap(R.drawable.meters));
	_scoarshapes.add(loadBitmap(R.drawable.scoar));
	_scoarshapes.add(loadBitmap(R.drawable.best));
	_scoar1 = new Sprite(this,_scoarshapes);
	_scoar1.setCurrentFrame(11);
	_scoar1.moveTo(480.0f - _scoar1.shape().getWidth(), 0.0f);
	_scoar2 = new Sprite(this,_scoarshapes);
	_scoar2.setCurrentFrame(10);
	_scoar2.moveTo(480.0f - _scoar2.shape().getWidth(), _scoar1.shape().getHeight());
	_bestScoar1 = new Sprite(this,_scoarshapes);
	_bestScoar1.setCurrentFrame(12);
	_bestScoar1.moveTo(320.0f - _bestScoar1.shape().getWidth(), 0.0f);
	_bestScoar2 = new Sprite(this,_scoarshapes);
	_bestScoar2.setCurrentFrame(10);
	_bestScoar2.moveTo(320.0f - _bestScoar2.shape().getWidth(), _bestScoar1.shape().getHeight());
	_scoarDigits = new Sprite[6];
	_bestDigits = new Sprite[6];
	for(int i=0;i<_scoarDigits.length;i++) {
	    _scoarDigits[i] = new Sprite(this,_scoarshapes);
	    _bestDigits[i] = new Sprite(this,_scoarshapes);
	}
	_itsoverimg = loadBitmap(R.drawable.itsover);
	updateScoar(0);
	displayBest();
    }

    public void initSounds() {
	_pool = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
	_boost = _pool.load(_myActivity,R.raw.boost,1);
	_hurt = _pool.load(_myActivity,R.raw.hurt,1);
    }

    public void initPause() {
	_pauseshapes = new ArrayList<Bitmap>(2);
	_pauseshapes.add(loadBitmap(R.drawable.pause));
	_pauseshapes.add(loadBitmap(R.drawable.play));
	_pauseSprite = new Sprite(this,_pauseshapes);
	_pauseSprite.moveTo(480.0f - _pauseSprite.shape().getWidth(),
			    320.0f - _pauseSprite.shape().getHeight());
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
	Log.e("RBootsView","got event, state: " + _state.toString());
	    
	switch(_state)
	    {
	    case TITLE_SCREEN:
		handleEventTitle(e);
		break;
	    case INSTRUCTION_SCREEN:
		handleEventInstructions(e);
		break;
	    case RUNNING:
		handleEventRunning(e);
		break;
	    case PAUSED:
		handleEventPaused(e);
		break;
	    case GAMEOVER:
		handleEventGameOver(e);
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
		initInstructions();
	    }
	}
    }

    public void handleEventInstructions(InputEvent e)
    {
	if(e instanceof MotionEvent) {
	    Log.e("RBootsView","got motion event");
	    MotionEvent me = (MotionEvent)e;
	    if(me.getAction() == MotionEvent.ACTION_DOWN) {
		Log.e("RBootsView","action down received");
		initGame();
		_player.thrustOn();
	    }
	}
    }

    public void handleEventRunning(InputEvent e)
    {
	if(e instanceof MotionEvent) {
	    MotionEvent me = (MotionEvent)e;
	    if(me.getX() >= _pauseSprite.pos().x &&
	       me.getX() <  _pauseSprite.pos().x + _pauseSprite.shape().getWidth() &&
	       me.getY() >= _pauseSprite.pos().y &&
	       me.getY() <  _pauseSprite.pos().y + _pauseSprite.shape().getHeight() &&
	       me.getAction() == MotionEvent.ACTION_DOWN) {
		_pauseSprite.setCurrentFrame(1);
		pauseGame();
		return;
	    }
	    Log.e("RBootsView","got motion event: " + Integer.toString(me.getAction()));
	    if(me.getAction() == MotionEvent.ACTION_DOWN) {
		_player.thrustOn();
	    }
	    else if(me.getAction() == MotionEvent.ACTION_UP ||
		    me.getAction() == MotionEvent.ACTION_CANCEL) {
		_player.thrustOff();
	    }
	}
    }

    public void handleEventPaused(InputEvent e)
    {
	if(e instanceof MotionEvent) {
	    MotionEvent me = (MotionEvent)e;
	    if(me.getX() >= _pauseSprite.pos().x &&
	       me.getX() <  _pauseSprite.pos().x + _pauseSprite.shape().getWidth() &&
	       me.getY() >= _pauseSprite.pos().y &&
	       me.getY() <  _pauseSprite.pos().y + _pauseSprite.shape().getHeight() &&
	       me.getAction() == MotionEvent.ACTION_DOWN) {
		_pauseSprite.setCurrentFrame(0);
		unpauseGame();
		return;
	    }
	}

    }

    public void handleEventGameOver(InputEvent e)
    {
	handleEventInstructions(e);
    }

    public RBootsWallSprite walls()
    {
	return _walls;
    }

    public RBootsPlayer player()
    {
	return _player;
    }

    public RBootsObstacleGenerator ogen()
    {
	return _og;
    }

    public void signalDeath() {
	_walls.setVel(0.0f,0.0f);
	_player.thrustOff();
	_itsoverSprite = new Sprite(this,_itsoverimg);
	_itsoverSprite.moveTo((getViewportSize().x - _itsoverimg.getWidth()) / 2.0f,
			      100.0f);
	updateBest();
	_state = GameState.GAMEOVER;
    }

    public void updateScoar(int newScoar)
    {
	if(newScoar < 0) newScoar = -newScoar;
	_scoarval = newScoar;
	float x = _scoar2.pos().x - (_scoar2.shape().getWidth());
	float y = _scoar1.shape().getHeight();
	for(int i=0;i<_scoarDigits.length;i++) {
	    _scoarDigits[i].setCurrentFrame(newScoar % 10);
	    _scoarDigits[i].moveTo(x - _scoarDigits[i].shape().getWidth(),y);
	    if(newScoar <= 0) {
		_scoarDigits[i].setAppearanceAgent(_nullAppearanceAgent);
	    }
	    else {
		_scoarDigits[i].setAppearanceAgent(DefaultAppearanceAgent.instance());
	    }
	    newScoar /= 10;
	    x -= _scoarDigits[i].shape().getWidth();
	}
	
    }

    public void updateBest() {
	if (_scoarval > _prefs.getInt("best",0)) {
	    SharedPreferences.Editor e = _prefs.edit();
	    e.putInt("best",_scoarval);
	    e.commit();
	}
	displayBest();
    }

    public void displayBest() {
	int best = _prefs.getInt("best",0);
	float x = _bestScoar2.pos().x - (_bestScoar2.shape().getWidth());
	float y = _bestScoar1.shape().getHeight();
	for(int i=0;i<_bestDigits.length;i++) {
	    _bestDigits[i].setCurrentFrame(best % 10);
	    _bestDigits[i].moveTo(x - _bestDigits[i].shape().getWidth(),y);
	    if(best <= 0) {
		_bestDigits[i].setAppearanceAgent(_nullAppearanceAgent);
	    }
	    else {
		_bestDigits[i].setAppearanceAgent(DefaultAppearanceAgent.instance());
	    }
	    best /= 10;
	    x -= _bestDigits[i].shape().getWidth();
	}
	
    }

}
