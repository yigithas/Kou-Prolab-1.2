package model.robot;

import model.capability.CanShield;
import model.common.Position;

public class ShieldedRobot extends Robot implements CanShield {

	public ShieldedRobot(int id, Position position, int energy) {
		super(id, "ZIRHLI", position, energy);
	}

	@Override
	public boolean canShield() {
		return CanShield.super.canShield();
	}

}
