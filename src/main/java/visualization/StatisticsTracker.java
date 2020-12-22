package visualization;

import elements.Animal;
import elements.Plant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import map.WorldMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StatisticsTracker {
    private final int simulationIndex;
    private int currentAnimalCount;
    private int plantCount;
    private final int[] genes;
    private int totalEnergy;
    private int deadAnimalCount;
    private int totalLivedDays;
    private int totalChildrenCount;
    private int simulationDay;
    private int trackedChildrenCount;
    private int trackedDescendantsCount;
    private List<Animal> trackedDescendants;
    private int trackedDayOfDeath;
    private Text currentAnimals;
    private Text currentPlants;
    private Text averageGenotype;
    private Text averageEnergy;
    private Text averageLifespan;
    private Text averageChildren;
    private Text currentDay;
    private Animal trackedAnimal;
    private Text trackedAnimalGenotypeTitle;
    private Text trackedAnimalGenotype;
    private Text trackedAnimalChildren;
    private Text trackedAnimalDescendants;
    private Text trackedAnimalDayOfDeath;


    public StatisticsTracker(WorldMap map, GridPane view, int simulationIndex) {
        this.simulationIndex = simulationIndex;
        plantCount = map.getPlantsOnMap().values().size();
        currentAnimalCount = 0;
        totalEnergy = 0;
        deadAnimalCount = 0;
        totalLivedDays = 0;
        totalChildrenCount = 0;
        genes = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
        for (List<Animal> animals : map.getAnimalsOnMap().values()) {
            for (Animal animal : animals) {
                currentAnimalCount += 1;
                for (int gene : animal.getGenotype()) {
                    genes[gene] += 1;
                }
                totalEnergy += animal.getCurrentEnergy();
            }
        }
        simulationDay = 0;
        trackedChildrenCount = 0;
        trackedDescendantsCount = 0;
        trackedDescendants = new LinkedList<>();
        trackedDayOfDeath = 0;
        this.buildStatisticsDisplay(view);

    }

    private void buildStatisticsDisplay (GridPane view) {
        view.setVgap(10);
        Text title = new Text("Simulation statistics");
        view.add(title, 0,1);
        GridPane.setHalignment(title, HPos.CENTER);
        currentDay = new Text("Current day: " + simulationDay);
        view.add(currentDay, 0,3);
        GridPane.setHalignment(currentDay, HPos.CENTER);
        currentAnimals = new Text("Current animals count: " + currentAnimalCount);
        view.add(currentAnimals, 0,4);
        GridPane.setHalignment(currentAnimals, HPos.CENTER);
        currentPlants = new Text("Current plants count: " + plantCount);
        view.add(currentPlants, 0,5);
        GridPane.setHalignment(currentPlants, HPos.CENTER);
        averageGenotype = new Text((getAverageGenotype().toString()));
        Text avgGenotypeTitle = new Text("Average genotype:");
        view.add(avgGenotypeTitle, 0, 6);
        GridPane.setHalignment(avgGenotypeTitle, HPos.CENTER);
        view.add(averageGenotype, 0, 7);
        GridPane.setHalignment(averageGenotype, HPos.CENTER);
        averageEnergy = new Text("Average energy level: " + getAverageEnergy());
        view.add(averageEnergy, 0, 8);
        GridPane.setHalignment(averageEnergy, HPos.CENTER);
        averageLifespan = new Text("Average lifespan: " + getAverageLifespan());
        view.add(averageLifespan, 0, 9);
        GridPane.setHalignment(averageLifespan, HPos.CENTER);
        averageChildren = new Text("Average children count: " + getAverageChildrenCount());
        view.add(averageChildren, 0, 10);
        GridPane.setHalignment(averageChildren, HPos.CENTER);

        Button requestStatistics = new Button("Get statistics as file");
        requestStatistics.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveStatistics();
            }
        });
        view.add(requestStatistics,0,11);
        GridPane.setHalignment(requestStatistics, HPos.CENTER);

        trackedAnimalGenotypeTitle = new Text("Selected animal's genome:");
        trackedAnimalGenotypeTitle.setVisible(false);
        view.add(trackedAnimalGenotypeTitle, 0, 13);
        GridPane.setHalignment(trackedAnimalGenotypeTitle, HPos.CENTER);
        trackedAnimalGenotype = new Text();
        trackedAnimalGenotype.setVisible(false);
        view.add(trackedAnimalGenotype, 0, 14);
        GridPane.setHalignment(trackedAnimalGenotype, HPos.CENTER);

        trackedAnimalChildren = new Text("Selected animal's children count: " + trackedChildrenCount);
        trackedAnimalChildren.setVisible(false);
        view.add(trackedAnimalChildren, 0, 15);
        GridPane.setHalignment(trackedAnimalChildren, HPos.CENTER);

        trackedAnimalDescendants = new Text("Selected animal's descendants count: " + trackedDescendantsCount);
        trackedAnimalDescendants.setVisible(false);
        view.add(trackedAnimalDescendants, 0, 16);
        GridPane.setHalignment(trackedAnimalDescendants, HPos.CENTER);

        trackedAnimalDayOfDeath = new Text("Selected animal's day of death: -");
        trackedAnimalDayOfDeath.setVisible(false);
        view.add(trackedAnimalDayOfDeath, 0, 17);
        GridPane.setHalignment(trackedAnimalDayOfDeath, HPos.CENTER);

    }

    private List<Integer> getAverageGenotype() {
        Integer[] averageGenotype = new Integer[32];
        double[] averageGenes = Arrays.stream((genes)).asDoubleStream().map(x -> x/currentAnimalCount).toArray();
        int gene = 0;
        double geneCount = averageGenes[0];
        for (int j = 0; j < 32; j++) {
            averageGenotype[j] = gene;
            if (gene < 7 && j + 1 > geneCount) {
                gene += 1;
                geneCount += averageGenes[gene];
            }
        }
        return Arrays.asList(averageGenotype);
    }

    private int getAverageEnergy() {
        if (currentAnimalCount == 0) return 0;
        else return totalEnergy / currentAnimalCount;
    }

    private int getAverageLifespan() {
        if (deadAnimalCount == 0) return 0;
        else return totalLivedDays / deadAnimalCount;
    }

    private int getAverageChildrenCount() {
        if (currentAnimalCount == 0) return 0;
        else return totalChildrenCount / currentAnimalCount;
    }

    public void animalAdded(Animal added, boolean wasBorn) {
        currentAnimalCount += 1;
        for (int gene : added.getGenotype()) {
            genes[gene] += 1;
        }
        if (wasBorn) totalChildrenCount += 1;
        else totalEnergy += added.getCurrentEnergy();
    }

    public void animalBorn(Animal born, Animal parent1, Animal parent2) {
        animalAdded(born, true);
        if (trackedAnimal == parent1 || trackedAnimal == parent2) {
            trackedChildrenCount += 1;
            trackedDescendantsCount += 1;
            trackedDescendants.add(born);
        }
        for (Animal descendant : trackedDescendants) {
            if(descendant == parent1 || descendant == parent2) {
                if (trackedDescendants.contains(born)) {
                    break;
                }
                else {
                    trackedDescendants.add(born);
                    trackedDescendantsCount += 1;
                }
            }
        }
    }

    public void animalDeceased(Animal deceased) {
        currentAnimalCount -= 1;
        deadAnimalCount += 1;
        totalEnergy -= deceased.getCurrentEnergy();
        for (int gene : deceased.getGenotype()) {
            genes[gene] -= 1;
        }
        totalLivedDays += deceased.getAge();
        if (deceased == trackedAnimal) {
            trackedDayOfDeath = simulationDay;
        }
    }

    public void animalFatigued(int energyLoss) {
        totalEnergy -= energyLoss;
    }

    public void plantGrown() {
        plantCount += 1;
    }

    public void plantEaten(Plant plant) {
        plantCount -= 1;
        totalEnergy += plant.getEnergyValue();
    }

    public void dayPassed() {
        simulationDay += 1;
    }

    public void updateStatisticsView() {
        currentAnimals.setText("Current animals count: " + currentAnimalCount);
        currentPlants.setText("Current plants count: " + plantCount);
        averageGenotype.setText(getAverageGenotype().toString());
        averageEnergy.setText("Average energy level: " + getAverageEnergy());
        averageLifespan.setText("Average lifespan: " + getAverageLifespan());
        averageChildren.setText("Average children count: " + getAverageChildrenCount());
        currentDay.setText("Current day: " + simulationDay);
        trackedAnimalChildren.setText("Selected animal's children count: " + trackedChildrenCount);
        trackedAnimalDescendants.setText("Selected animal's descendants count: " + trackedDescendantsCount);
        if (trackedDayOfDeath != 0) trackedAnimalDayOfDeath.setText("Selected animal's day of death: " + trackedDayOfDeath);
    }

    public void setTrackedAnimal(Animal animalToTrack) {
        if (trackedAnimal != null) {
            trackedAnimal.getModel().getAnimalView().setStroke(Color.TRANSPARENT);
            trackedAnimal.getModel().getAnimalView().setStrokeWidth(0);
            trackedAnimal.getModel().getAnimalView().setRadius(trackedAnimal.getModel().getAnimalView().getRadius()+2);
            trackedAnimalGenotypeTitle.setVisible(false);
            trackedAnimalGenotype.setText("");
            trackedAnimalGenotype.setVisible(false);
            trackedAnimalChildren.setText("");
            trackedAnimalChildren.setVisible(false);
            trackedAnimalDescendants.setText("");
            trackedAnimalDescendants.setVisible(false);
            trackedAnimalDayOfDeath.setText("");
            trackedAnimalDayOfDeath.setVisible(false);
        }
        if (trackedAnimal == animalToTrack) {
            trackedAnimal = null;
        }
        else {
            trackedAnimal = animalToTrack;
            trackedAnimal.getModel().getAnimalView().setStroke(Color.RED);
            trackedAnimal.getModel().getAnimalView().setStrokeWidth(2);
            trackedAnimal.getModel().getAnimalView().setRadius(trackedAnimal.getModel().getAnimalView().getRadius()-2);
            trackedAnimalGenotypeTitle.setVisible(true);
            trackedAnimalGenotype.setText("" + trackedAnimal.getGenotype());
            trackedAnimalGenotype.setVisible(true);

            trackedChildrenCount = 0;
            trackedAnimalChildren.setText("Selected animal's children count: " + trackedChildrenCount);
            trackedAnimalChildren.setVisible(true);

            trackedDescendantsCount = 0;
            trackedDescendants = new LinkedList<>();
            trackedAnimalDescendants.setText("Selected animal's descendants count: " + trackedDescendantsCount);
            trackedAnimalDescendants.setVisible(true);

            trackedDayOfDeath = 0;
            trackedAnimalDayOfDeath.setText("Selected animal's day of death: -");
            trackedAnimalDayOfDeath.setVisible(true);
        }

    }

    private void saveStatistics() {
        File savedStatistics = new File("src\\main\\resources\\savedStatisticsSimulation" + simulationIndex + ".txt");
        try {
            savedStatistics.createNewFile();
            FileWriter writer = new FileWriter("src\\main\\resources\\savedStatisticsSimulation" + simulationIndex + ".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            String rawText = "Current animals count: " + currentAnimalCount + "!" + "Current plants count: " + plantCount + "!" + "Average genotype: " + getAverageGenotype().toString() + "!" + "Average energy level: " + getAverageEnergy() + "!" + "Average lifespan: " + getAverageLifespan() + "!" + "Average children count: " + getAverageChildrenCount() + "!";
            String[] text = rawText.split("!");
            for (String line : text) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
