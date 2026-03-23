package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Guard {
    public float x,y;
    public float startX,startY;
    public float angle;
    public float speed;
    public float maxspeed = 130f;
    public float length = 30f;


    public float fovRange = 200f;
    public float FOV = 120f;
    public float FOVrotatingAngle = 75f;
    private float rotationSpeed = 45f;
    private float startingAngle;
    float minAngle;
    float maxAngle;
    private boolean rotatingRight = true;

    public State state = State.SCOUTING;

    private PathFinder pathfinder;
    private Array<Node> path;
    private int pathIndex = 0;

    public Guard(float x, float y, float angle, PathFinder pathfinder){
        this.x= x;
        this.y= y;
        startX= x;
        startY= y;
        this.angle= angle;
        startingAngle = angle;
        this.pathfinder = pathfinder;
    }

    public void update(float delta, Rectangle[] buildings,Player player) {
        float newX = x;
        float newY = y;
        if(state.equals(State.CHASING)) {
            speed = maxspeed;
            angle = (float) Math.toDegrees(Math.atan2(player.y - y, player.x - x));

            float radians = (float) Math.toRadians(angle);
            newX += (float) Math.cos(radians) * speed * delta;
            newY += (float) Math.sin(radians) * speed * delta;

            if(!canSeePlayer(player) || !player.alive){
                path = pathfinder.findPath(x, y, startX, startY);
                pathIndex = 0;
                state = State.RETURNING;
            }

        }else if(state.equals(State.SCOUTING)){
            minAngle = startingAngle - FOVrotatingAngle;
            maxAngle = startingAngle + FOVrotatingAngle;
            if (rotatingRight) {
                angle += rotationSpeed * delta;
                if (angle >= maxAngle) rotatingRight = false;
            } else {
                angle -= rotationSpeed * delta;
                if (angle <= minAngle) rotatingRight = true;
            }

            if(canSeePlayer(player) && player.alive ){state = State.CHASING;}

        } else if (state.equals(State.RETURNING)) {
            if (path != null && pathIndex < path.size) {
                Node target = path.get(pathIndex);
                float targetX = target.col * PathFinder.TILE_SIZE + PathFinder.TILE_SIZE / 2f;
                float targetY = target.row * PathFinder.TILE_SIZE + PathFinder.TILE_SIZE / 2f;

                float returnAngle = (float) Math.toDegrees(Math.atan2(targetY - y, targetX - x));
                float radians = (float) Math.toRadians(returnAngle);
                newX += (float) Math.cos(radians) * speed * delta;
                newY += (float) Math.sin(radians) * speed * delta;

                this.angle = returnAngle; // update facing direction too

                float dist = (float) Math.sqrt((targetX - x) * (targetX - x) + (targetY - y) * (targetY - y));
                if (dist < 5f) pathIndex++;
                if(canSeePlayer(player) && player.alive ){state = State.CHASING;}

            } else {
                x = startX;
                y = startY;
                state = State.SCOUTING;
            }
        }
// this now correctly applies to ALL states including RETURNING
        if (!buildingCollides(newX, y, buildings)) x = newX;
        if (!buildingCollides(x, newY, buildings)) y = newY;

    }

    public void draw(ShapeRenderer sr){
        if (state.equals(State.CHASING)){
            sr.setColor(Color.RED);
        } else if (state.equals(State.SCOUTING)) {
            sr.setColor(Color.YELLOW);
        }else {
            sr.setColor(Color.ORANGE);
        }

        float renderX = x-(length/2f);
        float renderY = y-(length/2f);
        sr.rect(renderX,renderY,length,length);

        float noseX = x + (float)Math.cos(Math.toRadians(angle)) * (length + 8);
        float noseY = y + (float)Math.sin(Math.toRadians(angle)) * (length + 8);

        sr.line(x, y, noseX, noseY);

        float leftAngle  = angle + FOV / 2f;
        float rightAngle = angle - FOV / 2f;
        float leftX  = x + (float)Math.cos(Math.toRadians(leftAngle))  * fovRange;
        float leftY  = y + (float)Math.sin(Math.toRadians(leftAngle))  * fovRange;
        float rightX = x + (float)Math.cos(Math.toRadians(rightAngle)) * fovRange;
        float rightY = y + (float)Math.sin(Math.toRadians(rightAngle)) * fovRange;

        sr.arc(x,y,fovRange,rightAngle,FOV);
    }
    public enum State {
        SCOUTING,
        CHASING,
        RETURNING
    }
    private boolean buildingCollides(float cx, float cy, Rectangle[] buildings) {
        for (Rectangle building : buildings) {
            if ((cx + (length/2f)) > building.x &&
                (cx - (length/2f)) < building.x + building.width &&
                (cy + (length/2f)) > building.y &&
                (cy - (length/2f)) < building.y + building.height) {
                return true;
            }
        }
        return false;
    }
    public boolean canSeePlayer(Player player) {
        float dx = player.x - x;
        float dy = player.y - y;
        float distance = (float) (Math.sqrt(dx * dx + dy * dy)- player.radius);
        if (distance > fovRange) return false;

        float angleToPlayer = (float) Math.toDegrees(Math.atan2(dy, dx));
        float angleDiff = Math.abs(angleToPlayer - angle) % 360;
        if (angleDiff > 180) angleDiff = 360 - angleDiff; // normalize to 0-180
        return (angleDiff <= FOV / 2f);
    }

}
