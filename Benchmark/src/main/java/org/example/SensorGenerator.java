package org.example;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SensorGenerator {
    //header
    private final String[] header;
    static ArrayList<Sensor> data = new ArrayList <>();
    static String SensorName = "Temp_Sensor";//TODO: change name of Sensor
    static ArrayList<String[]> sensors = new ArrayList<>();
    public static int id = 0;
    private static String type;
    private static String typeOfSensor;
    private static String identifier;
    private static String date;
    private static String time;

    public SensorGenerator(String t, String data1, String data2) {
        identifier = "id";//header[1]
        date = "date";// header[2]
        time = "time"; // header[3]
        typeOfSensor = "typeOfSensor"; // header[0]
        header = new String[] {typeOfSensor, identifier, date, time, data1, data2};
        type =  t;
        id += 1;
    }

    public static void main(String[] args) {
        /* TODO: generate multiple different sensors
        *   - make the amount of sensors choosable
        *   - and make the amount of entries of a sensor choosable
        *   - predefine some sensorTypes
        */
        SensorGenerator sensor1 = new SensorGenerator("tyre", "temp", "pressure"); //TODO: change typeOfSensor and data1, data2
        //SensorGenerator sensor2 = new SensorGenerator("speed", "kp/h", "wind_speed");
        File file = new File("src/main/resources/" + SensorName + ".csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            // adding header to csv
            writer.writeNext(sensor1.header);
            //writer.writeNext(sensor2.header);

            //create data
            Sensor newData = new Sensor(sensor1.header[0], sensor1.header[1], sensor1.header[2], sensor1.header[3], sensor1.header[4], sensor1.header[5]);
            int counter = 2000; // change amount of entries here
            while(counter != 0) {
                double data1 = RandomData.getRandom(89.99, 101.99); //change Random min and max here
                double data2 = RandomData.getRandom(17.9, 30.5); //change Random min and max here
                newData.sensorData.add(new String[]{type, String.valueOf(id), (new SimpleDateFormat("dd:MM:yyyy").format(new Date())), RandomData.getTime(), String.valueOf(data1), String.valueOf(data2)});
                counter --;
            }
            for(int i = 0; i < newData.sensorData.size(); i++) {
                writer.writeNext(newData.sensorData.get(i));
            }

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
