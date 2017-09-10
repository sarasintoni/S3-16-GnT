package com.unibo.s3.main_system;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.unibo.s3.BasicCameraInputController;
import com.unibo.s3.CameraInputControllerKeymap;
import com.unibo.s3.InputProcessorAdapter;

import static com.unibo.s3.main_system.util.ScaleUtils.getPixelsPerMeter;

public abstract class AbstractMainApplication extends ApplicationAdapter implements InputProcessorAdapter {

    /*rendering stuff*/
    protected ShapeRenderer shapeRenderer;
    protected BitmapFont font;
    protected SpriteBatch textBatch;

    /*camera*/
    protected OrthographicCamera cam;
    private float cameraSpeed = 20f;
    private float cameraZoomSpeed = 0.1f;
    private float minZoom = 1.5f;
    private float maxZoom = 50f;
    private float cameraViewportWidthMeters = 30f;
    private BasicCameraInputController camController;
    private CameraInputControllerKeymap camKeymap;

    protected boolean pause = false;

    protected abstract void doRender();

    protected abstract void doCustomRender();

    protected abstract void doUpdate(float delta);

    @Override
    public void create () {
        shapeRenderer = new ShapeRenderer();
        textBatch = new SpriteBatch();
        font = new BitmapFont();
        initCamera();
    }

    @Override
    public void render () {
        //handleCameraInput();
        camController.handleInput();
        cam.update();
        Gdx.graphics.setTitle("camera@"+cam.position+"("+cam.zoom+")");

        /*clear screen & opengl buffers*/
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*get time elapsed since previous frame was rendered*/
        final float dt = Gdx.graphics.getDeltaTime();

        if (!pause) {
            GdxAI.getTimepiece().update(dt);
            doUpdate(dt);
        }

        /*set camera projection matrix to all renderers*/
        shapeRenderer.setProjectionMatrix(cam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        doRender();
        shapeRenderer.end();

        doCustomRender();
    }

    @Override
    public void dispose () {
        shapeRenderer.dispose();
        textBatch.dispose();
    }

    @Override
    public void resize(int newWidth, int newHeight){
        //cam.setToOrtho(false, cameraViewportWidthMeters,cameraViewportWidthMeters * newWidth / (float)newHeight);

        cam.viewportWidth = cameraViewportWidthMeters *  getPixelsPerMeter();
        cam.viewportHeight = (cameraViewportWidthMeters *  getPixelsPerMeter()) * (((float)newHeight) / newWidth);
        cam.update();
        textBatch.getProjectionMatrix().setToOrtho2D(0, 0, newWidth, newHeight);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.P) {
            pause = !pause;
        }
        return false;
    }

    private void initCamera() {

        final float aspectRatio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        // *  getPixelsPerMeter()
        cam = new OrthographicCamera(cameraViewportWidthMeters,
                cameraViewportWidthMeters * aspectRatio);

        camKeymap = new CameraInputControllerKeymap(Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT, Keys.Q, Keys.R);

        camController = new BasicCameraInputController(cam, cameraSpeed, cameraZoomSpeed,
                minZoom, maxZoom, camKeymap);

        cam.position.set(cam.viewportWidth / 2 , cam.viewportHeight / 2,0);
        cam.zoom = 5;
        cam.update();
    }

    /**
     *
     * @param screenPosition a {@link Vector2} in screen space to be converted to world coordinate.
     * @return the converted {@link Vector2} in world space.
     */
    public Vector2 screenToWorld(Vector2 screenPosition) {
        Vector3 u = new Vector3(screenPosition.x, screenPosition.y, 0);
        cam.unproject(u);
        return new Vector2(u.x, u.y);
    }

    /**
     *
     * @param worldPosition a {@link Vector2} in world space to be converted to screen coordinate.
     * @return the converted {@link Vector2} in screen space.
     */
    public Vector2 worldToScreen(Vector2 worldPosition) {
        Vector3 p = new Vector3(worldPosition.x, worldPosition.y, 0);
        cam.project(p);
        return new Vector2(p.x, p.y);
    }

}
