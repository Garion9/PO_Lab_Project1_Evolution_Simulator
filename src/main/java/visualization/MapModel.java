package visualization;

import elements.Animal;
import elements.Plant;
import javafx.scene.layout.StackPane;
import map.IPositionChangeObserver;
import map.WorldMap;
import movement.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class MapModel implements IPositionChangeObserver {
    private final List<AnimalModel> animalModels = new ArrayList<AnimalModel>();
    private final List<PlantModel> plantModels = new ArrayList<PlantModel>();
    private final StackPane[][] background;
    private final double cellSize;

    public MapModel(WorldMap map, int startingEnergy, StackPane[][] background, double cellSize) {
        this.cellSize = cellSize;
        for (Plant plant : map.getPlantsOnMap().values()) {
            PlantModel plantModel = new PlantModel(plant, cellSize);
            plantModels.add(plantModel);
        }

        for (List<Animal> animals : map.getAnimalsOnMap().values()) {
            for (Animal animal : animals) {
                AnimalModel animalModel = new AnimalModel(animal, startingEnergy, cellSize);
                animal.attachModel(animalModel);
                animalModels.add(animalModel);
            }
        }
        this.background = background;
    }

    public void addAnimal(Animal animal, Vector2d position) {
        if (!animalModels.contains(animal.getModel())) {
            animalModels.add(animal.getModel());
        }
        background[position.y][position.x].getChildren().add(animal.getModel().getAnimalView());
    }

    public void removeAnimal(Animal animal, Vector2d position) {
        background[position.y][position.x].getChildren().removeAll(animal.getModel().getAnimalView());
    }

    public void addPlant(Plant plant) {
        PlantModel plantModel = new PlantModel(plant, cellSize);
        plant.attachModel(plantModel);
        plantModels.add(plantModel);
        background[plant.getPosition().y][plant.getPosition().x].getChildren().add(plantModel.getPlantView());
    }
    public void removePlant(Plant plant) {
        plantModels.remove(plant.getModel());
        System.out.println();
        background[plant.getPosition().y][plant.getPosition().x].getChildren().remove(plant.getModel().getPlantView());
    }

    public double getCellSize() {
        return cellSize;
    }

    @Override
    public void positionChanged(Animal movedElement, Vector2d oldPosition, Vector2d newPosition) {
        removeAnimal(movedElement, oldPosition);
        addAnimal(movedElement, newPosition);
        movedElement.getModel().getAnimalView().setFill(movedElement.getModel().getColor());
    }

}
