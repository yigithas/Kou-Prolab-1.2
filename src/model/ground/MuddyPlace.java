package model.ground;

import config.GameConstants;
import engine.Grid;
import model.robot.Robot;

public class MuddyPlace extends Ground {

	public MuddyPlace() {
		super('C');
	}

	@Override
	public boolean stepOn(Robot robot, Grid grid) {
		if(robot.canFly()) {
			return robot.spendEnergy(GameConstants.CAMUR_ARAZISI_FLY_COST);
		}

		if(robot.canShield()){
			return robot.spendEnergy(GameConstants.CAMUR_ARAZISI_SHIELD_COST);
		}

		return robot.spendEnergy(GameConstants.CAMUR_ARAZISI_DEFAULT_COST);
	}

}
