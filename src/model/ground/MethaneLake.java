package model.ground;

import config.GameConstants;
import engine.Grid;
import model.robot.Robot;

public class MethaneLake extends Ground {

	public MethaneLake() {
		super('~');
	}

	@Override
	public boolean canLandOn() {
		return false;
	}

	@Override
	public boolean stepOn(Robot robot, Grid grid) {
		if(robot.canFly()) {
			return robot.spendEnergy(GameConstants.METAN_GOLU_FLY_COST);
		}
		
		return false;
	}

}
