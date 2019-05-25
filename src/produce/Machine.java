package produce;

import java.util.ArrayList;

public class Machine {
    public int machineID;

    public ArrayList<Integer> processTime = new ArrayList<>();

    public int currentTime;

    public void process(ArrayList<ArrayList<Integer>> partSequence, int index) {
        currentTime = 0;
        for (int i = 0; i < partSequence.get(0).size(); i++) {
            Part part = Parts.parts.get(partSequence.get(index).get(i)-1);
            if (currentTime < part.finishTime) {
                currentTime = part.finishTime;
                currentTime +=processTime.get(part.PartID-1);
                part.finishTime=currentTime;
            }else{
                currentTime+=processTime.get(part.PartID-1);
                part.finishTime=currentTime;
            }

        }
    }

    public Machine(int machineID, ArrayList<Integer> processTime) {
        this.machineID = machineID;
        this.processTime = processTime;
    }



}
