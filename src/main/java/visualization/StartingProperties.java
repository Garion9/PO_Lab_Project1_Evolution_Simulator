package visualization;

public class StartingProperties {
    public int worldMapHeight;
    public int worldMapWidth;
    public double jungleToWorldRatio;
    public int animalStartingEnergy;
    public int animalMoveEnergyCost;
    public int plantEnergyBenefit;
    public int startingAnimalsCount;
    public int startingPlantsCount;

    public String getWorldMapHeight() {
        return String.valueOf(worldMapHeight);
    }

    public String getWorldMapWidth() {
        return String.valueOf(worldMapWidth);
    }

    public String getJungleToWorldRatio() {
        return String.valueOf(jungleToWorldRatio);
    }

    public String getAnimalStartingEnergy() {
        return String.valueOf(animalStartingEnergy);
    }

    public String getAnimalMoveEnergyCost() {
        return String.valueOf(animalMoveEnergyCost);
    }

    public String getPlantEnergyBenefit() {
        return String.valueOf(plantEnergyBenefit);
    }

    public String getStartingAnimalsCount() {
        return String.valueOf(startingAnimalsCount);
    }

    public String getStartingPlantsCount() {
        return String.valueOf(startingPlantsCount);
    }
}
