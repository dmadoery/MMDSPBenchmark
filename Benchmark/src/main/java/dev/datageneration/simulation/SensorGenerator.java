package dev.datageneration.simulation;

import dev.datageneration.simulation.Sensors.Sensor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SensorGenerator {
    // Layout sensorList: {"Name", "typeOfSensor", "Data1", "dataRange_min1", "dataRange_max1", "Data2", "dataRange_min2", "dataRange_max2"}
    static List<Sensor> sensorList = new ArrayList<>();

    /**
     * Creates Sensors and fills them with data accordingly to the given sensorArray.
     * Once the sensors are created it writes their data into csv files.
     * @param sensorArray int[]
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
//            File csvFile = new File("src/main/resources/" + sensor.getId() + "_" + sensor.getType() + ".csv");
            File jsonFile = new File("src/main/resources/" + sensor.getId() + "_" + sensor.getType() + ".json");
            try {
                // create FileWriter object with file as parameter
                FileWriter jsonOutputFile = new FileWriter(jsonFile);
//                FileWriter csvOutputFile = new FileWriter(csvFile);

                // create CSVWriter object filewriter object as parameter
//                CSVWriter csvWriter = new CSVWriter(outputfile, ';',
//                        CSVWriter.NO_QUOTE_CHARACTER,
//                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                        CSVWriter.DEFAULT_LINE_END);

                //create JSONWriter object
                List<String[]> data = sensor.getDataPoints();
                JSONArray jsonArray = new JSONArray();
                String[] header = sensor.getHeader();

                int freq = sensor.getFrequencyValue();
                int count = 1;
                for (String[] d : data) {
                    if(freq == 0) {
                        freq = sensor.getFrequencyValue();
                        count ++;
                    }
                    JSONObject sensorDataObject = new JSONObject();
                    for (int j = 0; j < d.length; j++) {
                        sensorDataObject.put(header[j], Double.valueOf(d[j])); // Add each sensor data point to JSON object
                    }
                    // Wrap each JSON object with a number prefix
                    JSONObject freqObject = new JSONObject();
                    freqObject.put("freq", count);
                    freqObject.put(sensor.getType(), sensorDataObject);  // Prefix with number
                    freq--;

                    // Add the prefixed object to the JSON array
                    jsonArray.put(freqObject);
                }

                // Write JSON array to file
                jsonOutputFile.write(jsonArray.toString(4)); // Indented JSON output for readability
                jsonOutputFile.close(); // Close JSON file writer


//                //write header CSV
//                csvWriter.writeNext(sensor.getHeader());
//                System.out.println(Arrays.toString(sensor.getHeader()));
//
//                //write data CSV
//                List<String[]> data = sensor.getDataPoints();
//                for (String [] d: data) {
//                    csvWriter.writeNext(d);
//                    System.out.println(Arrays.toString(d));
//                }

               System.out.println("-------------------------------------------------------");
//                 closing writer connection
//                 csvWriter.close();
//                 csvOutputFile.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
