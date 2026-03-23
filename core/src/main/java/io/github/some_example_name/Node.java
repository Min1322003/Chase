package io.github.some_example_name;

public class Node {
    public int col, row;
    public float f, g, h;
    public Node previous;
    public boolean isWall;

    public Node(int col, int row, boolean isWall) {
        this.col = col;
        this.row = row;
        this.isWall = isWall;
        this.f = this.g = this.h = 0;
        this.previous = null;
    }
}
