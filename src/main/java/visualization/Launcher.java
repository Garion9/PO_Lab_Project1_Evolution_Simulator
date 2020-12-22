package visualization;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Launcher extends Application {

    private int simulationIndex = 0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Evolution Simulator");
        GridPane settings = null;
        try {
            settings = buildSettings();
        } catch (FileNotFoundException e) {
            settings = new GridPane();
            settings.setAlignment(Pos.CENTER);
            Label line1 = new Label("Cannot find starting properties file");
            line1.setFont(Font.font(15));
            settings.add(line1, 0, 0);
            Label line2 = new Label(e.toString());
            line2.setFont(Font.font(15));
            settings.add(line2, 0, 1);
        }

        Scene scene = new Scene(settings, 640, 640);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private GridPane buildSettings() throws FileNotFoundException {
        GridPane settings = new GridPane();
        settings.setAlignment(Pos.CENTER);
        settings.setHgap(10);
        settings.setVgap(10);
        settings.setPadding(new Insets(25, 25, 25, 25));

        Gson gson = new Gson();
        StartingProperties properties;
        Reader reader = null;
        String fileName = "src\\main\\java\\visualization\\StartingProperties.json";
        try {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(fileName);
        }
        properties = gson.fromJson(reader, StartingProperties.class);
        System.out.println(properties);


        Text sceneTitle = new Text("Input simulation starting values");
        sceneTitle.setFont(Font.font(20));
        settings.add(sceneTitle, 0, 0);

        Text section1 = new Text("World map properties:");
        section1.setFont(Font.font(15));
        settings.add(section1, 0,1);

        Label mapHeight = new Label("World map height:");
        settings.add(mapHeight,0,2);
        TextField userMapHeight = new TextField(properties.getWorldMapHeight());
        settings.add(userMapHeight, 1,2);

        Label mapWidth = new Label("World map width:");
        settings.add(mapWidth,0,3);
        TextField userMapWidth = new TextField(properties.getWorldMapWidth());
        settings.add(userMapWidth, 1,3);

        Label jungleRatio = new Label("Jungle to world ratio:");
        settings.add(jungleRatio,0,4);
        TextField userJungleRatio = new TextField(properties.getJungleToWorldRatio());
        settings.add(userJungleRatio, 1,4);

        Text section2 = new Text("Energy properties:");
        section2.setFont(Font.font(15));
        settings.add(section2, 0,5);

        Label startEnergy = new Label("Animal start energy:");
        settings.add(startEnergy,0,6);
        TextField userStartEnergy = new TextField(properties.getAnimalStartingEnergy());
        settings.add(userStartEnergy, 1,6);

        Label moveEnergy = new Label("Animal move energy cost:");
        settings.add(moveEnergy,0,7);
        TextField userMoveEnergy = new TextField(properties.getAnimalMoveEnergyCost());
        settings.add(userMoveEnergy, 1,7);

        Label plantEnergy = new Label("Plant Energy benefit:");
        settings.add(plantEnergy,0,8);
        TextField userPlantEnergy = new TextField(properties.getPlantEnergyBenefit());
        settings.add(userPlantEnergy, 1,8);

        Text section3 = new Text("Starting quantity properties:");
        section3.setFont(Font.font(15));
        settings.add(section3, 0,9);

        Label startingAnimals = new Label("Starting animals count:");
        settings.add(startingAnimals,0,10);
        TextField userStartingAnimals = new TextField(properties.getStartingAnimalsCount());
        settings.add(userStartingAnimals, 1,10);

        Label startingPlants = new Label("Starting plants count:");
        settings.add(startingPlants,0,11);
        TextField userStartingPlants = new TextField(properties.getStartingPlantsCount());
        settings.add(userStartingPlants, 1,11);

        Button startButton = new Button("Start simulation");
        HBox hbStartButton = new HBox(10);
        hbStartButton.setAlignment(Pos.BOTTOM_CENTER);
        hbStartButton.getChildren().add(startButton);
        settings.add(hbStartButton, 1, 12);

        final Text simStarted = new Text();
        simStarted.setWrappingWidth(200);
        settings.add(simStarted, 1, 13);

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!userMapHeight.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid world map height (integer in range[1,9999])");
                }
                else if(!userMapWidth.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid world map width (integer in range[1,9999])");
                }
                else if(!userJungleRatio.getText().matches("[0]\\.[0-9]{1,3}|[1]")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid jungle to world ratio (floating point value in range(0,1] with up to 3 decimal places precision)");
                }
                else if(!userStartEnergy.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid animal start energy (integer in range[1,9999])");
                }
                else if(!userMoveEnergy.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid animal move energy cost (integer in range[1,9999])");
                }
                else if(!userPlantEnergy.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid plant energy benefit (integer in range[1,9999])");
                }
                else if(!userStartingAnimals.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid starting animals count (integer in range[1,9999])");
                }
                else if(!userStartingPlants.getText().matches("[0-9]{1,4}")) {
                    simStarted.setFill(Color.RED);
                    simStarted.setText("Invalid starting animals count (integer in range[1,9999])");
                }
                else {
                    simStarted.setFill(Color.GREEN);
                    simStarted.setText("Simulation has started");

                    simulationIndex += 1;

                    int height = Integer.parseInt(userMapHeight.getText());
                    int width = Integer.parseInt(userMapWidth.getText());
                    double ratio = Double.parseDouble(userJungleRatio.getText());
                    int startEnergy = Integer.parseInt(userStartEnergy.getText());
                    int moveEnergy = Integer.parseInt(userMoveEnergy.getText());
                    int plantEnergy = Integer.parseInt(userPlantEnergy.getText());
                    int startingAnimals = Integer.parseInt(userStartingAnimals.getText());
                    int startingPlants = Integer.parseInt(userStartingPlants.getText());

                    Simulation simulation = new Simulation(height, width, ratio, startEnergy, moveEnergy, plantEnergy, startingAnimals, startingPlants);

                    simulation.run(simulationIndex);

                }

            }
        });
        return settings;
    }

}