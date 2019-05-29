package IOUtil;

import order.Order;
import order.Orders;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class InputReader {
    public static void readFile(String filepath) {
        File file = new File(filepath);
        if (!file.isDirectory()) {
//            System.out.println("path=" + file.getPath());
//            System.out.println("absolutepath=" + file.getAbsolutePath());
//            System.out.println("name=" + file.getName());
            extractProduceInfo(file);
        }else if (file.isDirectory()) {
            String[] fileList = file.list();
            for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                readFile(filepath + "\\" + fileList[i]);
            }
        }

    }

    public static void extractProduceInfo(File file){
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] temp;
            while((line = bufferedReader.readLine())!=null){
                if(line.contains("instance")) {
                    Order order = new Order();
                    order.instanceName = line;
                    line = bufferedReader.readLine();
                    temp = line.split("\\s+");
                    order.machineCount = Integer.valueOf(temp[1]);
                    order.partCount = Integer.valueOf(temp[0]);

                    for (int i = 0; i < order.partCount; i++) {
                        ArrayList<Integer> list = new ArrayList<>();
                        line = bufferedReader.readLine();
                        temp = line.split("\\s+");
                        for (int j = 1; j < 2 * order.machineCount; j += 2) {
                            list.add(Integer.valueOf(temp[j]));
                        }
                        order.produceInfo.add(list);
                    }
                    Orders.orders.add(order);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
