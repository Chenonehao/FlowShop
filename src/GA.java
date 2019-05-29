import IOUtil.*;
import order.Order;
import order.Orders;
import population.Population;
import produce.Machines;
import produce.Parts;

import java.util.ArrayList;
import java.util.Random;

public class GA {

    public static void main(String[] args) {
        readFile("input");
        OutputWriter.init();
        for(Order order : Orders.orders){
            evolve(order);
            order.process();
        }
    }

    public static void readFile(String path){
        InputReader.readFile(path);
    }

    public static void evolve(Order order){
        Population population = new Population(100, 0,order);
        population.initPopulation();
        Machines.initMachines(order);
        Parts.initParts(order);
        while(population.generation<100000){
            population.computeFitness();
            population.select();
            population.cross();
            population.mutate();
            population.update();
            population.generation++;
            System.out.println(population.generation);
        }
    }
}
