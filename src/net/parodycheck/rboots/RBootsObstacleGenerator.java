package net.parodycheck.rboots;

import net.parodycheck.spritecorehc.*;
import java.util.*;
import android.util.*;

class RBootsObstacleGenerator {
    RBootsView _view;
    float _nextPos;
    float _width;
    float _minDist;
    RBootsObstacle _o;
    RBootsObstacleGenerator(RBootsView v) {
	_view = v;
	_width = (float)v.getWidth();
	_nextPos = 0.0f;
	_minDist = 500.0f;

    }

    void update(float pos) {
	int i = _view.randomSource().nextInt((int)_minDist);
	if(pos >= _nextPos) {
	    newObstacle(pos + _width);
	    _nextPos += _minDist + i;
	}
    }

    void newObstacle(float pos) {
	try {
	    _o = new RBootsObstacle(_view,pos);
	}
	catch(Exception e) {
	    Log.i("RBootsObstacleGenerator",e.toString());
	}
    }
}