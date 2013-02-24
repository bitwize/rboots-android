package net.parodycheck.rboots;

import net.parodycheck.spritecorehc.*;
import java.util.*;
import android.util.*;
import android.graphics.*;

class RBootsObstacle extends Sprite
{
    PointF _initPos;
    int _height;
    public RBootsObstacle(RBootsView v,float xPos) {
	super(v,RBootsWallSprite.wallImg());
	_height = 16 + v.randomSource().nextInt(32);
	_initPos = new PointF();
	_initPos.x = xPos;
	moveTo(v.walls().pos().x + _initPos.x,0.0f);
	int wallMin = v.walls().topEdgeFor(pos().x);
	int wallMax = v.walls().bottomEdgeFor(pos().x) - _height;
	_initPos.y =  (float)((v.randomSource().nextInt(wallMax - wallMin)) + wallMin);
	moveTo(v.walls().pos().x + _initPos.x,_initPos.y);
	setAppearanceAgent
	    (new SpriteAppearanceAgent() {
		    public void renderSpriteOn(Sprite aSprite,Canvas aCanvas)
		    {
			Bitmap shape = aSprite.shape();
			PointF pos = aSprite.pos();
			PointF hs = aSprite.hotspot();
			Rect src;
			Rect dest;
			src = new Rect(0,0,shape.getWidth(),_height);
			dest = new Rect(src);
			dest.offset((int)(pos.x - hs.x),(int)(pos.y - hs.y));
			SpriteCoreView v = aSprite.host();
			android.graphics.Paint p = v.paint();
			try {
			    aCanvas.drawBitmap(shape,src,dest,null);
			}
			catch(Exception e)
			    {
			    }

		    }
		});
	setBehaviorAgent(new SpriteBehaviorAgent()
	    {
		public void act(Sprite aSprite) {
		    RBootsView v = (RBootsView)host();
		    if(!v.isPaused()) {
			moveTo(v.walls().pos().x + _initPos.x,_initPos.y);
			if(isTouching(v.player())) {
			    v.player().possiblyHurt();
			}
		    }
		}
		    
	    });
    }
    public boolean isTouching(Sprite anotherSprite) {
	float left1,top1,right1,bottom1;
	float left2,top2,right2,bottom2;
	boolean step1,step2;
	left1 = pos().x - hotspot().x;
	top1 = pos().y - hotspot().y;
	right1 = left1 + shape().getWidth();
	bottom1 = top1 + _height;
	left2 = anotherSprite.pos().x - anotherSprite.hotspot().x;
	top2 = anotherSprite.pos().y - anotherSprite.hotspot().y;
	right2 = left2 + anotherSprite.shape().getWidth();
	bottom2 = top2 + anotherSprite.shape().getHeight();
	if(left1 <= left2) {
		if(left2 < right1) step1 = true; else step1 = false;
	}
	else {
		if(right2 > left1) step1 = true; else step1 = false;
	}
	if(top1 <= top2) {
		if(top2 < bottom1) step2 = true; else step2 = false;
	}
	else {
		if(bottom2 > top1) step2 = true; else step2 = false;
	}
	return step1 && step2;
    }


}