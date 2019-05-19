package population;

import order.Order;
import produce.Machines;
import produce.Parts;

import java.util.ArrayList;

public class Population {
    public ArrayList<Individual> individuals = new ArrayList<>();

    public int populationSize;

    public int generation;

    public Population(int populationSize, int generation) {
        this.populationSize = populationSize;
        this.generation = generation;
        this.individuals.clear();
    }

    public void initPopulation(Order order){
        for(int i=0;i<populationSize;i++){
            individuals.add(new Individual(order));
        }
    }

    public void computeFitness(Order order){
        for(int i=0;i<populationSize;i++){
            Machines.resetTime();
            Parts.resetTime();
            individuals.get(i).calculateTimeCost(order);
        }
    }

    public void cross(){

    }

    public void mutate(){

    }
}
