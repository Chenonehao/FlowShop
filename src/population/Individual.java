package population;

import order.Order;
import produce.Machine;
import produce.Machines;

import java.util.ArrayList;
import java.util.Random;

public class Individual {
    public ArrayList<ArrayList<Integer>> geneticInfo;

    public double fitness;

    public int timeCost;

    public Individual(Order order) {
        geneticInfo= new ArrayList<>();
        for(int i=0;i<order.machineCount;i++){
            ArrayList<Integer> list = new ArrayList<>(order.partCount);
            Random random = new Random();
            for(int j=0;j<order.partCount;j++){
                int randomNumber = random.nextInt(order.partCount)%order.partCount+1;
                while(list.contains(randomNumber))
                    randomNumber = random.nextInt(order.partCount)%order.partCount+1;
                list.add(randomNumber);
            }

            geneticInfo.add(list);
        }
    }

    public void calculateTimeCost(Order order){
        for(int i=0;i<order.machineCount;i++){
            Machine machine = Machines.machines.get(i);
            machine.process(geneticInfo.get(i));
        }
        System.out.println(geneticInfo.get(0));
        System.out.println(Machines.machines.get(order.machineCount-1).currentTime);
    }
}
