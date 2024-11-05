package dev.datageneration.simulation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;


public class DataGenerator {
    static final File folder = new File("src/main/resources");
    static final String fName = "ALL_DATA";
    static List<String> filenames = new LinkedList<>();
    static List<JSONObject> allData = new ArrayList<>();  // Store JSONObjects instead of String arrays

    /**
     * Reads the data from the generated JSON files and stores them in a list (allData).
     * Always sorts the list by "prefix" and writes the sorted data back to a JSON file called ALL_DATA.json.
     */
    public static void dataGenerator() throws Exception {
        filenames = RandomData.listFilesForFolder(folder);

        // Debug: Print filenames to ensure correct files are read
        System.out.println("Filenames: " + filenames);

        for (String file : filenames) {
            if (file.endsWith(".json")) {
                FileReader fReader = new FileReader(new File(folder, file));
                BufferedReader bReader = new BufferedReader(fReader);

                // Read the entire content of the JSON file
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = bReader.readLine()) != null) {
                    jsonContent.append(line);
                }
                bReader.close();

                // Parse the JSON content
                JSONArray jsonArray = new JSONArray(jsonContent.toString());

                // Process each entry in the JSON array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    allData.add(jsonObject);  // Store each JSONObject in the allData list
                }
            }
        }

        // Debug: Print size of allData list
        System.out.println("Total JSON objects collected: " + allData.size());

        // Sort the list based on the "freq" key in ascending order
        allData.sort(Comparator.comparingInt(obj -> obj.getInt("freq")));  // Sort by freq in ascending order

        // Check if we have data
        if (allData.isEmpty()) {
            System.out.println("No data to write.");
            return;  // Exit if no data to write
        }

        // Write the sorted data back to a JSON file "ALL"
        File outputFile = new File("src/main/resources/" + fName + ".json");

        try (FileWriter outputfile = new FileWriter(outputFile)) {
            JSONArray outputArray = new JSONArray(allData);  // Convert the list of JSONObjects back to JSONArray
            outputfile.write(outputArray.toString(4));  // Indented output for readability
            System.out.println("Data successfully written to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing the output file: " + e.getMessage());
            throw e;
        }
    }
}