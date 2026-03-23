package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.Node;

public class PathFinder {

    private Node[][] nodes;
    private int cols, rows;

    public static final int TILE_SIZE = 10;
    public static final int COLS = 160;
    public static final int ROWS = 120;

    public PathFinder(Rectangle[] obstacles) {
        cols = COLS;
        rows = ROWS;

        nodes = new Node[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                boolean wall = isBlockedByObstacle(i, j, obstacles);
                nodes[i][j] = new Node(i, j, wall);
            }
        }
    }

    private boolean isBlockedByObstacle(int col, int row, Rectangle[] obstacles) {

        float wx = col * TILE_SIZE + TILE_SIZE / 2f;
        float wy = row * TILE_SIZE + TILE_SIZE / 2f;

        for (Rectangle rect : obstacles) {
            // expand rect by padding
            float padding = 15f;
            if (wx >= rect.x - padding &&
                wx <= rect.x + rect.width + padding &&
                wy >= rect.y - padding &&
                wy <= rect.y + rect.height + padding) {
                return true;
            }
        }
        return false;
    }

    public Array<Node> findPath(float startWorldX, float startWorldY,
                                float endWorldX,   float endWorldY) {


        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++) {
                nodes[i][j].f = nodes[i][j].g = nodes[i][j].h = 0;
                nodes[i][j].previous = null;
            }

        Array<Node> openSet   = new Array<>();
        Array<Node> closedSet = new Array<>();

        int startCol = Math.min((int)(startWorldX / TILE_SIZE), COLS - 1);
        int startRow = Math.min((int)(startWorldY / TILE_SIZE), ROWS - 1);
        int endCol   = Math.min((int)(endWorldX   / TILE_SIZE), COLS - 1);
        int endRow   = Math.min((int)(endWorldY   / TILE_SIZE), ROWS - 1);

        startCol = Math.max(0, startCol);
        startRow = Math.max(0, startRow);
        endCol   = Math.max(0, endCol);
        endRow   = Math.max(0, endRow);

        Node startNode = nodes[startCol][startRow];
        Node endNode   = nodes[endCol][endRow];

        openSet.add(startNode);

        while (openSet.size > 0) {
            int winnerIdx = 0;
            for (int i = 1; i < openSet.size; i++)
                if (openSet.get(i).f < openSet.get(winnerIdx).f)
                    winnerIdx = i;

            Node current = openSet.get(winnerIdx);

            if (current == endNode)
                return reconstructPath(current);

            openSet.removeIndex(winnerIdx);
            closedSet.add(current);

            for (Node neighbour : getNeighbours(current)) {
                if (neighbour == null || neighbour.isWall || closedSet.contains(neighbour, true))
                    continue;

                float tempG = current.g + 1;

                if (openSet.contains(neighbour, true)) {
                    if (tempG < neighbour.g)
                        neighbour.g = tempG;
                } else {
                    neighbour.g = tempG;
                    openSet.add(neighbour);
                }

                neighbour.h = heuristic(neighbour, endNode);
                neighbour.f = neighbour.g + neighbour.h;
                neighbour.previous = current;
            }
        }

        return new Array<>();
    }

    private Array<Node> getNeighbours(Node node) {
        Array<Node> neighbours = new Array<>();
        int i = node.col, j = node.row;
        if (j > 0)        neighbours.add(nodes[i][j - 1]); // top
        if (j < rows - 1) neighbours.add(nodes[i][j + 1]); // bottom
        if (i > 0)        neighbours.add(nodes[i - 1][j]); // left
        if (i < cols - 1) neighbours.add(nodes[i + 1][j]); // right
        return neighbours;
    }

    private float heuristic(Node a, Node b) {
        float dc = b.col - a.col;
        float dr = b.row - a.row;
        return (float) Math.sqrt(dc * dc + dr * dr);
    }

    private Array<Node> reconstructPath(Node end) {
        Array<Node> path = new Array<>();
        Node temp = end;
        while (temp != null) {
            path.add(temp);
            temp = temp.previous;
        }
        path.reverse(); // so index 0 = start, last = end
        return path;
    }

}
