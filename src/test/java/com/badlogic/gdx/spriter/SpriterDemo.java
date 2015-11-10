// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.spriter.demo.SpriterDemoApp;

public class SpriterDemo {

	public static void main(String[] args) throws Exception {

		ApplicationListener listener = new SpriterDemoApp();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1024;
		config.height = 768;
		
		config.x = 2250;
		
		
		new LwjglApplication(listener, config);
	}
}
