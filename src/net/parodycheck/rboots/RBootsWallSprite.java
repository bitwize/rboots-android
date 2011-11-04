package net.parodycheck.rboots;

import android.util.*;
import android.graphics.*;
import net.parodycheck.spritecorehc.*;

public class RBootsWallSprite extends Sprite
{
    public static final int WALL_SEGMENT_WIDTH = 8;

    private class RingBuffer {
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
    private int _wallTop;
    private int _wallTopFirstDeriv;
    private int _wallTopSecondDeriv;
    private int _wallDist;
    private double _xPos;
    private double _leftEdge;
    private Sprite _bg;


    private static Bitmap theWallImg;
    public static void initWallImg(android.app.Activity a) {
	theWallImg = BitmapFactory.decodeResource(a.getResources(),R.drawable.wall);
    }

    public RBootsWallSprite(RBootsView host,Sprite bg) {
	super(host,theWallImg);
	_bg = bg;
	_topEdges = new RingBuffer(200);
	_bottomEdges = new RingBuffer(200);
	SpriteAppearanceAgent a =
	    new SpriteAppearanceAgent() {
		public void renderSpriteOn(Sprite aSprite,Canvas aCanvas) {
		    PointF p = pos();
		    int le = (int)_leftEdge;
		    int i = 0;
		    while(le < aCanvas.getWidth() + WALL_SEGMENT_WIDTH) {
			aCanvas.drawBitmap(shape(),le,_topEdges.ref(i) - shape().getHeight(),aSprite.host().paint());
			aCanvas.drawBitmap(shape(),le,_bottomEdges.ref(i),aSprite.host().paint());
			i++;
			le += WALL_SEGMENT_WIDTH;
		    }
		    
		}
	    };

	SpriteBehaviorAgent b =
	    new SpriteBehaviorAgent() {
		public void act(Sprite aSprite) {
		    while(_xPos > pos().x) {
			_topEdges.append(nextWallTop());
			_bottomEdges.append(nextWallBottom());
			_xPos -= WALL_SEGMENT_WIDTH;
		    }
		    _leftEdge = pos().x;
		    while(_leftEdge < -WALL_SEGMENT_WIDTH) {
			_leftEdge += WALL_SEGMENT_WIDTH;
		    }
		    _bg.moveTo(_bg.pos().x + (vel().x / 2.0f),
			       _bg.pos().y + (vel().y / 2.0f));
		}
	    };
	setAppearanceAgent(a);
	setBehaviorAgent(b);
	_wallTop = 15;
	_wallTopFirstDeriv = 0;
	_wallTopSecondDeriv = 0;
	_wallDist = 200;
	_xPos = 0.0;
	_leftEdge = 0.0;
	for(int i=0;i<200;i++) {
	    _topEdges.append(nextWallTop());
	    _bottomEdges.append(nextWallBottom());
	}
    }

    private int nextWallTop()
    {
	int i = host().randomSource().nextInt();
	if(_wallTop < 10) {
	    _wallTopFirstDeriv = 1;
	}
	if(_wallTop > 90) {
	    _wallTopFirstDeriv = -1;
	}
	_wallTop += _wallTopFirstDeriv;
	_wallTopFirstDeriv += _wallTopSecondDeriv;
	if((i & 1024) != 0)
	{
		_wallTopSecondDeriv = (i & 3);
	}
	else
	{
		_wallTopSecondDeriv = -(i & 3);
	}
	return _wallTop;
    }
    private int nextWallBottom()
    {
	return _wallTop + _wallDist;
    }

    public int topEdgeFor(float x) {
	int idx = (int)(x + _leftEdge);
	return _topEdges.ref(idx / WALL_SEGMENT_WIDTH);
    }
    public int bottomEdgeFor(float x) {
	int idx = (int)(x + _leftEdge);
	return _bottomEdges.ref(idx / WALL_SEGMENT_WIDTH);
    }

}

