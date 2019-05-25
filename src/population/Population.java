package population;

import order.Order;
import produce.Machines;
import produce.Parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {
    public ArrayList<Individual> individuals = new ArrayList<>();

    public ArrayList<Individual> nextGeneration = new ArrayList<>();

    public Individual bestIndividual;

    public Order order;

    public double totalP;

    public boolean highMutate = false;

    public int mutateProb = 5;

    public boolean stagnant = false;

    public int stagnantGeneration = 0;

    public int bestEver = Integer.MAX_VALUE;

    public int bestIndex;

    public int populationSize;

    public int generation;

    public Population(int populationSize, int generation, Order order) {
        this.populationSize = populationSize;
        this.generation = generation;
        this.order = order;
        this.individuals.clear();
    }

    public void initPopulation() {
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual(order));
            nextGeneration.add(new Individual(order));
        }
//        for(int j=0;j<1;j++){
//            individuals.add(new Individual(order,1));
//            nextGeneration.add(new Individual(order,1));
//        }
    }

    /**
     * 其实没有计算fitness，直接计算的选择概率
     */
    public void computeFitness() {
        int timeSum = 0;
        int timeCost = 0;
        totalP = 0;
        int averageTimeCost = 0;
        int minTimeConsume = Integer.MAX_VALUE;
        bestIndex = 0;
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.resetTime();
            timeCost = individuals.get(i).calculateTimeCost(order);
            individuals.get(i).timeCost = timeCost;
            if (timeCost < minTimeConsume) {
                minTimeConsume = timeCost;
                bestIndex = i;
            }
            timeSum += timeCost;
        }
        averageTimeCost = timeSum / populationSize;
        for (int i = 0; i < populationSize; i++) {
            individuals.get(i).selectProb = (1.0 / individuals.get(i).timeCost) * timeSum;
            totalP += individuals.get(i).selectProb;
        }

        if(bestIndividual==null){
            bestIndividual=new Individual(order);

        }
        bestIndividual.updateGeneticInfo(individuals.get(bestIndex).geneticInfo);

        if (minTimeConsume < bestEver) {
            bestEver = minTimeConsume;
            stagnantGeneration = 0;
            mutateProb = 5;
            stagnant = false;
        } else {
            stagnantGeneration++;
            if (stagnantGeneration > 100) {
                stagnant = true;
                mutateProb = 100;
            }
        }

        System.out.println(minTimeConsume + "  avg  " + averageTimeCost);
        System.out.println(individuals.get(bestIndex).geneticInfo);
    }

    private void calculateTime() {
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.resetTime();
            individuals.get(i).timeCost = individuals.get(i).calculateTimeCost(order);
        }
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.resetTime();
            nextGeneration.get(i).timeCost = nextGeneration.get(i).calculateTimeCost(order);
        }

    }

    /**
     * 使用轮盘赌生成下一代
     * 将最好的个体复制三份
     */
    public void select() {
        for (int i = 0; i < populationSize - 3; i++) {
            int index = selectOne();
            nextGeneration.get(i).updateGeneticInfo(individuals.get(index).geneticInfo);
        }
        for (int i = populationSize - 3; i < populationSize; i++) {
            nextGeneration.get(i).updateGeneticInfo(bestIndividual.geneticInfo);
        }
        switchGeneration();
    }


    /**
     * 使用轮盘赌在当前代中选择一个个体
     *
     * @return 下标
     */
    private int selectOne() {
        int temp = 0;
        Random random = new Random();
        temp = random.nextInt((int) (totalP * 100));
        for (int i = 0; i < populationSize; i++) {
            temp -= individuals.get(i).selectProb * 100;
            if (temp <= 0) return i;
        }
        return -1;
    }


    public void update() {
        switchGeneration();
        calculateTime();
        Random random = new Random();
        Collections.sort(individuals, new Comparator<Individual>() {
            public int compare(Individual o1, Individual o2) {
                return o1.timeCost - o2.timeCost;
            }
        });
//        Collections.sort(nextGeneration, new Comparator<Individual>() {
//            public int compare(Individual o1, Individual o2) {
//                return o2.timeCost - o1.timeCost;
//            }
//        });
//        for (int i = populationSize - 1; i > 0; i--) {
//            if (nextGeneration.get(i).timeCost < individuals.get(i).timeCost) {
//                individuals.get(i).updateGeneticInfo(nextGeneration.get(i).geneticInfo);
//            } else if (random.nextInt(100) < 5)
//                individuals.get(i).updateGeneticInfo(nextGeneration.get(i).geneticInfo);
//        }

        for (int i = populationSize - 1; i > populationSize-2; i--) {
            if (nextGeneration.get(i).timeCost < individuals.get(i).timeCost) {
                individuals.get(i).updateGeneticInfo(bestIndividual.geneticInfo);
            }
        }
//        if(generation>8000){
//            int i=1;
//        }
//
//        if(individuals.get(0).timeCost<bestEver){
//            bestEver = individuals.get(0).timeCost;
//            stagnantGeneration = 0;
//            mutateProb=5;
//            stagnant=false;
//        }else{
//            stagnantGeneration++;
//            if(stagnantGeneration>100) {
//                stagnant = true;
//                mutateProb=100;
//            }
//        }
//
//
//
//        for(int i=0;i<populationSize;i++){
//
//        }

//        int sameCount=0;
//
//        for(int i=1;i<populationSize;i++){
//            if(individuals.get(i).timeCost==individuals.get(i-1).timeCost)sameCount++;
//        }
//
//        if(sameCount<80) {
//            highMutate=false;
//            mutateProb = 5;
//            for (int i = populationSize - 1; i > 0; i--) {
//                if (nextGeneration.get(i).timeCost < individuals.get(i).timeCost) {
//                    individuals.get(i).updateGeneticInfo(nextGeneration.get(i).geneticInfo);
//                }
////                else if (random.nextInt(100) < 5)
////                    individuals.get(i).updateGeneticInfo(nextGeneration.get(i).geneticInfo);
//            }
//        }else{
//            highMutate=true;
//            mutateProb =100;
//            for(int i =2;i<populationSize;i++){
//                if(individuals.get(i).timeCost==individuals.get(i-2).timeCost)
//                    individuals.get(i).updateGeneticInfo(nextGeneration.get(i).geneticInfo);
//            }
//        }

    }


    public ArrayList<Integer> getTwoParent() {
        int updateCount = 0;
        int randomselect = 0;
        Random random = new Random();
        ArrayList<Integer> list = new ArrayList<>();

        int t = 2;
        while (t-- != 0) {
            int i = 0, j = 0;
            i = random.nextInt(populationSize);
            j = random.nextInt(populationSize);
            while (j == i) {
                j = random.nextInt(populationSize);
            }
            if (individuals.get(i).selectProb > individuals.get(j).selectProb) list.add(i);
            else list.add(j);
        }
        if (list.get(0).equals(list.get(1))) list = getTwoParent();
        return list;
        //switchGeneration();
        //shuffle();
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
        int CrossO, crossX;
        ArrayList<Integer> target;
        Random random = new Random();
        for (i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < 80) {
                target = getTwoParent();
                CrossO = target.get(0);
                crossX = target.get(1);
                crossOX(CrossO, crossX);
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
            for (int j = 0; j < nextGeneration.get(0).geneticInfo.size(); j++) {
                if (random.nextInt(100) < 5) {
                    start = random.nextInt(nextGeneration.get(0).geneticInfo.get(0).size());
                    end = random.nextInt(nextGeneration.get(0).geneticInfo.get(0).size());
                    if (start > end) {
                        int temp = end;
                        end = start;
                        start = temp;
                    }
                    if (nextGeneration.get(0).geneticInfo.get(0).size() != end - start + 1) {
                        target = random.nextInt(nextGeneration.get(0).geneticInfo.get(0).size() - (end - start + 1));
                        mutateDM(i, j, start, end, target);
                    }
                }
            }
        }
        //switchGeneration();
    }

    public void mutateAll() {
        Random random = new Random();
        int start = 0, end = 0;
        int target = 0;
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < 5) {

                start = random.nextInt(nextGeneration.get(0).geneticInfo.get(0).size());
                end = random.nextInt(nextGeneration.get(0).geneticInfo.get(0).size());
                if (start > end) {
                    int temp = end;
                    end = start;
                    start = temp;
                }
                if (nextGeneration.get(0).geneticInfo.get(0).size() != end - start + 1) {
                    target = random.nextInt(nextGeneration.get(0).geneticInfo.get(0).size() - (end - start + 1));
                    for (int j = 0; j < nextGeneration.get(0).geneticInfo.size(); j++) {
                        mutateDM(i, j, start, end, target);
                    }
                }
            }
        }
        //switchGeneration();
    }

    public void mutateWithEM() {
        Random random = new Random();
        int target1 = 0, target2 = 0;

        for (int i = 0; i < populationSize; i++) {
            int j = random.nextInt(individuals.get(0).geneticInfo.size());
            if (random.nextInt(100) < mutateProb) {
                target1 = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                target2 = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                mutateEM(i, j, target1, target2);
            }

        }
    }

    public void mutateWithEMAll() {
        Random random = new Random();
        int target1 = 0, target2 = 0;

        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < 5) {
                target1 = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                target2 = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                for (int j = 0; j < individuals.get(0).geneticInfo.size(); j++) {
                    mutateEM(i, j, target1, target2);
                }
            }
        }
    }


    public void mutateEM(int populationIndex, int geneIndex, int target1, int target2) {

        int temp = nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(target1);
        nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(target1, nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(target2));
        nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(target2, temp);

    }

    public void mutateWithEM2() {
        Random random = new Random();
        int target1 = 0, target2 = 0;

        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < individuals.get(0).geneticInfo.size(); j++) {
                if (random.nextInt(100) < 20) {
                    target1 = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                    target2 = random.nextInt(individuals.get(0).geneticInfo.get(0).size());
                    mutateEM(i, j, target1, target2);
                }
            }
        }
    }

    public void mutateEM2(int populationIndex, int geneIndex, int target1, int target2) {
        int start, end;
        if (target1 > target2) {
            start = target2;
            end = target1;
        } else {
            start = target1;
            end = target2;
        }
        for (int i = start; i < end; i++) {
            int temp = nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(i);
            nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(i, nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(i + 1));
            nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(i + 1, temp);
        }
    }


    private void mutateDM(int populationIndex, int geneIndex, int start, int end, int target) {
        ArrayList<Integer> origList = new ArrayList<>();
        ArrayList<Integer> origLeft = new ArrayList<>();
        for (int i = 0; i < nextGeneration.get(0).geneticInfo.get(0).size(); i++) {
            origList.add(nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(i));
        }

        for (int i = 0; i < start; i++) {
            origLeft.add(nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(i));
        }
        for (int i = end + 1; i < nextGeneration.get(0).geneticInfo.get(0).size(); i++) {
            origLeft.add(nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).get(i));
        }

        int j = 0;
        int k = 0;
        for (int i = 0; i < nextGeneration.get(0).geneticInfo.get(0).size(); i++) {
            if (i < target)
                nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(i, origLeft.get(i));
            else if (i < target + end - start + 1) {
                nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(i, origList.get(j + start));
                j++;
            } else {
                nextGeneration.get(populationIndex).geneticInfo.get(geneIndex).set(i, origLeft.get(k + target));
                k++;
            }

        }

    }
}
