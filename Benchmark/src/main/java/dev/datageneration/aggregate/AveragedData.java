package dev.datageneration.aggregate;

import lombok.Setter;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static dev.datageneration.jsonHandler.JsonFileHandler.readJsonFile;
import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;
import static dev.datageneration.simulation.RandomData.listFilesForFolder;

public class AveragedData {
    @Setter
    static File folderData;
    @Setter
    static File folderStore;
    static final String fName = "averagedData";
    static List<String> filenames = new LinkedList<>();
    static List<JSONObject> allData = new ArrayList<>();
    static List<JSONObject> aggregatedData = new ArrayList<>();
    static long durationTimeStep;
    static int windowSize;
    static int advanceBy;

    /**
     * Reads the data from the generated JSON files and stores them in a list (allData).
     */
    public static void aggregatedData(long durTime) throws Exception {
        durationTimeStep = durTime;
        advanceBy = 45; // good number
        windowSize = (int) (advanceBy * 4);


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
                case "tire":
                    createAverageTire(data, id);
                    break;

                case "heat":
                    createAverageHeat(data, id);
                    break;

                case "engine":
                    createAverageEngine(data, id);
                    break;

                case "brake":
                    createAverageBrake(data, id);
                    break;

                case "fuelPump":
                    createAverageFuelPump(data, id);
                    break;

                case "accelerometer":
                    createAverageAc(data, id);
                    break;

                case "speed":
                    createAverageSpeed(data, id);
                    break;

                default:
                    allData.remove(data);
                    break;
            }
        }

        //sort list
        aggregatedData.sort(Comparator.comparingInt(obj -> obj.getInt("tick")));  // Sort by tick in ascending order

        // Write the sorted data back to a JSON file "aggregatedData.json"
        writeJsonFile(folderStore, fName, aggregatedData);
    }

    private static void createAverageBrake(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double p1 = getAveragePressure(id);
            double t1 = getAverageTemp(id);
            double w1 = getAverageWear(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averagePressure", p1);
            sensorDataObject.put("averageWear", w1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
    }

    private static void createAverageSpeed(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double s1 = getAverageKph(id);
            double s2 = getAverageWindSpeed(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageSpeed kph", s1);
            sensorDataObject.put("averageSpeed mph", (s1 / 1.609344));
            sensorDataObject.put("averageWindSpeed", s2);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
    }


    private static void createAverageAc(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double a1 = getAverageThrottle(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageThrottlepedall", a1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
        rmvAll(id);
    }

    private static void createAverageFuelPump(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double t1 = getAverageTemp(id);
            double f1 = getAverageFlowRate(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averageFlowRate", f1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
        rmvAll(id);
    }


    private static void createAverageEngine(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double p1 = getAverageOilPressure(id);
            double p2 = getAverageFuelPressure(id);
            double t1 = getAverageTemp(id);
            double r1 = getAverageRpm(id);
            double f1 = getAverageFuelFlow(id);
            double e1 = getAverageExhaust(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averageOilPressure", p1);
            sensorDataObject.put("averageFuelPressure", p2);
            sensorDataObject.put("averageRPM", r1);
            sensorDataObject.put("averageFuelFlow", f1);
            sensorDataObject.put("averageExhaust", e1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
        rmvAll(id);
    }


    private static void createAverageHeat(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double t1 = getAverageTemp(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
        rmvAll(id);
    }
    public static void createAverageTire(JSONObject data, int id) {
        int startTime = 1;
        int endTime;
        while(allData.stream().anyMatch(obj -> obj.getJSONObject("data").getInt("id") == id)) {
            endTime = startTime + windowSize - 1;
            double p1 = getAveragePressure(id);
            double t1 = getAverageTemp(id);
            double w1 = getAverageWear(id);
            double l1 = getAverageLiability(id);

            rmv(id, advanceBy);

            JSONObject sensorDataObject = new JSONObject();
            sensorDataObject.put("averageTemp", t1);
            sensorDataObject.put("averagePressure", p1);
            sensorDataObject.put("averageWear", w1);
            sensorDataObject.put("averageLiability", l1);
            if(!data.getJSONObject("data").has("Error")) {
                sensorDataObject.put("position", data.getJSONObject("data").getInt("position"));
            }
            JSONObject fObject = jsonWrapper(data, sensorDataObject, id, startTime, endTime);
            aggregatedData.add(fObject);
            startTime += advanceBy;
        }
        rmvAll(id);
    }

    private static double getAverageExhaust(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if(d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("exhaust");
                    counter ++;
                    index ++;
                    break;
                }
                index ++;
            }
        }

        return p / counter;
    }

    private static double getAverageFuelFlow(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if(d.getInt("id") == id && !d.has("Error")) {
                    p += d.getInt("fuelFlow");
                    counter ++;
                    index ++;
                    break;
                }
                index ++;
            }
        }

        return p / counter;
    }

    private static double getAverageLiability(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("liability");
                    counter++;
                    index++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    private static double getAverageWear(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("wear");
                    counter++;
                    index++;
                    break;
                }
                index++;
            }
        }
        return p / counter;
    }

    private static double getAverageWindSpeed(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
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
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("kph");
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
        for (int i = 0; i <= windowSize; i++) {
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
        for (int i = 0; i <= windowSize; i++) {
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
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if (d.getInt("id") == id && !d.has("Error")) {
                    p += d.getLong("rpm");
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
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if(d.getInt("id") == id && !d.has("Error")) {
                    if(d.getString("type").equals("tire")) {
                        p += d.getDouble("pressure psi");
                    } else if (d.getString("type").equals("brake")) {
                        p += d.getDouble("brake_pressure");
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

    public static Double getAverageOilPressure(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if(d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("oil_pressure");
                    counter ++;
                    index ++;
                    break;
                }
                index ++;
            }
        }

        return p / counter;
    }

    public static Double getAverageFuelPressure(int id) {
        double p = 0.0;
        int counter = 0;
        int index = 0;
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject obj = allData.get(index);
                JSONObject d = (JSONObject) obj.get("data");
                if(d.getInt("id") == id && !d.has("Error")) {
                    p += d.getDouble("fuel_pressure");
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
        for (int i = 0; i <= windowSize; i++) {
            while (index < allData.size()) {
                JSONObject d = allData.get(index);
                JSONObject obj = (JSONObject) d.get("data");
                if (obj.getInt("id") == id && !obj.has("Error")) {
                    if (obj.getString("type").equals("tire")) {
                        p += obj.getInt("temperature tire");
                    } else if(obj.getString("type").equals("engine")) {
                        p += obj.getInt("temperature engine");
                    } else if(obj.getString("type").equals("brake")) {
                        p += obj.getInt("temperature brake");
                    } else if (obj.getString("type").equals("fuel_pump")) {
                        p += obj.getInt("temperature fuelP");
                    } else if (obj.getString("type").equals("brake")) {
                        p += obj.getInt("temperature brake");
                    } else if (obj.getString("type").equals("heat")) {
                        p += obj.getInt("temperature c");
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
