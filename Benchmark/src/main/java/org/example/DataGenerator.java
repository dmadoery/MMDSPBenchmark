package org.example;
import static java.lang.System.in;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;


public class DataGenerator {
    public static Sensor sens;
    public static ArrayList<String[]> data = new ArrayList <>();

    public static String[] split(String line) {
        return line.split(";", 10);
    }


    public static void main(String[] args) throws  Exception {
        try(FileReader in = new FileReader("src/main/resources/g_sensor.csv");
            BufferedReader br = new BufferedReader(in)) {
            int j = 0;
            while(true) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                if(line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                else{
                    String[] splited = split(line);
                    //System.out.println(splited.length);
                    data.add(splited);
//                    for (String item : data.get(j)) {
//                        System.out.println("Row " + j + ": " + item);
//                    }
                }
                if (j == 0) {
                    sens = new Sensor(data.get(j)[0],data.get(j)[1], data.get(j)[2], data.get(j)[3], data.get(j)[4], data.get(j)[5]);
                } if (j != 0) {
                    sens.sensorData.add(data.get(j));
                }
                j++;
            }
            for (int i = 0; i < sens.sensorData.size(); i++) {
                Sensor.print(i);
            }
            br.close();
        }
    }
}