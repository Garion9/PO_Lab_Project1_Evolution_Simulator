package elements;

import movement.Vector2d;
import visualization.PlantModel;

public class Plant extends AbstractWorldElement {
    private final int energyValue;
    private PlantModel model;

    public Plant(Vector2d position, int energyValue) {
        this.position = position;
        this.energyValue = energyValue;
    }

    public int getEnergyValue() {
        return this.energyValue;
    }

    public void attachModel(PlantModel model) {
        this.model = model;
    }

    public PlantModel getModel() {
        return model;
    }
}
