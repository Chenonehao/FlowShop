package order;

import java.util.ArrayList;

public class Order {
    public String instanceName;
    public int machineCount;
    public int partCount;
    public  ArrayList<ArrayList<Integer>> produceInfo = new ArrayList<>();
    public int timeCost;

    public Order() {
    }

    public int process(){

        return timeCost;
    }
}
