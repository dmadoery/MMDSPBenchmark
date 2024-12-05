package dev.datageneration.aggregate;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static dev.datageneration.jsonHandler.JsonFileHandler.readJsonFile;
import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;
import static dev.datageneration.simulation.RandomData.listFilesForFolder;

public class AggregatedData {

    static final File folderData = new File("src/main/resources/sensors");
    static final File folderStore = new File("src/main/resources");
    static final String fName = "aggregatedData";
    static List<String> filenames = new LinkedList<>();
    static List<JSONObject> allData = new ArrayList<>();
    static List<JSONObject> aggregatedData = new ArrayList<>();
    static int interval = 5;

    /**
     * Reads the data from the generated JSON files and stores them in a list (allData).
     */
    public static void aggregatedData() throws Exception {
        filenames = listFilesForFolder(folderData);

        for (String file : filenames) {
            if (file.endsWith(".json")) {
                readJsonFile(folderData, file, allData);
            }
        }

        while(!allData.isEmpty()) {
            JSONObject data = allData.getFirst();
            JSONObject d = data.getJSONObject("data");
            int id = d.getInt("id");
            switch (d.getString("type")) {
                case "tyre":
                    createAverageTyre(data, id);
                    break;

                case "heat":
                    createAverageHeat(data, id);
                    break;

                case "engine":
                    createAverageEngine(data, id);
                    break;

                case "fuel_pump":
                    createAverageFuelPump(data, id);
                    break;

                case "accelerometer":
                    createAverageAc(data, id);
                    break;

                case "speed":
                    createAverageSpeed(data, id);
                    break;

                default:
//                    aggregatedData.add(data);
                    allData.remove(data);
                    break;
            }
        }

        //sort list
        aggregatedData.sort(Comparator.comparingInt(obj -> obj.getInt("tick")));  // Sort by tick in ascending order

        // Write the sorted data back to a JSON file "aggregatedData.json"
        writeJsonFile(folderStore, fName, aggregatedData);
    }

    private static void createAverageSpeed(JSONObject data, int id) {
        int startTime = 1;
        int endTime = 0;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime += interval;
            double s1 = getAverageKph(id);
            double s2 = getAverageWindSpeed(id);

            rmv(id, interval);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageSpeed kph", s1);
            sensorDataObject.put("averageSpeed mph", (s1 / 1.609344));
            sensorDataObject.put("averageWindSpeed", s2);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += interval;
        }
    }

    private static void createAverageAc(JSONObject data, int id) {
        int startTime = 1;
        int endTime = 0;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime += interval;
            double a1 = getAverageThrottle(id);

            rmv(id, interval);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageFlowRate", a1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += interval;
        }
        rmvAll(id);
    }

    private static void createAverageFuelPump(JSONObject data, int id) {
        int startTime = 1;
        int endTime = 0;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime += interval;
            double t1 = getAverageTemp(id);
            double f1 = getAverageFlowRate(id);

            rmv(id, interval);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averageFlowRate", f1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += interval;
        }
        rmvAll(id);
    }


    private static void createAverageEngine(JSONObject data, int id) {
        int startTime = 1;
        int endTime = 0;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime += interval;
            double p1 = getAveragePressure(id);
            double t1 = getAverageTemp(id);
            double r1 = getAverageRpm(id);

            rmv(id, interval);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averagePressure", p1);
            sensorDataObject.put("averageRpm", r1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += interval;
        }
        rmvAll(id);
    }



    private static void createAverageHeat(JSONObject data, int id) {
        int startTime = 1;
        int endTime = 0;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime += interval;
            double t1 = getAverageTemp(id);

            rmv(id, interval);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += interval;
        }
        rmvAll(id);
    }
    public static void createAverageTyre(JSONObject data, int id) {
        int startTime = 1;
        int endTime = 0;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime += interval;
            double p1 = getAveragePressure(id);
            double t1 = getAverageTemp(id);

            rmv(id, interval);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averagePressure", p1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += interval;
        }
        rmvAll(id);
    }

    private static double getAverageWindSpeed(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("wind speed");
                    counter++;
                    index++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    private static double getAverageKph(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("kmp/h");
                    counter++;
                    index++;
                    break;
                }
                index++;
            }
        }
        return p / counter;

    }


    private static double getAverageThrottle(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("throttlepedall");
                    counter++;
                    index++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    private static double getAverageFlowRate(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("ml/min");
                    counter++;
                    index++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    private static double getAverageRpm(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("rpm");
                    counter++;
                    index ++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    public static Double getAveragePressure(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if(d.getInt("id") == id && !d.has("Error")) {
                    if(d.get("type").equals("tyre")) {
                        p += d.getDouble("pressure psi");
                    } else if(d.get("type").equals("engine")) {
                        p += d.getDouble("oil_pressure");
                    }
                    counter ++;
                    index ++;
                    break;
                }
                index ++;
            }
        }

        return p / counter;
    }
    public static Double getAverageTemp(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= interval; i++) {
            while (index < allData.size()) {
                JSONObject d = allData.get(index);
                JSONObject obj = (JSONObject) d.get("data");
                if (obj.getInt("id") == id && !obj.has("Error")) {
                    if (obj.getString("type").equals("tyre")) {
                        p += obj.getInt("temperature tyre");
                    } else if(obj.getString("type").equals("engine")) {
                        p += obj.getInt("temperature engine");
                    } else if(obj.getString("type").equals("brake")) {
                        p += obj.getInt("temperature brake");
                    } else if (obj.getString("type").equals("fuel_pump")) {
                        p += obj.getInt("temperature fuelP");
                    }
                    counter++;
                    index ++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    private static void rmvAll(int id) {
        allData.removeIf(obj -> obj.getJSONObject("data").getInt("id") == id);
    }

    private static void rmv(int id, int amount) {
        List<JSONObject> editedList = new ArrayList<>();
        for (JSONObject obj : allData) {
            if(amount <= 0) {
                break;
            }
            if (obj.getJSONObject("data").getInt("id") == id) {
                editedList.add(obj);
                amount--;
            }
        }
        allData.removeAll(editedList);
    }

    public static JSONObject jsonWrapper(JSONObject data, JSONObject sensorDataObject, int id, int startTime, int endTime) {
        sensorDataObject.put("id", id);
        sensorDataObject.put("type", data.getJSONObject("data").getString("type"));
        JSONObject fObject = new JSONObject();
        fObject.put("data", sensorDataObject);
        fObject.put("interval", new int[] {startTime, endTime});
        fObject.put("tick", endTime);
        return fObject;
    }

}
