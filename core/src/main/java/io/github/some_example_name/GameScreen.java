package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    public OrthographicCamera camera;
    public Viewport viewport;
    public ShapeRenderer renderer;
    private Player player;
    private Guard[] guards;
    private Rectangle[] buildings = {new Rectangle(100, 200, 400, 150),
        new Rectangle(750, 200, 500, 150),
        new Rectangle(100, 500, 200, 150),
        new Rectangle(600, 600, 200, 150),
        new Rectangle(100, 1000, 300, 150),
        new Rectangle(700, 850, 50, 400),
        new Rectangle(1200, 900, 200, 200),
        new Rectangle(1200, 600, 200, 50)
    };

    public static final float WORLD_WIDTH = 1600f;
    public static final float WORLD_HEIGHT = 1200f;

    public BitmapFont font;
    public SpriteBatch batch;

    private PathFinder pathfinder;

    public GameScreen(){
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH,WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();
        renderer = new ShapeRenderer();

        batch = new SpriteBatch();
        font = new BitmapFont();

        pathfinder = new PathFinder(buildings);

        player = new Player(100, 100);
        guards = new Guard[]{new Guard(530, 370, 45, pathfinder),
            new Guard(1000, 400, 90, pathfinder),
            new Guard(430, 970, 315, pathfinder),
            new Guard(1000, 1000, 225, pathfinder),
        };
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        renderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glLineWidth(10);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.DARK_GRAY);

        for(Rectangle rt : buildings){
            renderer.rect(rt.x,rt.y,rt.width,rt.height);
        } // x, y, width, height
        renderer.end();

        Gdx.gl.glLineWidth(1);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        for(Guard g : guards){
            g.update(v,buildings,player);
            g.draw(renderer);}

        player.update(v, buildings,guards);
        player.draw(renderer);

        if (!player.alive) {
            batch.begin();
            font.draw(batch, "You got caught! Press SPACE to respawn", 70, 30);
            batch.end();
        }

        renderer.end();
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(i, i1);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
