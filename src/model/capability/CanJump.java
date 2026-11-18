package model.capability;

public interface CanJump {
    default boolean canJump() {
        return true;
    }
}
