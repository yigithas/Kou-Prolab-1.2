package model.ground;

import config.GameConstants;
import engine.Grid;
import model.common.Position;
import model.robot.Robot;

public class CraterRock extends Ground {

	public CraterRock() {
		super('K');
	}

	@Override
	public boolean canLandOn() {
		return false;
	}

	@Override
	public boolean stepOn(Robot robot, Grid grid) {
		if(robot.canFly()) {
			if(robot.spendEnergy(GameConstants.KRATERLI_KAYA_FLY_COST)) {
				return true;
			}
			return false;
		}
		
		if(robot.canJump()) {
			Position landPosition = robot.getPosition().next(robot.getDirection()).next(robot.getDirection());
			
			if(!grid.isValidPosition(landPosition) || grid.isOccupiedByRobot(landPosition))
				return false;
			
			Ground landGround = grid.getGroundAt(landPosition);
			
			if(landGround == null || !landGround.canLandOn())
				return false;
			
			if(robot.spendEnergy(GameConstants.KRATERLI_KAYA_JUMP_COST)) {
				robot.moveTo(landPosition);
				
				if(landGround.isDeliveryRamp()) {
					landGround.stepOn(robot, grid);
				}
				return true;
			}
			
			return false;
		}
		
		return robot.spendEnergy(GameConstants.KRATERLI_KAYA_DEFAULT_COST);
	}

}
