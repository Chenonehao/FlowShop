package population;

import IOUtil.OutputWriter;
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

    public int nextGeneSize = 0;

    public boolean highMutate = false;

    public boolean syncMode = true;

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
        int maxTimConsume = 0;
        bestIndex = 0;
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.reset();
            timeCost = individuals.get(i).calculateTimeCost(order);
            individuals.get(i).timeCost = timeCost;
            if (timeCost < minTimeConsume) {
                minTimeConsume = timeCost;
                bestIndex = i;
            }
            if (timeCost > maxTimConsume)
                maxTimConsume = timeCost;
            timeSum += timeCost;
        }
        averageTimeCost = timeSum / populationSize;
        for (int i = 0; i < populationSize; i++) {
            //individuals.get(i).selectProb = (1.0 / individuals.get(i).timeCost) * timeSum;
            individuals.get(i).selectProb = maxTimConsume - individuals.get(i).timeCost + (averageTimeCost - individuals.get(i).timeCost);
            if (individuals.get(i).selectProb <= 0) individuals.get(i).selectProb = 0;
            totalP += individuals.get(i).selectProb;
        }

        if (bestIndividual == null) {
            bestIndividual = new Individual(order);

        }
        bestIndividual.updateGeneticInfo(individuals.get(bestIndex).geneticInfo);

        if (minTimeConsume < bestEver) {
            bestEver = minTimeConsume;
            stagnantGeneration = 0;
            mutateProb = 3;
            stagnant = false;
            OutputWriter.writeFile("bestever    " + bestEver + "    in generation " + generation);
            OutputWriter.writeFile(bestIndividual.geneticInfo.toString());
        } else {
            stagnantGeneration++;
            if (stagnantGeneration > 100) {
                stagnant = true;
                mutateProb = 50;
            }
        }

        System.out.println(minTimeConsume + "  avg  " + averageTimeCost + "   bestever   " + bestEver);
    }

    private void calculateTime() {
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.reset();
            individuals.get(i).timeCost = individuals.get(i).calculateTimeCost(order);
        }
        for (int i = 0; i < populationSize; i++) {
            Machines.resetTime();
            Parts.reset();
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
     * @return 下标
     */
    private int selectOne() {
        int temp = 0;
        Random random = new Random();
        if (totalP > 0) {
            temp = random.nextInt((int) (totalP * 100));
            for (int i = 0; i < populationSize; i++) {
                temp -= individuals.get(i).selectProb * 100;
                if (temp <= 0) return i;
            }
        } else {
            return random.nextInt(100);
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
        Collections.sort(nextGeneration, new Comparator<Individual>() {
            public int compare(Individual o1, Individual o2) {
                return o2.timeCost - o1.timeCost;
            }
        });

        for (int i = populationSize - 1; i > populationSize - 5; i--) {
            if (nextGeneration.get(i).timeCost < individuals.get(i).timeCost)
                individuals.get(i).updateGeneticInfo(nextGeneration.get(i).geneticInfo);
        }
        if (stagnant) {
            for (int i = populationSize - 5; i > populationSize - 20; i--) {
                individuals.get(populationSize - 1).updateGeneticInfo(bestIndividual.geneticInfo);
            }
        }
    }


    public ArrayList<Integer> getTwoParent() {
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
        int CrossO, CrossX;
        ArrayList<Integer> target;
        target = getTwoParent();
        CrossO = target.get(0);
        CrossX = target.get(1);
        Random random = new Random();
        nextGeneSize = 0;
        for (i = 0; i < populationSize / 2; i++) {
            if (random.nextInt(100) < 80) {
                crossOX(CrossO, CrossX);
            } else {
                //todo add O X to next generation
                nextGeneSize++;
                nextGeneration.get(nextGeneSize - 1).updateGeneticInfo(individuals.get(CrossO).geneticInfo);
                nextGeneSize++;
                nextGeneration.get(nextGeneSize - 1).updateGeneticInfo(individuals.get(CrossX).geneticInfo);
            }
        }
    }

    /**
     * ox1
     */
    private void crossOX(int O, int X) {
        Random random = new Random();
        ArrayList<Integer> resortO = new ArrayList<>();
        ArrayList<Integer> resortX = new ArrayList<>();
        int start, end;
        start = random.nextInt(individuals.get(0).geneticInfo.size());
        end = random.nextInt(individuals.get(0).geneticInfo.size());
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        if (start == end) return;
        OXresort(start, end, O, resortO);
        OXresort(start, end, X, resortX);

        OXremoveExist(start, end, X, resortO);
        OXremoveExist(start, end, O, resortX);

        OXreconstract(start, end, X, resortO);
        OXreconstract(start, end, O, resortX);

    }

    private void OXresort(int start, int end, int index, ArrayList<Integer> list) {
        for (int i = end + 1; i < individuals.get(0).geneticInfo.size(); i++) {
            list.add(individuals.get(index).geneticInfo.get(i));
        }
        for (int i = 0; i < end + 1; i++) {
            list.add(individuals.get(index).geneticInfo.get(i));
        }
    }

    private void OXremoveExist(int start, int end, int index, ArrayList<Integer> list) {
        ArrayList<Integer> deleteList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            int temp = individuals.get(index).geneticInfo.get(i);
            int order = 0;
            for (int j = 0; j <= i; j++) {
                if (temp == individuals.get(index).geneticInfo.get(j)) order++;
            }
            for (int k = 0; k < list.size(); k++) {
                if (list.get(k) == temp) order--;
                if (order == 0) {
                    deleteList.add(k);
                    break;
                }
            }
        }
        Collections.sort(deleteList, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        for (int i = deleteList.size() - 1; i >= 0; i--) {
            list.remove(deleteList.get(i).intValue());
        }
    }

    private void OXreconstract(int start, int end, int index, ArrayList<Integer> list) {
        nextGeneSize++;
        int temp = nextGeneSize - 1;
        int listIndex = 0;
        for (int i = start; i <= end; i++) {
            nextGeneration.get(temp).geneticInfo.set(i, individuals.get(index).geneticInfo.get(i));
        }
        for (int i = end + 1; i < individuals.get(0).geneticInfo.size(); i++) {
            nextGeneration.get(temp).geneticInfo.set(i, list.get(listIndex));
            listIndex++;
        }
        for (int i = 0; i < start; i++) {
            nextGeneration.get(temp).geneticInfo.set(i, list.get(listIndex));
            listIndex++;
        }

    }


    public void mutate() {
        Random random = new Random();
        if (stagnant) {
            if (random.nextInt(100) < 5)
                for (int i = 0; i < 3; i++) {
                    switch (random.nextInt(3)) {
                        case 0:
                            mutateWithEM();
                            break;
                        case 1:
                            mutateWithIM();
                            break;
                        case 2:
                            mutateWithDM();
                            break;
                    }
                }
            else
                mutateWithSO();
        } else
            mutateWithEM();
    }


    /**
     * Exchange Mutation
     * 交换变异
     * 交换染色体中两位置的基因
     */
    public void mutateWithEM() {
        Random random = new Random();
        int target1 = 0, target2 = 0;
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < mutateProb) {
                target1 = random.nextInt(individuals.get(0).geneticInfo.size());
                target2 = random.nextInt(individuals.get(0).geneticInfo.size());
                mutateEM(i, target1, target2);
            }
        }
    }


    public void mutateEM(int populationIndex, int target1, int target2) {
        int temp = nextGeneration.get(populationIndex).geneticInfo.get(target1);
        nextGeneration.get(populationIndex).geneticInfo.set(target1, nextGeneration.get(populationIndex).geneticInfo.get(target2));
        nextGeneration.get(populationIndex).geneticInfo.set(target2, temp);
    }

    /**
     * Insertion Mutation
     * 插入变异
     * 从染色体中选择一个基因，随机插入到另一个位置
     */
    public void mutateWithIM() {
        Random random = new Random();
        int start = 0, end = 0;
        start = random.nextInt(individuals.get(0).geneticInfo.size());
        end = random.nextInt(individuals.get(0).geneticInfo.size());
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < mutateProb) {
                for (int j = end; j > start; j--) {
                    mutateEM(i, j, j - 1);
                }
            }
        }

    }

    /**
     * Displacement Mutation
     * 替换变异
     * 随机选取一段，替换到随机的一个位置上
     */
    public void mutateWithDM() {
        Random random = new Random();
        int start = 0, end = 0;
        int target = 0;
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < mutateProb) {
                start = random.nextInt(nextGeneration.get(0).geneticInfo.size());
                end = random.nextInt(nextGeneration.get(0).geneticInfo.size());
                if (start > end) {
                    int temp = end;
                    end = start;
                    start = temp;
                }
                if (nextGeneration.get(0).geneticInfo.size() != end - start + 1) {
                    target = random.nextInt(nextGeneration.get(0).geneticInfo.size() - (end - start + 1));
                    mutateDM(i, start, end, target);
                }


            }
        }
    }

    private void mutateDM(int populationIndex, int start, int end, int target) {
        ArrayList<Integer> origList = new ArrayList<>();
        ArrayList<Integer> origLeft = new ArrayList<>();
        for (int i = 0; i < nextGeneration.get(0).geneticInfo.size(); i++) {
            origList.add(nextGeneration.get(populationIndex).geneticInfo.get(i));
        }

        for (int i = 0; i < start; i++) {
            origLeft.add(nextGeneration.get(populationIndex).geneticInfo.get(i));
        }
        for (int i = end + 1; i < nextGeneration.get(0).geneticInfo.size(); i++) {
            origLeft.add(nextGeneration.get(populationIndex).geneticInfo.get(i));
        }

        int j = 0;
        int k = 0;
        for (int i = 0; i < nextGeneration.get(0).geneticInfo.size(); i++) {
            if (i < target)
                nextGeneration.get(populationIndex).geneticInfo.set(i, origLeft.get(i));
            else if (i < target + end - start + 1) {
                nextGeneration.get(populationIndex).geneticInfo.set(i, origList.get(j + start));
                j++;
            } else {
                nextGeneration.get(populationIndex).geneticInfo.set(i, origLeft.get(k + target));
                k++;
            }

        }

    }

    /**
     * Specific Operate
     * 将随机一个工序的加工序列提前或者推迟
     */
    public void mutateWithSO() {
        Random random = new Random();
        int index = 0, movement = 0;
        for (int i = 0; i < populationSize; i++) {
            if (random.nextInt(100) < mutateProb) {
                index = random.nextInt(order.partCount);
                movement = random.nextInt(order.partCount) - random.nextInt(order.partCount);
                if (random.nextInt(100) < 80)
                    movement /= 3;
                mutateSO(i, index, movement);
            }
        }
    }

    public void mutateSO(int populationIndex, int index, int movement) {
        nextGeneration.get(populationIndex).specificOp(index, movement);
    }

}
