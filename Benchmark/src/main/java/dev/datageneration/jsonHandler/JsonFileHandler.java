package dev.datageneration.jsonHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class JsonFileHandler {
    static final File folderAggregated = new File("src/main/resources/");
    static final File folderSensors = new File("src/main/resources/sensors");
    
    public static void readJsonFile(File folder, String fileName, List<JSONObject> allData) throws IOException {
            FileReader fReader = new FileReader(new File(folder, fileName));
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

    public static void writeJsonFile(File folder, String fName, List<JSONObject> allData) throws IOException {
        // Write the sorted data back to a JSON file "ALL_DATA"
        File outputFile = new File(folder + "/" + fName + ".json");

        try (FileWriter outputfile = new FileWriter(outputFile)) {
            JSONArray outputArray = new JSONArray(allData);  // Convert the list of JSONObjects back to JSONArray
            outputfile.write(outputArray.toString(4));  // Indented output for readability
            System.out.println("Data successfully written to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing the output file: " + e.getMessage());
            throw e;
        }
    }

    public static void deleteAllJsonFiles() {
        File[] files = folderAggregated.listFiles() ;
        File[] files1 = folderSensors.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    boolean isDeleted = file.delete();
                    if (isDeleted) {
                        System.out.println("Deleted: " + file.getName());
                    } else {
                        System.out.println("Failed to delete: " + file.getName());
                    }
                }
            }
        }
        if (files1 != null) {
            for (File file : files1) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    boolean isDeleted = file.delete();
                    if (isDeleted) {
                        System.out.println("Deleted: " + file.getName());
                    } else {
                        System.out.println("Failed to delete: " + file.getName());
                    }
                }
            }
        }
    }
}
