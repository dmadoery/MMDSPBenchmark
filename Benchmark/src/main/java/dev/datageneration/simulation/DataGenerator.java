package dev.datageneration.simulation;

import dev.datageneration.jsonHandler.JsonFileHandler;
import lombok.Setter;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;


public class DataGenerator {
    @Setter
    static  File folderData;
    @Setter
    static  File folderStore;
    static final String fName = "ALL_DATA";
    static List<String> filenames = new LinkedList<>();
    static List<JSONObject> allData = new ArrayList<>();  // Store JSONObjects instead of String arrays

    /**
     * Reads the data from the generated JSON files and stores them in a list (allData).
     * Always sorts the list by "prefix" and writes the sorted data back to a JSON file called ALL_DATA.json.
     */
    public static void dataGenerator() throws Exception {
        filenames = RandomData.listFilesForFolder(folderData);

        for (String file : filenames) {
            if (file.endsWith(".json")) {
                JsonFileHandler.readJsonFile(folderData, file, allData);
            }
        }

        // Sort the list based on the "tick" key in ascending order
        allData.sort(Comparator.comparingInt(obj -> obj.getInt("tick")));  // Sort by tick in ascending order

        // Debug: Print size of allData list
//        System.out.println("Total JSON objects collected: " + allData.size());

        // Check if we have data
        if (allData.isEmpty()) {
            System.out.println("No data to write.");
            return;
        }

        // Write the sorted data back to a JSON file, fName = "ALL_DATA"
        writeJsonFile(folderStore, fName, allData);
    }
}