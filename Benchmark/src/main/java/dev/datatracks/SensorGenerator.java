package dev.datatracks;

import com.opencsv.CSVWriter;

import dev.datatracks.simulation.Sensor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SensorGenerator {
    // Layout sensorList: {"Name", "typeOfSensor", "Data1", "dataRange_min1", "dataRange_max1", "Data2", "dataRange_min2", "dataRange_max2"}
    static List<Sensor> sensors = new ArrayList<>();

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
        sensors = RandomData.createSensors(sensorArray);

        int i = 0;
        // write data to csv file for each sensor
        for (Sensor sensor : sensors) {

            //SensorGenerator sensor2 = new SensorGenerator("speed", "kp/h", "wind_speed");
            File file = new File("src/main/resources/" + sensor.getName() + ".csv");
            try {
                // create FileWriter object with file as parameter
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile, ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);


                // write header
                writer.writeNext( sensor.getHeader() );

                //write data
                for ( int i1 = 0; i1 < sensorArray[i]; i1++ ) {
                    writer.writeNext(sensor.createDataPoint().asString());
                }

                // closing writer connection
                writer.close();
                outputfile.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }
}
