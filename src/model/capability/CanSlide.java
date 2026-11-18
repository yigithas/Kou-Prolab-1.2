package model.capability;

public interface CanSlide {
    default boolean canSlide() {
        return true;
    }
}
