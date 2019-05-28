package population;

import order.Order;
import produce.Machine;
import produce.Machines;
import produce.Part;

import java.util.ArrayList;
import java.util.Collections;

public class Individual {
    public ArrayList<Integer> geneticInfo = new ArrayList<>();

    public ArrayList<ArrayList<Integer>> processInfo = new ArrayList<>();

    private ArrayList<Integer> machineStage = new ArrayList<>();

    private ArrayList<Integer> partStage = new ArrayList<>();

    public double fitness;

    public int timeCost;

    public double selectProb;

    public Individual(Order order) {
        //geneticInfo= new ArrayList<>();
        for (int i = 0; i < order.partCount; i++) {
            for (int j = 0; j < order.machineCount; j++)
                geneticInfo.add(i + 1);
        }
        Collections.shuffle(geneticInfo);

        for (int i = 0; i < order.machineCount; i++)
            machineStage.add(0);
        for (int i = 0; i < order.partCount; i++)
            partStage.add(0);

        for (int i = 0; i < order.machineCount; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < order.partCount; j++)
                list.add(0);
            processInfo.add(list);
        }
    }

    public Individual(Order order, int k) {
        for (int i = 0; i < order.machineCount * order.partCount; i++) {
            ArrayList<Integer> list = new ArrayList<>(order.partCount);

        }

    }


    public void updateGeneticInfo(ArrayList<Integer> newGene) {
        for (int i = 0; i < geneticInfo.size(); i++)
            geneticInfo.set(i, newGene.get(i));

    }

    public int calculateTimeCost(Order order) {
        decodeGene();
        for (int i = 0; i < order.machineCount; i++) {
            Machine machine = Machines.machines.get(i);
            machine.process(processInfo, i);
        }
        return Machines.machines.get(order.machineCount - 1).currentTime;
    }

    private void reset(){
        for(int i=0;i<machineStage.size();i++)
            machineStage.set(i,0);
        for(int i=0;i<partStage.size();i++)
            partStage.set(i,0);
    }

    private void decodeGene() {
        int temp, indexPart, indexMachine;
        reset();
        Part part;
        for (int i = 0; i < geneticInfo.size(); i++) {
            temp = geneticInfo.get(i);
            indexPart = partStage.get(temp - 1);
//            if(indexPart==5){
//                System.out.println(geneticInfo);
//                System.out.println(processInfo);
//                System.out.println(machineStage);
//                System.out.println(partStage);
//            }
            processInfo.get(indexPart).set(machineStage.get(indexPart), temp);
            machineStage.set(indexPart,machineStage.get(indexPart)+1);
            partStage.set(temp - 1, indexPart + 1);
        }
    }

}
