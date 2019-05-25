package produce;

import order.Order;

import java.util.ArrayList;

public class Machines {
    public static ArrayList<Machine> machines = new ArrayList<>();

    public static void initMachines(Order order) {
        machines.clear();
        for (int i = 0; i < order.machineCount; i++) {
            ArrayList<Integer> timeCost= new ArrayList<>();
            for(int j=0;j<order.partCount;j++)
                timeCost.add(order.produceInfo.get(j).get(i));
            machines.add(new Machine(i + 1, timeCost));
        }
    }

    public static void resetTime(){
        for(Machine machine: machines){
            machine.currentTime=0;
        }
    }
}
