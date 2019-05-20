import IOUtil.*;
import order.Order;
import order.Orders;
import population.Population;
import produce.Machines;
import produce.Parts;

public class GA {

    public static void main(String[] args) {
        readFile("input");
        for(Order order : Orders.orders){
            evolve(order);
            order.process();
        }
    }

    public static void readFile(String path){
        InputReader.readFile(path);
    }

    public static void evolve(Order order){
        Population population = new Population(100, 0);
        population.initPopulation(order);
        Machines.initMachines(order);
        Parts.initParts(order);
        while(population.generation<1){
            population.computeFitness(order);
            population.select(order);
            population.cross();
            population.mutate();
            population.generation++;
        }
    }
}
