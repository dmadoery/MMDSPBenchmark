package dev.datageneration.aggregate;

import dev.datageneration.simulation.RandomData;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static dev.datageneration.jsonHandler.JsonFileHandler.readJsonFile;
import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;
import static dev.datageneration.simulation.RandomData.listFilesForFolder;


/**
 * This class ensures that the data which is sent from the sensors are more real.
 * This is done by making some entries "null" entries and deleting some completely.
 */
public class ErrorCreator {
    @Setter
    static File folderData;
    static List<String> filenames = new LinkedList<>();
    static List<JSONObject> data = new ArrayList<>();  // Store JSONObjects instead of String arrays
    static List<JSONObject> dataWithErrors = new ArrayList<>();  // Store JSONObjects instead of String arrays

    public static void dataWithErrors() throws IOException {
        filenames = listFilesForFolder(folderData);

        for (String file : filenames) {
            if (file.endsWith(".json")) {

                readJsonFile(folderData, file, data);
                createErrors();
                deleteEntries();
                dataWithErrors.sort(Comparator.comparingInt(jsonObject -> jsonObject.getInt("tick")));
                writeJsonFile(folderData, file.split("\\.")[0], dataWithErrors);
            }
        }
    }

    private static void deleteEntries() {
        int amountDeletions = (int) RandomData.getRandom(0, (double)dataWithErrors.size() /100);
        for (int i = 0; i < amountDeletions; i++) {
            int rand = (int)RandomData.getRandom(0, dataWithErrors.size() - 1);
            dataWithErrors.remove(rand);
        }
    }

    private static void createErrors() {
        dataWithErrors.clear();
        int max = Math.max(data.size()/6 , 0);
        int amountErrors = (int)RandomData.getRandom(1, max);
        while (amountErrors > 0) {
            int rand = (int)RandomData.getRandom(0, data.size() - 1);
            JSONObject d = data.get(rand);
            int tick = d.getInt("tick");
            JSONObject dd = d.getJSONObject("data");
            String type = dd.getString("type");
            int id = dd.getInt("id");
            JSONObject error = new JSONObject();
            JSONObject errorData = new JSONObject();
            errorData.put("type", type);
            errorData.put("id", id);
            errorData.put("Error", "No Data");
            error.put("tick", tick);
            error.put("data", errorData);
            data.remove(rand);
            dataWithErrors.add(error);
            amountErrors--;
        }
        dataWithErrors.addAll(data);
        data.clear();
    }
}
