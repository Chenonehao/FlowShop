package produce;

import order.Order;

import java.util.ArrayList;

public class Machines {
    public static ArrayList<Machine> machines = new ArrayList<>();

    public static void initMachines(Order order) {
        machines.clear();
        for (int i = 0; i < order.machineCount; i++) {
            machines.add(new Machine(i + 1, order.produceInfo.get(i)));
        }
    }

    public static void resetTime(){
        for(Machine machine: machines){
            machine.currentTime=0;
        }
    }
}
