package IOUtil;

import produceInfo.ProduceInfo;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

public class InputReader {
    public static void readFile(String filepath) {
        File file = new File(filepath);
        if (!file.isDirectory()) {
            System.out.println("path=" + file.getPath());
            System.out.println("absolutepath=" + file.getAbsolutePath());
            System.out.println("name=" + file.getName());
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
                    ProduceInfo produceInfo = new ProduceInfo();
                    produceInfo.instanceName = line;
                    line = bufferedReader.readLine();
                    temp = line.split("\\s+");
                    //assertEquals(2, temp.length);
                    produceInfo.machineCount = Integer.valueOf(temp[0]);
                    produceInfo.partCount = Integer.valueOf(temp[1]);
                    //System.out.println(ProduceInfo.machineCount + "  " + ProduceInfo.partCount);

                    for (int i = 0; i < produceInfo.machineCount; i++) {
                        ArrayList<Integer> list = new ArrayList<>();
                        line = bufferedReader.readLine();
                        temp = line.split("\\s+");
                        for (int j = 1; j < 2 * produceInfo.partCount; j += 2) {
                            list.add(Integer.valueOf(temp[j]));
                        }
                        //System.out.println(list);
                        produceInfo.produceInfo.add(list);
                    }
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
