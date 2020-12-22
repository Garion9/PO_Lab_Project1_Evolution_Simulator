package visualization;

import elements.Plant;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlantModel {
    private final Plant plant;
    private final Rectangle plantView;

    public PlantModel(Plant plant, double size) {
        this.plant = plant;
        plantView = new Rectangle(size, size, Color.DARKGREEN);
    }

    public Plant getPlant() {
        return plant;
    }

    public Rectangle getPlantView() {
        return plantView;
    }

}
