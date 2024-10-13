package org.example;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SensorGenerator {
    // Layout sensorList: {"Name", "typeOfSensor", "Data1", "dataRange_min1", "dataRange_max1", "Data2", "dataRange_min2", "dataRange_max2"}
    static List<Sensor> sensorList= new ArrayList<>();

    /**
     * Creates Sensors and fills them with data accordingly to the given sensorArray.
     * Once the sensors are created it writes their data into csv files.
     * @param sensorArray
     */
    public static void creator(int[] sensorArray) {
        // create Sensors
        RandomData.setSensors();
        RandomData.setSeed(RandomData.seed);
        int amountSensors = sensorArray.length;
        sensorList = RandomData.create_Sensors(sensorArray);

        // write data to csv file for each sensor
        for (int i = 0; i < amountSensors; i++) {
            Sensor sensor = sensorList.get(i);

            //SensorGenerator sensor2 = new SensorGenerator("speed", "kp/h", "wind_speed");
            File file = new File("src/main/resources/" + sensor.id + sensor.type + ".csv");
            try {
                // create FileWriter object with file as parameter
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile, ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                // write header
                if (sensor.data1Info != null && sensor.data2Info == null) {
                    writer.writeNext(new String[]{"type", "id", "date", "time", sensor.data1Info});
                }
                else if (sensor.data3Info == null) {
                    writer.writeNext(new String[]{"type", "id", "date", "time", sensor.data1Info, sensor.data2Info});
                }
                else if (sensor.data4Info == null) {
                    writer.writeNext(new String[]{"type", "id", "date", "time", sensor.data1Info, sensor.data2Info,
                            sensor.data3Info});
                }
                else if (sensor.data5Info == null) {
                    writer.writeNext(new String[]{"type", "id", "date", "time", sensor.data1Info, sensor.data2Info,
                            sensor.data3Info, sensor.data4Info});
                }
                else if (sensor.data6Info == null) {
                    writer.writeNext(new String[]{"type", "id", "date", "time", sensor.data1Info, sensor.data2Info,
                            sensor.data3Info, sensor.data4Info, sensor.data5Info});
                } else {
                    writer.writeNext(new String[]{"type", "id", "date", "time", sensor.data1Info,
                            sensor.data2Info, sensor.data3Info, sensor.data4Info, sensor.data5Info, sensor.data6Info});
                }

                //write data
                for (String[] dataPoint : sensor.dataPoints) {
                    writer.writeNext(dataPoint);
                }
                // closing writer connection
                writer.close();
                outputfile.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
