// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.loader.SpriterDataLoader;

public class SpriterExample implements ApplicationListener {
	
	static final String greyGuy = "GreyGuy/player.scml";

	// Display stuff
	ShapeRenderer renderer;
	SpriteBatch batch;
	OrthographicCamera camera;

	AssetManager assetManager;
	SpriterAnimator animator = null;

	@Override
	public void create() {
		// Initialize object
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		
		// Load Spriter data
		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver);
		assetManager.setLoader(SpriterData.class, new SpriterDataLoader(resolver));

		assetManager.load(greyGuy, SpriterData.class);
		assetManager.finishLoading();

		SpriterData data = assetManager.get(greyGuy);
		
		// Create animator
		animator = new SpriterAnimator(data.entities.first());
		animator.play("idle");
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();

		animator.update(delta);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		renderer.setProjectionMatrix(camera.combined);

		batch.begin();
		renderer.begin(ShapeType.Line);

		animator.draw(batch, renderer);

		batch.end();
		renderer.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		animator.setPosition(width / 2f, 12f);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		renderer.dispose();
		batch.dispose();
		assetManager.dispose();
	}
	
	public static void main(String[] args) {
		new LwjglApplication(new SpriterExample());
	}

}
