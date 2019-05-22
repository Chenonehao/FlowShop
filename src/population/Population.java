package population;

import com.sun.org.apache.bcel.internal.generic.SWAP;
import order.Order;
import produce.Machines;
import produce.Parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population {
    public ArrayList<Individual> individuals = new ArrayList<>();

    public ArrayList<Individual> nextGeneration = new ArrayList<>();

    public int populationSize;

    public int generation;

    public Population(int populationSize, int generation) {
        this.populationSize = populationSize;
        this.generation = generation;
        this.individuals.clear();
    }

    public void initPopulation(Order order) {
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual(order));
            nextGeneration.add(new Individual(order));
        }
    }

    /**
     * 其实没有计算fitness，直接计算的选择概率
     *
     * @param order
     */
    public void computeFitness(Order order) {
        int timeSum = 0;
        int timeCost = 0;
        int averageTimeCost = 0;
        int minTimeConsume = Integer.MAX_VALUE;
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.resetTime();
            timeCost = individuals.get(i).calculateTimeCost(order);
            individuals.get(i).timeCost = timeCost;
            if (timeCost < minTimeConsume) minTimeConsume = timeCost;
            timeSum += timeCost;
        }
        averageTimeCost = timeSum / populationSize;
        for (int i = 0; i < populationSize; i++) {
            //individuals.get(i).selectProb = 0.5 + (averageTimeCost - individuals.get(i).timeCost) / (2.0 * (averageTimeCost - minTimeConsume));
            individuals.get(i).selectProb = 0.5 + (averageTimeCost - individuals.get(i).timeCost) / (2.0 * (averageTimeCost - minTimeConsume));
        }
        System.out.println(minTimeConsume + "  avg  " + averageTimeCost);
    }

    public void select(Order order) {
        int updateCount = 0;
        int randomselect = 0;
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < individuals.get(i).selectProb * 100) {
                nextGeneration.get(updateCount).updateGeneticInfo(individuals.get(i).geneticInfo);
                updateCount++;
            }
        }
        while (updateCount < populationSize) {
            randomselect = random.nextInt(populationSize);
            if (random.nextInt(100) < individuals.get(randomselect).selectProb * 100) {
                nextGeneration.get(updateCount).updateGeneticInfo(individuals.get(randomselect).geneticInfo);
                updateCount++;
            }
        }

        switchGeneration();
        shuffle();
    }

    private void switchGeneration() {
        ArrayList<Individual> temp = individuals;
        individuals = nextGeneration;
        nextGeneration = temp;
    }

    private void shuffle() {
        Collections.shuffle(individuals);
    }

    public void cross() {
        int i = 0, j = 0;
        Random random = new Random();
        for (i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < 95) {
                for (j = i + 1; j < populationSize; j++) {
                    if (random.nextInt(100) < 95) {
                        crossOX(i, j);
                        break;
                    } else {
                        nextGeneration.get(j).updateGeneticInfo(individuals.get(j).geneticInfo);
                    }
                }
                i = j + 1;
            } else {
                nextGeneration.get(i).updateGeneticInfo(individuals.get(i).geneticInfo);
            }
        }

    }

    /**
     * ox1
     */
    private void crossOX(int O, int X) {
        Random random = new Random();
        ArrayList<Integer> crossPartO = new ArrayList<>();
        ArrayList<Integer> crossPartX = new ArrayList<>();

        ArrayList<Integer> geneListO = new ArrayList<>();
        ArrayList<Integer> geneListX = new ArrayList<>();
        for (int i = 0; i < individuals.get(0).geneticInfo.size(); i++) {
            int start = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
            int end = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }

            crossPartO.clear();
            crossPartX.clear();
            geneListO.clear();
            geneListX.clear();

            for (int j = start; j <= end; j++) {
                crossPartO.add(individuals.get(O).geneticInfo.get(i).get(j));
                crossPartX.add(individuals.get(X).geneticInfo.get(i).get(j));

                nextGeneration.get(O).geneticInfo.get(i).set(j, individuals.get(X).geneticInfo.get(i).get(j));
                nextGeneration.get(X).geneticInfo.get(i).set(j, individuals.get(O).geneticInfo.get(i).get(j));
            }


            int t = 0;
            //end+1  ------   length
            for (t = end + 1; t < individuals.get(0).geneticInfo.get(0).size(); t++) {
                if (!crossPartX.contains(individuals.get(O).geneticInfo.get(i).get(t))) {
                    geneListO.add(individuals.get(O).geneticInfo.get(i).get(t));
                }
                if (!crossPartO.contains(individuals.get(X).geneticInfo.get(i).get(t))) {
                    geneListX.add(individuals.get(X).geneticInfo.get(i).get(t));
                }
            }

            // 0 ------ start
            for (t = 0; t <= end; t++) {
                if (!crossPartX.contains(individuals.get(O).geneticInfo.get(i).get(t))) {
                    geneListO.add(individuals.get(O).geneticInfo.get(i).get(t));
                }
                if (!crossPartO.contains(individuals.get(X).geneticInfo.get(i).get(t))) {
                    geneListX.add(individuals.get(X).geneticInfo.get(i).get(t));
                }
            }

            int tmp = 0;

            for (t = end + 1; t < individuals.get(0).geneticInfo.get(0).size(); t++) {
                nextGeneration.get(O).geneticInfo.get(i).set(t, geneListO.get(tmp));
                nextGeneration.get(X).geneticInfo.get(i).set(t, geneListX.get(tmp));
                tmp++;
            }

            for (t = 0; t < start; t++) {
                nextGeneration.get(O).geneticInfo.get(i).set(t, geneListO.get(tmp));
                nextGeneration.get(X).geneticInfo.get(i).set(t, geneListX.get(tmp));
                tmp++;
            }
        }
    }

    public void mutate() {
        Random random = new Random();
        int start = 0, end = 0;
        int target = 0;
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < individuals.get(0).geneticInfo.size(); j++) {
                if (random.nextInt(1000) < 5) {
                    start = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                    end = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                    if(start>end){
                        int temp = end;
                        end=start;
                        start=temp;
                    }
                    if(individuals.get(0).geneticInfo.get(0).size()!=end-start+1) {
                        target = random.nextInt(individuals.get(0).geneticInfo.get(0).size() - (end - start + 1));
                        mutateDM(i, j, start, end, target);
                    }
                }
            }
        }
        switchGeneration();
    }

    private void mutateDM(int populationIndex,int geneIndex,int start,int end, int target) {
        ArrayList<Integer> origList = new ArrayList<>();
        ArrayList<Integer> origLeft = new ArrayList<>();
        for(int i=0;i<individuals.get(0).geneticInfo.get(0).size();i++){
            origList.add(individuals.get(populationIndex).geneticInfo.get(geneIndex).get(i));
        }

        for(int i=0;i<start;i++){
            origLeft.add(individuals.get(populationIndex).geneticInfo.get(geneIndex).get(i));
        }
        for(int i=end + 1;i<individuals.get(0).geneticInfo.get(0).size();i++){
            origLeft.add(individuals.get(populationIndex).geneticInfo.get(geneIndex).get(i));
        }

        int j=0;
        int k=0;
        for(int i=0;i<individuals.get(0).geneticInfo.get(0).size();i++){
            if(i<target)
                individuals.get(populationIndex).geneticInfo.get(geneIndex).set(i,origLeft.get(i));
            else if(i<target + end - start + 1) {
                individuals.get(populationIndex).geneticInfo.get(geneIndex).set(i, origList.get(j + start));
                j++;
            }else{
                individuals.get(populationIndex).geneticInfo.get(geneIndex).set(i,origLeft.get(k+target));
                k++;
            }

        }

    }
}
