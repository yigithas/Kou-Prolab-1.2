package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.common.Position;
import model.common.RobotStatus;
import model.ground.Ground;
import model.robot.Robot;

public class Grid {
    private final int width;
    private final int height;
    private final Ground[][] cells;
    private final List<Robot> robots;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Ground[height][width];
        this.robots = new ArrayList<>();
    }

    public void setGround(int x, int y, Ground ground) {
        if (isValidCoordinate(x, y)) {
            this.cells[y][x] = ground;
        }
    }

    public Ground getGroundAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }
        return this.cells[pos.getY()][pos.getX()];
    }

    public void addRobot(Robot robot) {
        this.robots.add(robot);
    }

    public boolean isValidPosition(Position pos) {
        if (pos == null) return false;
        return isValidCoordinate(pos.getX(), pos.getY());
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isOccupiedByRobot(Position pos) {
        for (Robot r : robots) {
            if (r.getStatus() != RobotStatus.TESLIM && r.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public Robot getRobotAt(Position pos) {
        for (Robot r : robots) {
            if (r.getStatus() != RobotStatus.TESLIM && r.getPosition().equals(pos)) {
                return r;
            }
        }
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Robot> getRobots() {
        return Collections.unmodifiableList(robots);
    }
}
