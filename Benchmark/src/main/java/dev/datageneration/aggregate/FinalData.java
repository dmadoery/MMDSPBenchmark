package dev.datageneration.aggregate;

import lombok.Setter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.datageneration.jsonHandler.JsonFileHandler.readJsonFile;
import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;

public class FinalData {
    @Setter
    static File folderStore;
    static final String fName = "finalData";
    static List<JSONObject> finalData = new ArrayList<>();

    public static void createFinalData() throws IOException {
        readJsonFile(folderStore, "averagedData.json", finalData);
        readJsonFile(folderStore, "windowedData.json", finalData);

        finalData.sort(Comparator.comparingInt(jsonObject -> jsonObject.getInt("tick")));

        System.out.println("Amount of entries sent: " + finalData.size());

        writeJsonFile(folderStore, fName, finalData);
    }
}
