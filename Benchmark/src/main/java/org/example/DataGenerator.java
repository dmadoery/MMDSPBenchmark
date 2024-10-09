package org.example;

import java.io.*;
import java.util.*;

public class DataGenerator {
    static final File folder = new File("src/main/resources");
    static List<String> filenames = new LinkedList<String>();
    static ArrayList<Sensor> sensorList = new ArrayList<>();

    public static void main(String[] args) throws  Exception {
        filenames = RandomData.listFilesForFolder(folder);
        for (String file: filenames) {
            FileReader fReader = new FileReader(folder + "/" + file);
            BufferedReader bReader = new BufferedReader(fReader);
            String line = bReader.readLine();
            Sensor sensor = new Sensor(line.split(";", 10));
            while (true) {
                line = bReader.readLine();
                if (line == null) {
                    break;
                } else  {
                    sensor.sensorData.add(line.split(";", 10));
                    System.out.println(Arrays.toString(sensor.sensorData.getLast()));
                }
            }
            sensorList.add(sensor);
            bReader.close();
        }


//        printing all sensors with their data
        for (Sensor sensor : sensorList) {
            for (int i = 0; i < sensor.sensorData.size(); i++) {
                System.out.println(Arrays.toString(sensor.sensorData.get(i)));
            }
        }


        //TESTING:
//        for (Sensor sensor : sensorList) {
//            System.out.println(sensor);
//            System.out.println(Arrays.toString(sensor.sensorData.getFirst()));
//        }
    }
}