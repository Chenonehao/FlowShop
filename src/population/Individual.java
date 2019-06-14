package population;

import order.Order;
import produce.Machine;
import produce.Machines;
import produce.Part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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

    private void reset() {
        for (int i = 0; i < machineStage.size(); i++)
            machineStage.set(i, 0);
        for (int i = 0; i < partStage.size(); i++)
            partStage.set(i, 0);
    }

    private void decodeGene() {
        int temp, indexPart, indexMachine;
        reset();
        Part part;
        for (int i = 0; i < geneticInfo.size(); i++) {
            temp = geneticInfo.get(i);
            indexPart = partStage.get(temp - 1);
            processInfo.get(indexPart).set(machineStage.get(indexPart), temp);
            machineStage.set(indexPart, machineStage.get(indexPart) + 1);
            partStage.set(temp - 1, indexPart + 1);
        }
    }

    private void encodeGene() {
        for (int i = 0; i < processInfo.size(); i++) {
            for (int j = 0; j < processInfo.get(0).size(); j++) {
                geneticInfo.set(i * processInfo.get(0).size() + j, processInfo.get(i).get(j));
            }
        }
    }

    public void specificOp(int partID, int movement) {
        decodeGene();
        Random random = new Random();
        for (int i = 0; i < processInfo.size(); i++) {
            int index = processInfo.get(i).indexOf(partID + 1);
            //if (random.nextInt(100) < 90)
                if (movement > 0) {
                    if (index + movement >= processInfo.get(i).size()) {
                        for (int j = index; j < processInfo.get(i).size() - 1; j++) {
                            swamp(i, j, j + 1);
                        }
                    } else {
                        for (int j = index; j < index + movement; j++) {
                            swamp(i, j, j + 1);
                        }
                    }
                } else if (movement < 0) {
                    if (index + movement < 0) {
                        for (int j = index; j > 0; j--) {
                            swamp(i, j, j - 1);
                        }
                    } else {
                        for (int j = index; j > index + movement; j--) {
                            swamp(i, j, j - 1);
                        }
                    }
                }
        }
        encodeGene();
    }

    private void swamp(int i, int index1, int index2) {
        int temp = processInfo.get(i).get(index1);
        processInfo.get(i).set(index1, processInfo.get(i).get(index2));
        processInfo.get(i).set(index2, temp);
    }
}
