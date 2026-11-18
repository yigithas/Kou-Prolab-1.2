package model.ground;

import config.GameConstants;
import engine.Grid;
import model.common.Position;
import model.robot.Robot;

public class IceCrust extends Ground {

    public IceCrust() {
        super('I');
    }

    @Override
    public boolean isSlippery() {
        return true;
    }

    @Override
    public boolean stepOn(Robot robot, Grid grid) {
        if (robot.canFly()) {
            return robot.spendEnergy(GameConstants.BUZ_KABUGU_FLY_COST);
        }

        if (robot.canSlide()) {
            if (!robot.spendEnergy(GameConstants.BUZ_KABUGU_SLIDE_COST)) {
                return false;
            }

            Position currentPos = robot.getPosition().next(robot.getDirection());
            robot.moveTo(currentPos);

            while (true) {
                Position nextPos = currentPos.next(robot.getDirection());

                if (!grid.isValidPosition(nextPos) || grid.isOccupiedByRobot(nextPos)) {
                    break;
                }

                Ground nextGround = grid.getGroundAt(nextPos);

                if (nextGround != null && nextGround.isSlippery()) {
                    currentPos = nextPos;
                    robot.moveTo(currentPos);
                } else {
                    break;
                }
            }
            return true;
        }

        return robot.spendEnergy(GameConstants.BUZ_KABUGU_DEFAULT_COST);
    }
}
