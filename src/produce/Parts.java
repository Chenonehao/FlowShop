package produce;

import order.Order;

import java.util.ArrayList;

public class Parts {
    public static ArrayList<Part> parts = new ArrayList<>();

    public static void initParts(Order order){
        parts.clear();
        for(int i=0;i<order.partCount;i++){
            parts.add(new Part(i+1, 0));
        }
    }

    public static void resetTime(){
        for(Part part: parts){
            part.finishTime=0;
        }
    }
}
