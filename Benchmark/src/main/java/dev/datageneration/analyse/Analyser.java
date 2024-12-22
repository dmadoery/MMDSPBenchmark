package dev.datageneration.analyse;

import dev.datageneration.jsonHandler.JsonFileHandler;
import lombok.Setter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static dev.datageneration.jsonHandler.JsonFileHandler.writeFile;
import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;
import static dev.datageneration.analyse.Comparer.comparing;

public class Analyser {
    static List<JSONObject> dataSent = new LinkedList<>();
    static List<JSONObject> averagedData = new LinkedList<>();
    @Setter
    static File folder;
    static final String file1 = "ALL_DATA.json";
    static final String file2 = "averagedData.json";//"finalData.json";
    static int amountErrors = 0;
    static int amountWarnings;


    public static void analyser(boolean aggregated, List<JSONObject> dataReceived, long startTime,
                                long lastReceivedTime, int amountSensors, int throughput, int threadAmount) throws IOException {
        if (aggregated) { //aggregated Data
            JsonFileHandler.readJsonFile(folder, file2, averagedData);
            System.out.println("Aggregated data received");
        } else {
            JsonFileHandler.readJsonFile(folder, file1, dataSent);
            System.out.println("Normal data received");
        }

        List<JSONObject> onlyWindows = getOnlyWindows(dataReceived);

        dataReceived.removeAll(onlyWindows);

        List<JSONObject> onlyMaxWindows = getOnlyMaxWindows(onlyWindows);

        writeJsonFile(folder, "maxWindowsFromSystem", onlyMaxWindows);
        writeJsonFile(folder, "windowsFomSystem", onlyWindows);
        writeJsonFile(folder, "error_warnings_system", dataReceived);

        //send data to the comparer to validate the peeks
        String message = comparing(onlyMaxWindows, dataReceived, aggregated);

        analyse(dataReceived);

        // End time
        long elapsedTime = lastReceivedTime - startTime;
        double elapsedTimeInSeconds = elapsedTime / 1000.0;
        String time = String.format("%.5f", elapsedTimeInSeconds);
        double timeInSeconds = Double.parseDouble(time);

        // Get the Current Date and Time
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(currentDate);

        // Information after Receiving Data
        String output =
                "\n_________________________________________________________________________________________________\n" +
                formattedDate +
                "\nData received in " + time + " seconds" +
                "\nAmount Warnings in data: " + amountWarnings +
                "\nAmount Errors in data: " + amountErrors +
                "\nAmount of data received: " + throughput +
                "\nAmount of Sensors used: " + amountSensors +
                "\nAmount of Threads used: " + threadAmount +
                "\n\n" + message +
                "\n.................................................." +
                "\nPerformance metrics:"+
                "\nData latency: " + String.format("%.8f", (timeInSeconds / throughput)) + " seconds for one datapoint" +
                "\nData throughput: " + String.format("%.3f", (throughput / elapsedTimeInSeconds)) + " per second" +
                "\nThread efficiency: " + String.format("%.3f", (double) amountSensors / threadAmount) +
                "\n_________________________________________________________________________________________________";
        System.out.println(output);

        writeFile(folder, "analysis", output);

    }

    private static void analyse(List<JSONObject> receivedData) {
        for (JSONObject json : receivedData) {
            if (json.has("WarningMessage")) {
                amountWarnings++;
            }
            if (json.getJSONObject("data").has("Error")) {
                amountErrors++;
            }
        }
    }


    private static List<JSONObject> getOnlyMaxWindows(List<JSONObject> dataReceived) {
        dataReceived.sort(Comparator.comparingInt((JSONObject obj) -> obj.getJSONObject("data").getInt("id"))
                .thenComparingInt(obj -> obj.getInt("startTime")));

        List<JSONObject> onlyMaxWindows = new LinkedList<>();
        int id;
        int start;
        JSONObject json;
        for(int i = 0; i < dataReceived.size(); i++) {
            System.out.println(dataReceived.get(i).getJSONObject("data").getInt("id") + " " + dataReceived.get(i).getInt("startTime") + " " + dataReceived.get(i).getInt("endTime"));
            if(i == dataReceived.size() - 1) {
                onlyMaxWindows.add(dataReceived.get(i));
                break;
            }
            id = dataReceived.get(i).getJSONObject("data").getInt("id");
            start = dataReceived.get(i).getInt("startTime");
            json = dataReceived.get(i+1);
            if(json.getInt("startTime") != start && id >= 0) {
                onlyMaxWindows.add(dataReceived.get(i));
            }
        }
        onlyMaxWindows.sort(Comparator.comparingInt((JSONObject obj) -> obj.getInt("startTime")));
        return onlyMaxWindows;
    }

    private static List<JSONObject> getOnlyWindows(List<JSONObject> dataReceived) {
        List<JSONObject> onlyWindows = new LinkedList<>();
        for(JSONObject data : dataReceived) {
            if(data.has("startTime")) {
                onlyWindows.add(data);
            }
        }
        return onlyWindows;
    }
}
