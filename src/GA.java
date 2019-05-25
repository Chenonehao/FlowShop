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
        Random random = new Random();
        while(population.generation<100000){
            population.computeFitness();
            population.select();
            population.cross();
            //population.mutate();
            //population.mutateAll();

            //population.mutateWithEM2();
            //population.mutateWithEMAll();
//            if(population.stagnant){
//                switch (random.nextInt(5)){
//                    case 0:
//                        population.mutate();
//                        break;
//                    case 1:
//                        population.mutateAll();
//                        break;
//                    case 2:
//                        population.mutateWithEM();
//                        break;
//                    case 3:
//                        population.mutateWithEM2();
//                        break;
//                    case 4:
//                        population.mutateWithEMAll();
//                        break;
//                }
//
//            }else{
                population.mutateWithEM();
            //}

            population.update();
            population.generation++;
            System.out.println(population.generation);
        }
    }
}
