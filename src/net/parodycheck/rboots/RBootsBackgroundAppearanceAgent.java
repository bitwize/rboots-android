package net.parodycheck.rboots;


import android.content.Context;
import android.util.*;
import android.view.*;
import android.graphics.*;

import net.parodycheck.spritecorehc.*;

public class RBootsBackgroundAppearanceAgent
    implements SpriteAppearanceAgent
{
    RBootsBackgroundAppearanceAgent() {}

    public void renderSpriteOn(Sprite aSprite,Canvas aCanvas)
    {
	Bitmap shape = aSprite.shape();
	SpriteCoreView v = aSprite.host();
	android.graphics.Paint p = v.paint();
	int w = aCanvas.getWidth();
	int h = aCanvas.getHeight();
	Log.i("RBootsBackgroundAppearanceAgent","canvas: " + Integer.toString(w) + " " + Integer.toString(h));

	float px = aSprite.pos().x;
	while(px < 0) {px += aSprite.width();}
	float py = aSprite.pos().y;
	while(py < 0) {py += aSprite.height();}
	Log.i("RBootsBackgroundAppearanceAgent","sprite: " + Integer.toString(aSprite.width()) + " " + Integer.toString(aSprite.height()));
	PointF offset = new PointF((float)((int)px % aSprite.width()) - aSprite.width(), 
				   (float)((int)py % aSprite.height()) - aSprite.height());
	for(float j = offset.y;j <= h;j += aSprite.height()) {
	    for(float i = offset.x; i <= w;i += aSprite.width()) {
		Log.i("RBootsBackgroundAppearanceAgent","drawing " + Float.toString(i) + " " + Float.toString(j));
		aCanvas.drawBitmap(shape,i,j,p);
	    }
	}
	
    }
}