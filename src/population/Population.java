package population;

import order.Order;
import produce.Machines;
import produce.Parts;

import java.util.ArrayList;
import java.util.Random;

public class Population {
    public ArrayList<Individual> individuals = new ArrayList<>();

    public ArrayList<Individual> nextGeneration = new ArrayList<>();

    public int populationSize;

    public int generation;

    public Population(int populationSize, int generation) {
        this.populationSize = populationSize;
        this.generation = generation;
        this.individuals.clear();
    }

    public void initPopulation(Order order) {
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual(order));
            nextGeneration.add(new Individual(order));
        }
    }

    /**
     * 其实没有计算fitness，直接计算的选择概率
     *
     * @param order
     */
    public void computeFitness(Order order) {
        int timeSum = 0;
        int timeCost = 0;
        int averageTimeCost = 0;
        int minTimeConsume = Integer.MAX_VALUE;
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.resetTime();
            timeCost = individuals.get(i).calculateTimeCost(order);
            individuals.get(i).timeCost = timeCost;
            if (timeCost < minTimeConsume) minTimeConsume = timeCost;
            timeSum += timeCost;
        }
        averageTimeCost = timeSum / populationSize;
        for (int i = 0; i < populationSize; i++) {
            individuals.get(i).selectProb = 0.5 + (averageTimeCost - individuals.get(i).timeCost) / (2.0 * (averageTimeCost - minTimeConsume));
        }
    }

    public void select(Order order) {
        int updateCount = 0;
        int randomselect = 0;
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < individuals.get(i).selectProb * 100) {
                nextGeneration.get(updateCount).updateGeneticInfo(individuals.get(i).geneticInfo);
                updateCount++;
            }
        }
        while (updateCount < populationSize) {
            randomselect = random.nextInt(populationSize);
            if (random.nextInt(100) < individuals.get(randomselect).selectProb * 100) {
                nextGeneration.get(updateCount).updateGeneticInfo(individuals.get(randomselect).geneticInfo);

                updateCount++;
            }
        }
    }

    public void cross() {
        crossOX();
    }

    private void crossOX(){

    }

    public void mutate() {
        mutateDM();
    }

    private void mutateDM(){

    }
}
