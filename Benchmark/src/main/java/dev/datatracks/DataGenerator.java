package dev.datatracks;

import com.opencsv.CSVWriter;

import dev.datatracks.simulation.Sensor;
import java.io.*;
import java.util.*;

public class DataGenerator {
    static final File folder = new File("src/main/resources");
    static final String fname = "ALL_DATA";
    static List<String> filenames = new LinkedList<String>();
    static List<Sensor> sensorList = new ArrayList<>();
    static List<String[]> allData = new ArrayList<>();
    static Random random = new Random();
    static long seed = 795673489;

    /**
     * Reads the data from the generated csv files and stores them in a list (allData)
     * Shuffles the list randomly and writes the data back to a csv file called ALL_DATA.csv
     *
     * @throws Exception
     */
    public static void dataGenerator() throws  Exception {
        random.setSeed(seed);
        filenames = RandomData.listFilesForFolder(folder);
        for (String file: filenames) {
            FileReader fReader = new FileReader(folder + "/" + file);
            BufferedReader bReader = new BufferedReader(fReader);
            String line = bReader.readLine();
            Sensor sensor = Sensor.parse(List.of(line.split(";", 10)));
            while (true) {
                line = bReader.readLine();
                if (line == null) {
                    break;
                } else  {
                    sensor.add(line.split(";", 10));
                    allData.add(line.split(";", 10));
                    //System.out.println(Arrays.toString(sensor.dataPoints.getLast()));
                }
            }
            sensorList.add(sensor);
            bReader.close();
        }

        Collections.shuffle(allData, random);

        File outputFile = new File("src/main/resources/" + fname + ".csv");

        try (FileWriter outputfile = new FileWriter(outputFile);
             CSVWriter writer = new CSVWriter(outputfile, ';',
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            // Write the header (use the first sensor's header)
            if (!sensorList.isEmpty()) {
                writer.writeNext(new String[]{
                        "type", "id", "date", "time", "dataInfo1", "dataInfo2", "dataInfo3", "dataInfo4", "dataInfo5", "dataInfo6"});
            }

            // Write the shuffled data rows
            for (String[] data : allData) {
                data[3] = RandomData.getTime(); //overwrite Data Time
                writer.writeNext(data);
            }
        }

    }

}