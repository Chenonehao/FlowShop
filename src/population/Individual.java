package population;

import com.sun.org.apache.xpath.internal.operations.Or;
import order.Order;
import produce.Machine;
import produce.Machines;

import java.util.ArrayList;
import java.util.Random;

public class Individual {
    public ArrayList<ArrayList<Integer>> geneticInfo = new ArrayList<>();

    public double fitness;

    public int timeCost;

    public double selectProb;

    public Individual(Order order) {
        //geneticInfo= new ArrayList<>();
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

    public Individual(Order order, int k){
        for(int i=0;i<order.machineCount;i++){
            ArrayList<Integer> list = new ArrayList<>(order.partCount);
            Random random = new Random();
            for(int j=0;j<order.partCount;j++){
                list.add(j+1);
            }
            geneticInfo.add(list);
        }

    }


    public void updateGeneticInfo(ArrayList<ArrayList<Integer>> newGene){
        for(int i=0;i<geneticInfo.size();i++)
            for(int j=0;j<geneticInfo.get(0).size();j++){
                geneticInfo.get(i).set(j,newGene.get(i).get(j));
            }
    }

    public int calculateTimeCost(Order order){
        for(int i=0;i<order.machineCount;i++){
            Machine machine = Machines.machines.get(i);
            machine.process(geneticInfo, i);
        }
        //System.out.println(geneticInfo.get(0));
        //System.out.println(Machines.machines.get(order.machineCount-1).currentTime);
        return Machines.machines.get(order.machineCount-1).currentTime;
    }
}
