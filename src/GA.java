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
            OutputWriter.init(order);
            evolve(order);
            order.process();
        }
    }

    public static void readFile(String path){
        InputReader.readFile(path);
    }

    public static void evolve(Order order){
        Population population = new Population(500, 0,order);
        population.initPopulation();
        Machines.initMachines(order);
        Parts.initParts(order);
        System.out.println("in order "+ order.instanceName);
        while(population.generation<100000){
            population.computeFitness();
            population.select();
            population.cross();
            population.mutate();
            population.update();
            population.generation++;
            if(population.stagnantGeneration>5000)break;
            //System.out.println(population.generation);
        }

    }
}
