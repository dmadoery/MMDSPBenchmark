package org.example;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SensorGenerator2 {
    //static ArrayList<Sensor> data = new ArrayList <>();
    // Layout sensorList: {"Name", "typeOfSensor", "Data1", "dataRange_min1", "dataRange_max1", "Data2", "dataRange_min2", "dataRange_max2"}
    static ArrayList<String[]> sensorList = new ArrayList<>();
    public static int id = 0;


//    public SensorGenerator2(String t, String data1, String data2) {
//        identifier = "id";//header[1]
//        date = "date";// header[2]
//        time = "time"; // header[3]
//        typeOfSensor = "typeOfSensor"; // header[0]
//        type =  t;
//        id += 1;
//    }

    public static void main(String[] args) {
        // create Sensors
        RandomData.setSensors();
        int[] entriesPerSensor = new int[] {20000, 400000, 10000, 500}; //TODO: Change amount of Sensors and entries here.
        int amountSensors = entriesPerSensor.length;
        sensorList = RandomData.create_Sensors(amountSensors);

        // write data to csv file for each sensor
        for (int i = 0; i < amountSensors; i++) {
            String[] header = sensorList.get(i);

            //SensorGenerator sensor2 = new SensorGenerator("speed", "kp/h", "wind_speed");
            File file = new File("src/main/resources/" + header[0] + ".csv");
            try {
                // create FileWriter object with file as parameter
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile, ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                // write header
                writer.writeNext(header);

                //write data
                while (entriesPerSensor[i] != 0) {
                    writer.writeNext(new String[]{header[1], String.valueOf(id), RandomData.getDate(), RandomData.getTime(), String.valueOf(RandomData.getRandom(Double.parseDouble(header[3]), Double.parseDouble(header[4]))), String.valueOf(RandomData.getRandom(Double.parseDouble(header[6]), Double.parseDouble(header[7])))});
                    entriesPerSensor[i]--;
                }
                 id ++;
                // closing writer connection
                writer.close();
                outputfile.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
