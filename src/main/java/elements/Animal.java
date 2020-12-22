package elements;

import map.IPositionChangeObserver;
import map.IPositionChangePublisher;
import map.WorldMap;
import movement.DirectionHandler;
import movement.MapDirection;
import movement.Vector2d;
import visualization.AnimalModel;

import java.util.*;

public class Animal extends AbstractWorldElement implements IPositionChangePublisher {
    private int currentEnergy;
    private MapDirection orientation;
    private final List<Integer> genotype;
    private int age;
    private final WorldMap map;
    private final List<IPositionChangeObserver> observers = new ArrayList<IPositionChangeObserver>();
    private AnimalModel model;

    // constructor for starting animals (random genotypes)
    public Animal(WorldMap map, int startingEnergy) {
        this.map = map;
        this.observers.add(map);
        this.position = generateStartingPosition(map);
        currentEnergy = startingEnergy;
        orientation = DirectionHandler.getRandom();
        genotype = generateGenotype();
        age = 0;
    }

    //constructor for newborn animals (inherited genotypes)
    public Animal(WorldMap map, Vector2d position, int startingEnergy, List<Integer> genes) {
        this.map = map;
        this.observers.add(map);
        this.position = position;
        currentEnergy = startingEnergy;
        orientation = DirectionHandler.getRandom();
        Animal.validateGenotype(genes);
        Collections.sort(genes);
        genotype = genes;
        age = 0;
    }

    public int getCurrentEnergy() { return currentEnergy; }

    public List<Integer> getGenotype() { return genotype; }

    private static List<Integer> generateGenotype() {
        List<Integer> genes = new ArrayList<>();
        Random generator = new Random();
        while (genes.size() < 32) {
            genes.add(generator.nextInt(8));
        }
        Animal.validateGenotype(genes);
        Collections.sort(genes);
        return genes;
    }

    private static void validateGenotype( List<Integer> genotype) {
        Random generator = new Random();
        boolean wasChanged = false;
        boolean isCorrect = false;
        while (!isCorrect) {
            wasChanged = false;
            for (int i = 0; i < 8; i++) {
                if(!genotype.contains(i)) {
                    genotype.set(generator.nextInt(32), i);
                    wasChanged = true;
                }
            }
            if(!wasChanged) isCorrect = true;
        }
    }

    public void changeEnergy(int difference) { currentEnergy += difference; }

    public void move() {
        Random generator = new Random();
        int degree = generator.nextInt(genotype.size());
        orientation = DirectionHandler.rotate(orientation, genotype.get(degree));
        Vector2d oldPosition = this.position;
        Vector2d newPosition = position.add(orientation.toUnitVector());
        newPosition = newPosition.parsePosition(map.getLowerLimit(), map.getUpperLimit());
        position = newPosition;
        this.positionChanged(oldPosition, newPosition);
    }

    public static Animal copulate(Animal parentFirst, Animal parentSecond) {
        Random generator = new Random();
        int childEnergy = (parentFirst.getCurrentEnergy()/4) + (parentSecond.getCurrentEnergy()/4);
        parentFirst.changeEnergy(parentFirst.getCurrentEnergy()/(-4));
        parentSecond.changeEnergy(parentSecond.getCurrentEnergy()/(-4));

        int split2 = generator.nextInt(30) + 1;
        int split1 = generator.nextInt(split2);

        Integer[] firstPart = new Integer[split1 + 1];
        firstPart = Arrays.copyOfRange(parentFirst.genotype.toArray(firstPart), 0, split1);
        Integer[] secondPart = new Integer[split2 - split1 + 1];
        secondPart = Arrays.copyOfRange(parentSecond.genotype.toArray(secondPart), split1, split2);
        Integer[] thirdPart = new Integer[32 - split2];
        thirdPart = Arrays.copyOfRange(parentFirst.genotype.toArray(thirdPart), split2, 32);

        List<Integer> first = Arrays.asList(firstPart.clone());
        List<Integer> second = Arrays.asList(secondPart.clone());
        List<Integer> third = Arrays.asList(thirdPart.clone());

        List<Integer> childGenotype = new ArrayList<>(first);
        childGenotype.addAll(second);
        childGenotype.addAll(third);

        Vector2d childPosition = Animal.generateChildPosition(parentFirst.position, parentFirst.map);
        return new Animal(parentFirst.map, childPosition, childEnergy, childGenotype);
    }


    private static Vector2d generateChildPosition (Vector2d parentPosition, WorldMap map) {
        Random generator = new Random();
        List<MapDirection> directions = DirectionHandler.possibleDirectionsList();
        for (int i = 0; i < 8; i++) {
            int generatedIndex = generator.nextInt(directions.size());
            Vector2d childPosition = directions.get(generatedIndex).toUnitVector().add(parentPosition);
            childPosition = childPosition.parsePosition(map.getLowerLimit(), map.getUpperLimit());
            if(map.isOccupiedByAnimal(childPosition)) {
                directions.remove(generatedIndex);
            }
            else {
                return childPosition;
            }
        }
        directions = DirectionHandler.possibleDirectionsList();
        return directions.get(generator.nextInt(directions.size())).toUnitVector().add(parentPosition);
    }

    private static Vector2d generateStartingPosition(WorldMap map) {
        ArrayList<Vector2d> possibleAnimalPositions = new ArrayList<Vector2d>();
        for (int i = 0; i < map.getUpperLimit().x+1; i++) {
            for (int j = 0; j < map.getUpperLimit().y+1; j++) {
                Vector2d currentPosition = new Vector2d(i,j);
                if (!map.isOccupiedByAnimal(currentPosition)) possibleAnimalPositions.add(currentPosition);
            }
        }
        Random generator = new Random();
        int generatedIndex = generator.nextInt(possibleAnimalPositions.size());
        return possibleAnimalPositions.get(generatedIndex);
    }

    public void growOlder() {
        age += 1;
    }

    public int getAge() {
        return age;
    }

    public void attachModel(AnimalModel model) {
        this.model = model;
        addObserver(map.getModel());
    }

    public AnimalModel getModel() {
        return model;
    }



    @Override
    public void addObserver(IPositionChangeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IPositionChangeObserver observer) {
        observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for(IPositionChangeObserver observer : observers) {
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }

    public static int compareAnimalsByEnergy(Animal first, Animal second) {
        return  second.currentEnergy - first.currentEnergy;
    }
}
