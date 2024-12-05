package dev.datageneration.aggregate;

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

public class WindowedData {

    static final File folderData = new File("src/main/resources/sensors");
    static final File folderStore = new File("src/main/resources");
    static final String fName = "windowedData";
    static List<String> filenames = new LinkedList<>();
    static List<JSONObject> data = new ArrayList<>();  // Store JSONObjects instead of String arrays
    static List<JSONObject> windowedData = new ArrayList<>();  // Store JSONObjects instead of String arrays

    public static void createWindowedData() throws IOException {
        filenames = listFilesForFolder(folderData);

        for (String file : filenames) {
            if (file.endsWith(".json")) {
                readJsonFile(folderData, file, data);
                getWindowedData();
            }
        }
        windowedData.sort(Comparator.comparingInt(jsonObject -> jsonObject.getInt("tick")));
        writeJsonFile(folderStore, fName, windowedData);
    }

    private static void getWindowedData() {
        while (!data.isEmpty()) {
            JSONObject dataEntry = data.getFirst();
            JSONObject d = dataEntry.getJSONObject("data");
            getWarnings(d.getString("type"), dataEntry);
        }
    }

    private static void getWarnings(String type, JSONObject jsonObject) {
        String warning;
        if(jsonObject.getJSONObject("data").has("Error")) {
            windowedData.add(jsonObject);
            data.remove(jsonObject);
            return;
        }
        switch (type) {
            case "tyre":
                if(jsonObject.getJSONObject("data").getInt("temperature tyre") > 100) {
                    warning = "position:" + jsonObject.getJSONObject("data").getInt("position") + " is to hot.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "heat":
                if(jsonObject.getJSONObject("data").getInt("temperature c") > 45) {
                    warning = " to hot temperature.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "engine":
                if(jsonObject.getJSONObject("data").getDouble("oil_pressure") > 6) {
                    warning = " oil pressure to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                if(jsonObject.getJSONObject("data").getDouble("temperature engine") > 550) {
                    warning = " is overheating.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "fuel_pump":
                if(jsonObject.getJSONObject("data").getInt("ml/min") < 800) {
                    warning = " fuel flow is to low.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "brake":
                if(jsonObject.getJSONObject("data").getInt("temperature brake") > 900) {
                    warning = " is overheating.";
                    createErrorObject(jsonObject, type, warning);
                } else {
                    windowedData.add(jsonObject);
                }
                data.remove(jsonObject);
                break;

            case "g_force":
                if(jsonObject.getJSONObject("data").getInt("g-lateral") > 5.8) {
                    warning = " g-force lateral is high.";
                    createErrorObject(jsonObject, type, warning);
                } else if (jsonObject.getJSONObject("data").getInt("g-longitudinal") > 4.8) {
                    warning = " g-force longitudinal is high.";
                    createErrorObject(jsonObject, type, warning);
                }else {
                    windowedData.add(jsonObject);
                }
                data.remove(jsonObject);
                break;

            default:
                windowedData.add(jsonObject);
                data.remove(jsonObject);
                break;
        }
    }

    private static void createErrorObject(JSONObject jsonObject, String type, String warning) {
        JSONObject error = new JSONObject();
        error.put("data", jsonObject.getJSONObject("data"));
        error.put("WarningMessage", type + " id:" + jsonObject.getJSONObject("data").getInt("id") + " " + warning);
        error.put("tick", jsonObject.getInt("tick"));
        windowedData.add(error);
    }
}
