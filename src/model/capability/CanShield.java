package model.capability;

public interface CanShield {
    default boolean canShield() {
        return true;
    }
}
