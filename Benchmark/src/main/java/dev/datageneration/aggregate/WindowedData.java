package dev.datageneration.aggregate;

import lombok.Setter;
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

    @Setter
    static  File folderData;
    @Setter
    static  File folderStore;
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
            case "tire":
                if(jsonObject.getJSONObject("data").getInt("temperature tire") > 110) {
                    warning = "position:" + jsonObject.getJSONObject("data").getInt("position") + " is to hot.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getInt("wear") > 90) {
                    warning = "position:" + jsonObject.getJSONObject("data").getInt("position") + " is worn down.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getDouble("pressure psi") > 30) {
                    warning = "position:" + jsonObject.getJSONObject("data").getInt("position") + " to high pressure.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "heat":
                if(jsonObject.getJSONObject("data").getInt("temperature c") > 50) {
                    warning = " to hot temperature.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "engine":
                if(jsonObject.getJSONObject("data").getDouble("oil_pressure") > 7) {
                    warning = " oil pressure to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                if(jsonObject.getJSONObject("data").getInt("temperature engine") > 600) {
                    warning = " is overheating.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getDouble("fuel_pressure") > 5) {
                    warning = " fuel pressure to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getLong("rpm") > 18000) {
                    warning = " rpm to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                if(jsonObject.getJSONObject("data").getDouble("exhaust") > 1.2) {
                    warning = " exhaust fumes not good.";
                    createErrorObject(jsonObject, type, warning);
                }
                if(jsonObject.getJSONObject("data").getInt("fuelFlow") > 120) {
                    warning = " fuelFlow to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "fuelPump":
                if(jsonObject.getJSONObject("data").getLong("ml/min") > 4000) {
                    warning = " fuel flow is to low.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getInt("temperature fuelP") > 60) {
                    warning = " fuel-pump temperature is to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "brake":
                if(jsonObject.getJSONObject("data").getInt("temperature brake") > 1000) {
                    warning = " is overheating.";
                    createErrorObject(jsonObject, type, warning);
                }
                if(jsonObject.getJSONObject("data").getInt("wear") > 90) {
                    warning = " is worn down.";
                    createErrorObject(jsonObject, type, warning);
                }
                if(jsonObject.getJSONObject("data").getInt("brake_pressure") > 10) {
                    warning = " brake pressure to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "gForce":
                if(jsonObject.getJSONObject("data").getDouble("g-lateral") > 6) {
                    warning = " g-force lateral is high.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getDouble("g-longitudinal") > 5) {
                    warning = " g-force longitudinal is high.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;
            case "accelerometer":
                if(jsonObject.getJSONObject("data").getInt("throttlepedall") > 100) {
                    warning = " throttlepedall is high.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            case "speed":
                if(jsonObject.getJSONObject("data").getDouble("kph") > 360) {
                    warning = " kph is high.";
                    createErrorObject(jsonObject, type, warning);
                }
                if (jsonObject.getJSONObject("data").getDouble("wind speed") > 200) {
                    warning = " wind speed is to high.";
                    createErrorObject(jsonObject, type, warning);
                }
                data.remove(jsonObject);
                break;

            default:
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
