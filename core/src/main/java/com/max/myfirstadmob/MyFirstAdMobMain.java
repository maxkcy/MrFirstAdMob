package com.max.myfirstadmob;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyFirstAdMobMain extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture image;
	private Sprite sprite;
	private Sprite background;
	float x , y;
	OrthographicCamera cam;
	Viewport viewPort;
	IActivityRequestHandler myRequestHandler;

	public MyFirstAdMobMain(IActivityRequestHandler myRequestHandler) {
		this.myRequestHandler = myRequestHandler;
	}

	public MyFirstAdMobMain() {
	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		image = new Texture("badlogic.png");

		sprite = new Sprite(image);
		sprite.setScale(.5f);
		sprite.setOrigin(0,0);
		cam = new OrthographicCamera();
		viewPort = new FitViewport(400, 800, cam);
		cam.position.set(viewPort.getWorldWidth()/2, viewPort.getWorldHeight()/2, 0);
		x = viewPort.getWorldWidth()/2 - (sprite.getWidth())*sprite.getScaleX()/2;
		y = viewPort.getWorldHeight()/2 - (sprite.getHeight())*sprite.getScaleY()/2;
		background = new Sprite(new Texture(Gdx.files.internal("background.jpg")));
		background.setBounds(0, 0, viewPort.getWorldWidth(), viewPort.getWorldHeight());
	}


	boolean showingads = true;
	boolean click;
	@Override
	public void render() {
		Gdx.gl.glClearColor(.111f, .222f, .333f, .444f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();
		x -= Gdx.input.getAccelerometerX();
		y -= Gdx.input.getAccelerometerY();

		if (y < 0){ y = 0;}
		if (y > viewPort.getWorldHeight() - sprite.getHeight()*sprite.getScaleY()){ y = viewPort.getWorldHeight() - sprite.getHeight()*sprite.getScaleY();}
		if (x < 0){x = 0;}
		if (x > viewPort.getWorldWidth() - sprite.getWidth()*sprite.getScaleX()){x = viewPort.getWorldWidth() - sprite.getWidth()*sprite.getScaleX();}
		viewPort.apply();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		background.draw(batch);
		sprite.setPosition(x, y);
		sprite.draw(batch);
		batch.end();


		if (Gdx.input.isTouched()){
			if (!click){
				if(showingads){
					showingads = false;
					myRequestHandler.showAds(showingads);
				}else{
					showingads = true;
					myRequestHandler.showAds(showingads);
				}
				//justTouched
				click = true;
			}
		}

		if(!Gdx.input.isTouched() && click){
			//justUnTouched
			click = false;
		}
	}

	@Override
	public void resize(int width, int height) {
		viewPort.update(width, height);
		super.resize(width, height);
	}
	@Override
	public void dispose() {
		batch.dispose();
		image.dispose();
	}
}