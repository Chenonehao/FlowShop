package produce;

import java.util.ArrayList;

public class Part {
    public int PartID;

    public int processStage;

    public int finishTime;

    public Part(int partID, int finishTime) {
        PartID = partID;
        this.finishTime = finishTime;
    }

}
