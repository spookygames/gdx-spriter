/**
 * Copyright (c) 2015-2016 Spooky Games
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgement in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package net.spookygames.gdx.spriter;

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

import net.spookygames.gdx.spriter.SpriterAnimator;
import net.spookygames.gdx.spriter.data.SpriterData;
import net.spookygames.gdx.spriter.loader.SpriterDataLoader;

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
