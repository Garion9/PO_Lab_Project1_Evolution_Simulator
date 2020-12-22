package visualization;

import elements.Animal;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import map.WorldMap;
import movement.Vector2d;

import java.util.Arrays;

public class Simulation {
    private final int height;
    private final int width;
    private final double ratio;
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private final int startingAnimals;
    private final int startingPlants;

    public Simulation(int height, int width, double ratio, int startEnergy, int moveEnergy, int plantEnergy, int startingAnimals, int startingPlants) {
        this.height = height;
        this.width = width;
        this.ratio = ratio;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.startingAnimals = startingAnimals;
        this.startingPlants = startingPlants;
    }

    public void run(int simulationIndex) {
        double windowSize = 800;
        WorldMap map = new WorldMap(width, height, ratio, startEnergy);
        Stage stage = new Stage();
        stage.setTitle("Simulation " + simulationIndex);

        GridPane background = new GridPane();

        StackPane[][] fields = buildBackground(width, height, background, map, windowSize);

        final boolean[] paused = {true};

        GridPane statisticsWindow = new GridPane();
        statisticsWindow.setPrefWidth(windowSize/2);
        statisticsWindow.setAlignment(Pos.TOP_CENTER);

        GridPane simulationWindow = buildSimulationWindow(background, statisticsWindow, paused, windowSize);

        Scene scene = new Scene(simulationWindow);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMinWidth(windowSize);
        stage.setMinHeight(windowSize);
        stage.show();

        simulate(map, fields, statisticsWindow, paused, windowSize, background, simulationIndex);
    }

    private StackPane[][] buildBackground(int width, int height, GridPane background, WorldMap map, double windowSize) {


        StackPane[][] fields = new StackPane[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(map.inJungle(new Vector2d(j,height-i-1))) {
                    fields[height-i-1][j] = new StackPane(new Rectangle(windowSize/(double)Math.max(width, height),windowSize/(double)Math.max(width, height), Color.GREEN));
                }
                else {
                    fields[height-i-1][j] = new StackPane(new Rectangle(windowSize/(double)Math.max(width, height),windowSize/(double)Math.max(width, height), Color.YELLOWGREEN));
                }
                background.add(fields[height-i-1][j], j, i);
            }
        }
        return fields;
    }

    private GridPane buildSimulationWindow(GridPane background, GridPane statisticsWindow, boolean[] paused, double windowSize) {
        GridPane simulationWindow = new GridPane();
        GridPane controlPanel = new GridPane();
        controlPanel.setPrefWidth(windowSize/3);
        controlPanel.setAlignment(Pos.TOP_CENTER);
        controlPanel.add(new Text("Evolution Simulator"), 0, 0, 2, 1);

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                paused[0] = true;
            }
        });
        controlPanel.add(pauseButton, 0,1);

        Button continueButton = new Button("Continue");
        controlPanel.add(continueButton, 1, 1);
        continueButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                paused[0] = false;
            }
        });

        simulationWindow.add(controlPanel, 0,0, 1, 1);
        simulationWindow.add(statisticsWindow, 0,1);
        simulationWindow.add(background,1,0,1,10);

        simulationWindow.setAlignment(Pos.CENTER);
        simulationWindow.setHgap(0);
        simulationWindow.setVgap(0);
        simulationWindow.setPadding(new Insets(0, 0, 0, 0));

        return simulationWindow;
    }

    private void addTrackingHandler(WorldMap map, StatisticsTracker tracker, boolean[] paused, GridPane background, double cellSize, int height, int width) {
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                background.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!paused[0]) return;
                        int clickedX = (int)(event.getX()/cellSize);
                        int clickedY = (height - (int)(event.getY()/cellSize) - 1);
                        if (map.getAnimalsOnMap().containsKey(new Vector2d(clickedX, clickedY))) {
                            Animal selectedAnimal = map.getAnimalsOnMap().get(new Vector2d(clickedX, clickedY)).get(0);
                            tracker.setTrackedAnimal(selectedAnimal);
                        }

                    }
                });
            }
        }
    }

    private void simulate(WorldMap map, StackPane[][] fields, GridPane statisticsWindow, boolean[] paused, double windowSize, GridPane background, int simulationIndex) {
        double cellSize = windowSize/(double)Math.max(width, height);
        map.attachMapModel(fields, cellSize);
        MapModel model = map.getModel();
        map.attachStatisticsTracker(statisticsWindow, simulationIndex);
        StatisticsTracker tracker = map.getStatisticsTracker();
        map.generatePlants(startingPlants,plantEnergy);
        for (int i = 0; i < startingAnimals; i++) {
            Animal animal = new Animal(map, startEnergy);
            AnimalModel animalModel = new AnimalModel(animal, startEnergy, cellSize);
            animal.attachModel(animalModel);
            map.place(animal, animal.getPosition());
            model.addAnimal(animal, animal.getPosition());
            tracker.animalAdded(animal,false);
        }
        tracker.updateStatisticsView();
        addTrackingHandler(map, tracker, paused, background, cellSize, height, width);



        Timeline periodic = new Timeline(
                new KeyFrame(Duration.seconds(0.2),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if(!paused[0]) {
                                    map.decay();
                                    map.run();
                                    map.eat();
                                    map.reproduce(startEnergy/4);
                                    map.generatePlants(2,plantEnergy);
                                    map.fatigue(moveEnergy);
                                    tracker.dayPassed();
                                    tracker.updateStatisticsView();
                                }
                            }
                        }));
        periodic.setCycleCount(Timeline.INDEFINITE);
        periodic.play();
    }
}
