package map;

import elements.Animal;
import movement.Vector2d;

public interface IPositionChangeObserver {
    void positionChanged(Animal movedElement, Vector2d oldPosition, Vector2d newPosition);
}
