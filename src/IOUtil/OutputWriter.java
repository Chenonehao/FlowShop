package IOUtil;

import order.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
    static File writeName;
    static BufferedWriter out;

    public static void init(Order order) {
        writeName = new File("output\\outputOf"+order.instanceName+".txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
        try {
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            FileWriter writer = new FileWriter(writeName);
            out = new BufferedWriter(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入TXT文件
     */
    public static void writeFile(String info) {
        try {
            {
                out.write(info + "\r\n");
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


