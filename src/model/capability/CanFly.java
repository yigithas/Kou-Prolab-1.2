package model.capability;

public interface CanFly {
    default boolean canFly() {
        return true;
    }
}
