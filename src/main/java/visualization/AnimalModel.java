package visualization;

import elements.Animal;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AnimalModel {
    private final Animal animal;
    private final Circle animalView;
    private final int startingEnergy;

    public AnimalModel(Animal animal, int startingEnergy, double size) {
        this.startingEnergy = startingEnergy;
        this.animal = animal;
        animalView = new Circle(size/2, getColor());
    }

    public Color getColor() {
        if (animal.getCurrentEnergy() < 0.2 * startingEnergy) return Color.rgb(224, 179, 173);
        else if (animal.getCurrentEnergy() < 0.6 * startingEnergy) return Color.rgb(201, 124, 110);
        else if (animal.getCurrentEnergy() < startingEnergy) return Color.rgb(146, 82, 73);
        else return Color.rgb(88, 50, 44);
    }

    public Animal getAnimal() {
        return animal;
    }

    public Circle getAnimalView() {
        return animalView;
    }

}
