package net.parodycheck.rboots;

import java.util.*;
import android.util.*;
import android.graphics.*;
import android.media.*;
import net.parodycheck.spritecorehc.*;

public class RBootsPlayer extends Sprite
{

    private static ArrayList<Bitmap> thePlayerImgs;
    private static ArrayList<Bitmap> theFlameImgs;
    private SoundPool _pool;
    private int _boostID;
    private int _hurtID;
    private int _boostSound = 0;
    private class RBootsPlayerAppearanceAgent implements SpriteAppearanceAgent
    {
	private SpriteAppearanceAgent _oldAgent;
	private boolean _flickerToggle;
	private boolean _flickerOn;
	public void renderSpriteOn(Sprite aSprite,Canvas aCanvas)
	{
	    if((!_flickerOn) || _flickerToggle) {
		_oldAgent.renderSpriteOn(aSprite,aCanvas);
	    }
	    if(_flickerOn) {
		_flickerToggle = (!_flickerToggle);
	    }
	}
	public RBootsPlayerAppearanceAgent(SpriteAppearanceAgent oa)
	{
	    _oldAgent = oa;
	}
	public void setFlicker(boolean f) {
	    _flickerOn = f;
	}
    }


    public static void initPlayerImgs(RBootsView r) {
	thePlayerImgs = new ArrayList<Bitmap>();
	thePlayerImgs.add(r.loadBitmap(R.drawable.player));
	thePlayerImgs.add(r.loadBitmap(R.drawable.playerdead));
	theFlameImgs = new ArrayList<Bitmap>();
	theFlameImgs.add(r.loadBitmap(R.drawable.flame0));
	theFlameImgs.add(r.loadBitmap(R.drawable.flame1));
    }

    private float _accel;
    private boolean _dead;
    private long _flickerTimer;
    private int _life;
    private Sprite _flame;
    public RBootsPlayer(RBootsView h) {
	super(h,thePlayerImgs);
	_pool = h._pool;
	_boostID = h._boost;
	_hurtID = h._hurt;
	SpriteAppearanceAgent a;
	
	a = appearanceAgent();
	setAppearanceAgent(new RBootsPlayerAppearanceAgent(a));
	setBehaviorAgent(new SpriteBehaviorAgent() {
		public void act(Sprite s) {
		    if(!((RBootsView)host()).isPaused()) {
			setVel(vel().x,vel().y + _accel);
			_flame.moveTo(pos().x + 2,pos().y + shape().getHeight());
			_flame.setCurrentFrame((_flame.currentFrame() + 1) % 2);
			if(_flickerTimer > 0) {
			    _flickerTimer -= 20;
			    if(_flickerTimer <= 0) {
				setFlicker(false);
			    }
			}
			if(headAbove(walls().topEdgeFor(pos().x)) ||
			   feetBelow(walls().bottomEdgeFor(pos().x))) {
			    possiblyHurt();
			}
			DefaultBehaviorAgent.instance().act(s);
		    }
		}
	    });

	_flame = new Sprite(host(),theFlameImgs);
	{
	    final SpriteAppearanceAgent oldFlameAgent = _flame.appearanceAgent();
	    _flame.setAppearanceAgent(new SpriteAppearanceAgent() {
		    public void renderSpriteOn(Sprite aSprite,Canvas aCanvas)
		    {
			if(_accel < 0.0) {
			    oldFlameAgent.renderSpriteOn(aSprite,aCanvas);
			}
		    }
		});
	}
	

	_dead = false;
	_flickerTimer = 0;
	_life = 3;
	setHotspot((float)shape().getWidth() / 2.0f,0.0f);
	_flame.setHotspot((float)_flame.shape().getWidth() / 2.0f,0.0f);
	thrustOff();	
    }

    public float accel() {
	return _accel;
    }
    
    private void setAccel(float a) {
	_accel = a;
    }

    public void thrustOn() {
	if(!_dead) {
	    _accel = -0.085f;
	    if(_boostSound == 0) {
		_boostSound = _pool.play(_boostID,1.0f,1.0f,1,-1,1.0f);
	    }
	}
    }

    public void thrustOff() {
	if(!_dead) {
	    _accel = 0.085f;
	}
	if(_boostSound != 0) {
	    _pool.stop(_boostSound);
	    _boostSound = 0;
	}
    }

    public void setFlicker(boolean f) {
	RBootsPlayerAppearanceAgent a = (RBootsPlayerAppearanceAgent)appearanceAgent();
	if(a != null) {
	    a.setFlicker(f);
	}
    }

    public void possiblyHurt() {
	if(!_dead &&
	   _flickerTimer <= 0) {
	    hurt();
	}
    }

    private void hurt() {
	_flickerTimer = 2000;
	setFlicker(true);
	if(_life > 0) {
	    RBootsView v = (RBootsView)host();
	    _pool.play(_hurtID,1.0f,1.0f,1,0,1.0f);
	    _life--;
	    if(v != null) {
		v.setLife(_life);
	    }
	    if(_life <= 0) {
		die();
	    }
	}
    }

    private void die() {
	_dead = true;
	setAccel(0.1f);
	setCurrentFrame(1);
	RBootsView v = (RBootsView)host();
	if(v != null) {
	    v.signalDeath();
	}
    }

    public RBootsWallSprite walls() {
	RBootsView v = (RBootsView)host();
	if(v != null) {
	    return v.walls();
	}
	return null;
    }

    public boolean headAbove(int y) {
	return pos().y < y;
    }

    public boolean feetBelow(int y) {
	return pos().y + shape().getHeight() > y;
    }

}
