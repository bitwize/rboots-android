package net.parodycheck.rboots;

import android.util.*;
import android.graphics.*;
import net.parodycheck.spritecorehc.*;

public class RBootsWallSprite extends Sprite
{
    
    class RBootsWallAppearanceAgent implements SpriteAppearanceAgent {
	public void renderSpriteOn(Sprite aSprite, Canvas aCanvas) {
	    
	}
    }
    class RingBuffer {
	int[] _items;
	int _start;
	int _end;
	RingBuffer(int size) {
	    _items = new int[size];
	    empty();
	}
	void empty() {
	    _start = 0;
	    _end = 1;
	    
	}
	void append(int item) {
	    if(_end == 0) {
		_end = _items.length;
	    }
	    _items[_end - 1] = item;
	    ++_end;
	    _end %= _items.length;
	    if(_end == _start) {
		++_start;
		_start %= _items.length;
	    }
	}

	int ref(int index) {
	    index += _start;
	    index %= _items.length;
	    return _items[index];
	}
    }

    private RingBuffer _topEdges;
    private RingBuffer _bottomEdges;

    static Bitmap theWallImg;
    static void initWallImg(android.app.Activity a) {
	theWallImg = BitmapFactory.decodeResource(a.getResources(),R.drawable.wall);	
    }

    RBootsWallSprite(RBootsView host) {
	super(host,theWallImg);
	_topEdges = new RingBuffer(200);
	_bottomEdges = new RingBuffer(200);
    }


}

