package net.parodycheck.rboots;

import java.util.*;
import android.util.*;
import android.graphics.*;
import net.parodycheck.spritecorehc.*;

public class RBootsPlayer extends Sprite
{

    private static Bitmap thePlayerImg;
    private static ArrayList<Bitmap> theFlameImgs;

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

    public static void initPlayerImgs(android.app.Activity a) {
	thePlayerImg = BitmapFactory.decodeResource(a.getResources(),R.drawable.player);
	theFlameImgs = new ArrayList<Bitmap>();
	theFlameImgs.add(BitmapFactory.decodeResource(a.getResources(),R.drawable.flame0));
	theFlameImgs.add(BitmapFactory.decodeResource(a.getResources(),R.drawable.flame1));
    }

    private float _accel;
    private boolean _dead;
    private long _flickerTimer;
    private int _life;
    public RBootsPlayer(RBootsView host) {
	super(host,thePlayerImg);
	SpriteAppearanceAgent a;
	a = appearanceAgent();
	setAppearanceAgent(new RBootsPlayerAppearanceAgent(a));
	setBehaviorAgent(new SpriteBehaviorAgent() {
		public void act(Sprite s) {
		    setVel(vel().x,vel().y + _accel);
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
		}
	    });
	_dead = false;
	_flickerTimer = 0;
	_life = 3;
	setHotspot((float)shape().getWidth() / 2,0.0f);
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
	}
    }

    public void thrustOff() {
	if(!_dead) {
	    _accel = 0.085f;
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