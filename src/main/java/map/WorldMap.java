package map;

import elements.Animal;
import elements.Plant;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import movement.Vector2d;
import visualization.AnimalModel;
import visualization.MapModel;
import visualization.StatisticsTracker;

import java.util.*;

public class WorldMap implements IPositionChangeObserver{
    private final Map<Vector2d, List<Animal>> animalsOnMap = new LinkedHashMap<Vector2d, List<Animal>>();
    private final Map<Vector2d, Plant> plantsOnMap = new LinkedHashMap<Vector2d, Plant>();
    private final Vector2d steppeLowerLimit;
    private final Vector2d steppeUpperLimit;
    private final Vector2d jungleLowerLimit;
    private final Vector2d jungleUpperLimit;
    private final List<Vector2d> steppePossiblePlantLocations = new ArrayList<Vector2d>();
    private final List<Vector2d> junglePossiblePlantLocations = new ArrayList<Vector2d>();
    private final int animalStartingEnergy;
    private MapModel model;
    private StatisticsTracker statTracker;

    public WorldMap(int width, int height, double jungleRatio, int startingEnergy) {
        steppeLowerLimit = new Vector2d(0,0);
        steppeUpperLimit = new Vector2d(width-1,height-1);
        int bottomX = (int)Math.ceil(((double)width/2)*(1-jungleRatio));
        int bottomY = (int)Math.ceil(((double)height/2)*(1-jungleRatio));
        int topX = (int)Math.floor(((double)width/2)*(1+jungleRatio)-1);
        int topY = (int)Math.floor(((double)height/2)*(1+jungleRatio)-1);
        jungleLowerLimit = new Vector2d(bottomX, bottomY);
        jungleUpperLimit = new Vector2d(topX, topY);
        animalStartingEnergy = startingEnergy;

        for(int i = 0; i <= steppeUpperLimit.x; i++) {
            for(int j = 0; j <= steppeUpperLimit.y; j++) {
                Vector2d possibleLocation = new Vector2d(i, j);
                if (possibleLocation.inArea(jungleLowerLimit, jungleUpperLimit)) {
                    junglePossiblePlantLocations.add(possibleLocation);
                }
                else {
                    steppePossiblePlantLocations.add(possibleLocation);
                }
            }
        }
    }

    public void generatePlants(int plantsAmount, int plantEnergy) {
        Random generator = new Random();
        int currentAmountOfGeneratedPlants = 0;
        while (currentAmountOfGeneratedPlants < plantsAmount/2) {
            if (steppePossiblePlantLocations.size() < 1 + this.animalsInSteppe()) break;
            int generatedIndex = generator.nextInt(steppePossiblePlantLocations.size());
            Plant steppePlant = new Plant(steppePossiblePlantLocations.get(generatedIndex), plantEnergy);
            if (!this.isOccupiedByAnimal(steppePlant.getPosition())) {
                plantsOnMap.put(steppePossiblePlantLocations.get(generatedIndex), steppePlant);
                steppePossiblePlantLocations.remove(generatedIndex);
                currentAmountOfGeneratedPlants += 1;
                model.addPlant(steppePlant);
                statTracker.plantGrown();
            }
        }
        currentAmountOfGeneratedPlants = 0;
        while (currentAmountOfGeneratedPlants < plantsAmount/2) {
            if (junglePossiblePlantLocations.size() < 1 + this.animalsInJungle()) break;
            int generatedIndex = generator.nextInt(junglePossiblePlantLocations.size());
            Plant junglePlant = new Plant(junglePossiblePlantLocations.get(generatedIndex), plantEnergy);
            if (!this.isOccupiedByAnimal(junglePlant.getPosition())) {
                plantsOnMap.put(junglePossiblePlantLocations.get(generatedIndex), junglePlant);
                junglePossiblePlantLocations.remove(generatedIndex);
                currentAmountOfGeneratedPlants += 1;
                model.addPlant(junglePlant);
                statTracker.plantGrown();
            }
        }
    }

    private int animalsInJungle() {
        int count = 0;
        for (Vector2d position : animalsOnMap.keySet()) {
            if (position.inArea(jungleLowerLimit, jungleUpperLimit)) count++;
        }
        return count;
    }

    private int animalsInSteppe() {
        int count = 0;
        for (Vector2d position : animalsOnMap.keySet()) {
            if (!position.inArea(jungleLowerLimit, jungleUpperLimit)) count++;
        }
        return count;
    }

    public void place(Animal animal, Vector2d position) {
        if (!animalsOnMap.containsKey(position)) {
            List<Animal> animalOnNewPosition = new ArrayList<>();
            animalOnNewPosition.add(animal);
            animalsOnMap.put(position, animalOnNewPosition);
        }
        else if (animalsOnMap.containsKey(position)) {
            List<Animal> animalsOnPosition = animalsOnMap.get(position);
            if (!animalsOnPosition.contains(animal)) {
                animalsOnPosition.add(animal);
            }
        }
    }

    public void remove(Animal animal, Vector2d position) {
        if (animalsOnMap.containsKey(position)) {
            if (animalsOnMap.get(position).contains(animal)) {
                animalsOnMap.get(position).remove(animal);
            }
            if(animalsOnMap.get(position).isEmpty()) {
                animalsOnMap.remove(position);
            }
        }
    }

    public void run() {
        List<List<Animal>> currentMapSituation = new ArrayList<>(animalsOnMap.values());
        for (List<Animal> animalsOnPosition : currentMapSituation) {
            List<Animal> animalsOnCurrentPosition = new ArrayList<>(animalsOnPosition);
            for (Animal animal : animalsOnCurrentPosition) {
                animal.move();
            }
        }
    }

    public List<Animal> getStrongest(Vector2d position) {
        List<Animal> animalsOnPosition = new ArrayList<Animal>(animalsOnMap.get(position));
        if(animalsOnPosition.size() == 1) {
            return animalsOnPosition;
        }
        else {
            animalsOnPosition.sort(Animal::compareAnimalsByEnergy);
            int maxEnergy = animalsOnPosition.get(0).getCurrentEnergy();
            List<Animal> strongest = new ArrayList<Animal>();
            for (Animal animal : animalsOnPosition) {
                if (animal.getCurrentEnergy() == maxEnergy) strongest.add(animal);
                else break;
            }
            return strongest;
        }
    }

    public void eat() {
        List<Vector2d> plantPositions = new ArrayList<>(plantsOnMap.keySet());
        for (Vector2d plantPosition : plantPositions) {
            if(this.isOccupiedByAnimal(plantPosition)) {
                List<Animal> strongest = this.getStrongest(plantPosition);
                Plant consumedPlant = plantsOnMap.get(plantPosition);
                int toShare = strongest.size();
                for (Animal animal : strongest) {
                    animal.changeEnergy(consumedPlant.getEnergyValue()/toShare);
                }
                model.removePlant(consumedPlant);
                statTracker.plantEaten(consumedPlant);
                plantsOnMap.remove(plantPosition);
                if(plantPosition.inArea(jungleLowerLimit, jungleUpperLimit)) junglePossiblePlantLocations.add(plantPosition);
                else steppePossiblePlantLocations.add(plantPosition);
            }
        }
    }

    public void fatigue(int unitaryEnergyLoss) {
        for (List<Animal> animals : animalsOnMap.values()) {
            for (Animal animal : animals) {
                animal.changeEnergy((-1)*unitaryEnergyLoss);
                animal.growOlder();
                statTracker.animalFatigued(unitaryEnergyLoss);
            }
        }
    }

    public void decay() {
        List<List<Animal>> currentAnimalsOnMap = new ArrayList<List<Animal>>(animalsOnMap.values());
        for (List<Animal> animals : currentAnimalsOnMap) {
            List<Animal> animalsOnPosition = new ArrayList<>(animals);
            for (Animal animal : animalsOnPosition) {
                if (animal.getCurrentEnergy() <= 0) {
                    this.remove(animal, animal.getPosition());
                    animal.removeObserver(this);
                    model.removeAnimal(animal,animal.getPosition());
                    statTracker.animalDeceased(animal);
                }
            }
        }
    }

    public void reproduce(int minimalEnergy) {
        List<Vector2d> animalPositions = new ArrayList<>(animalsOnMap.keySet());
        for (Vector2d position : animalPositions) {
            if (animalsOnMap.get(position).size() >= 2) {
                Random generator = new Random();
                Animal parent1;
                Animal parent2;
                List<Animal> animalsOnPosition = animalsOnMap.get(position);
                animalsOnPosition.sort(Animal::compareAnimalsByEnergy);
                parent1 = animalsOnPosition.get(0);
                int secondHighestEnergy = animalsOnPosition.get(1).getCurrentEnergy();
                int randomBound = 0;
                for (int i = 1; i < animalsOnPosition.size(); i++) {
                    if (animalsOnPosition.get(i).getCurrentEnergy() == secondHighestEnergy) randomBound+=1;
                    else break;
                }
                parent2 = animalsOnPosition.get(generator.nextInt(randomBound) + 1);
                if(parent2.getCurrentEnergy() >= minimalEnergy) {
                    Animal child = Animal.copulate(parent1, parent2);
                    child.attachModel(new AnimalModel(child, animalStartingEnergy, model.getCellSize()));
                    this.place(child, child.getPosition());
                    model.addAnimal(child, child.getPosition());
                    statTracker.animalBorn(child, parent1, parent2);
                }
            }
        }
    }

    public boolean isOccupiedByAnimal(Vector2d position) {
        return animalsOnMap.containsKey(position);
    }

    @Override
    public void positionChanged(Animal movedElement, Vector2d oldPosition, Vector2d newPosition) {
        this.remove(movedElement, oldPosition);
        this.place(movedElement, newPosition);
    }

    public Vector2d getLowerLimit() { return steppeLowerLimit; }

    public Vector2d getUpperLimit() { return steppeUpperLimit; }

    public boolean inJungle(Vector2d position) { return position.inArea(jungleLowerLimit, jungleUpperLimit); }

    public Map<Vector2d, List<Animal>> getAnimalsOnMap() { return animalsOnMap; }

    public Map<Vector2d, Plant> getPlantsOnMap() { return plantsOnMap; }

    public void  attachMapModel(StackPane[][] background, double cellSize) {
        model = new MapModel(this, animalStartingEnergy, background, cellSize);
    }

    public MapModel getModel() {
        return model;
    }

    public void attachStatisticsTracker(GridPane statisticsWindow, int simulationIndex) {
        statTracker = new StatisticsTracker(this, statisticsWindow, simulationIndex);
    }

    public StatisticsTracker getStatisticsTracker() {
        return statTracker;
    }


}
