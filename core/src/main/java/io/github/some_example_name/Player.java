package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    public float x, y;
    public float checkpointX, checkpointY;
    public boolean alive ;
    public float angle;
    public float speed = 150f;
    public float radius = 15f;

    public Player(float x, float y) {
        checkpointX = x;
        checkpointY = y;
        this.x = x;
        this.y = y;
        this.angle = 0f;
        alive = true;
    }

    public void update(float delta, Rectangle[] buildings, Guard[] guards) {

        if(!alive) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE))  respawn();
        } else {
            float newX = x;
            float newY = y;

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) )  newX -= speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) newX += speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.UP))    newY += speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  newY -= speed * delta;

            if(guardCollides(newX,newY,guards)) {
                die();
                return;
            }

            if (!buildingCollides(newX, y, buildings)) x = newX;
            if (!buildingCollides(x, newY, buildings)) y = newY;

            boolean up    = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            if (up && right)       angle = 45;
            else if (up && left)   angle = 135;
            else if (down && left) angle = 225;
            else if (down && right)angle = 315;

            else if (right) angle = 0;
            else if (up)    angle = 90;
            else if (left)  angle = 180;
            else if (down)  angle = 270;
        }
    }
    private boolean guardCollides(float cx, float cy, Guard[] guards) {


        for (Guard g : guards) {
            if ((cx + radius) > (g.x - 15f) &&
                (cx - radius) < (g.x + 15f) &&
                (cy + radius) > (g.y - 15f) &&
                (cy - radius) < (g.y + 15f)) {
                return true;
            }
        }
        return false;
    }
    private boolean buildingCollides(float cx, float cy, Rectangle[] buildings) {
        for (Rectangle building : buildings) {
            if (cx + radius > building.x &&
                cx - radius < building.x + building.width &&
                cy + radius > building.y &&
                cy - radius < building.y + building.height) {
                return true;
            }
        }
        return false;
    }

    public void draw(ShapeRenderer sr) {
        if(alive){
            sr.setColor(Color.GREEN);
        }else{sr.setColor(Color.GRAY);
        }
        sr.circle(x, y, radius);

        float noseX = x + (float)Math.cos(Math.toRadians(angle)) * (radius + 8);
        float noseY = y + (float)Math.sin(Math.toRadians(angle)) * (radius + 8);
        sr.line(x, y, noseX, noseY);

    }

    public void saveCheckpoint() {
        checkpointX = x;
        checkpointY = y;
    }

    public void die() {
        alive = false;
        x = checkpointX;
        y = checkpointY;
    }

    public void respawn() {
        alive = true;
    }
}
