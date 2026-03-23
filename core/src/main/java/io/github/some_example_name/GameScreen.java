package io.github.some_example_name;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    public OrthographicCamera camera;
    public Viewport viewport;
    public ShapeRenderer renderer;
    private Player player;
    private Guard[] guards;
    private Rectangle[] buildings = {new Rectangle(300, 200, 200, 150),
        new Rectangle(700, 600, 200, 150),
        new Rectangle(1000, 200, 200, 150),
        new Rectangle(200, 1000, 200, 150)};

    public static final float WORLD_WIDTH = 1600f;
    public static final float WORLD_HEIGHT = 1200f;

    private PathFinder pathfinder;

    public GameScreen(){
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH,WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();
        renderer = new ShapeRenderer();

        pathfinder = new PathFinder(buildings);

        player = new Player(100, 100);
        guards = new Guard[]{new Guard(530, 370, 45, pathfinder),
            new Guard(1000, 400, 135, pathfinder),
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

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);
        for(Rectangle rt : buildings){
            renderer.rect(rt.x,rt.y,rt.width,rt.height);
        } // x, y, width, height

        for(Guard g : guards){
            g.update(v,buildings,player);
            g.draw(renderer);}

        player.update(v, buildings,guards);
        player.draw(renderer);

//        if (!player.alive) {
//            // you'll need a BitmapFont for this
//            font.draw(batch, "You got caught! Press SPACE to respawn", 250, 320);
//        }

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
