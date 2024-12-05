package dev.datageneration.receiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datageneration.jsonHandler.JsonFileHandler;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static dev.datageneration.jsonHandler.JsonFileHandler.writeFile;
import static dev.datageneration.jsonHandler.JsonFileHandler.writeJsonFile;
import static dev.datageneration.jsonHandler.JsonParser.parseToJsonNodeList;
import static dev.datageneration.jsonHandler.JsonParser.parseToJsonNodeSet;

public class Analyser {
    static List<JSONObject> dataSent = new LinkedList<>();
    static final File folder = new File("src/main/resources");
    static final String file1 = "ALL_DATA.json";
    static final String file2 = "finalData.json";
    static int missingEntries = 0;
    static int missMatch = 0;
    static int amountErrors = 0;
    static int doubleEntries;
    static int earlySent;
    static int lateSent;
    static int amountWarnings;
    static boolean allReceived = false;


    public static void analyser(boolean aggregated, List<JSONObject> dataReceived, long startTime,
                                long lastReceivedTime, int amountSensors, int throughput, int threadAmount) throws IOException {
        if (aggregated) { //aggregated Data
            JsonFileHandler.readJsonFile(folder, file2, dataSent);
            System.out.println("Aggregated data received");
        } else {
            JsonFileHandler.readJsonFile(folder, file1, dataSent);
            System.out.println("Normal data received");
        }

        writeJsonFile(folder, "test", dataReceived);
//        ObjectMapper mapper = new ObjectMapper();
//        List<JsonNode> receivedSet = parseToJsonNodeSet(dataReceived, mapper);
//        List<JsonNode> sentNodes = parseToJsonNodeList(dataSent, mapper);

        // Compare the two JSONArrays if there are any missing entries or mismatches
        analyse(dataSent,dataReceived);

        if (dataReceived.size() > dataSent.size()) {
//            doubleEntries = dataReceived.size() - dataSent.size();
            allReceived = true;
        } else if (dataReceived.size() < dataSent.size()) {
//            missingEntries = dataSent.size() - dataReceived.size();
        } else {
            allReceived = true;
        }

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
//                "\nMismatches in data: " + missMatch +
                "\nMissing Entries in data: " + missingEntries +
                "\nDouble Entries in data: " + doubleEntries +
                "\nEarly Sent in data: " + earlySent +
                "\nLate Sent in data: " + lateSent +
                "\nAmount Warnings in data: " + amountWarnings +
                "\nAmount Errors in data: " + amountErrors +
                "\nAmount of data received: " + throughput +
                "\nAll data received: " + allReceived +
                "\nAmount of Sensors used: " + amountSensors +
                "\nAmount of Threads used: " + threadAmount +
                "\n.................................................." +
                "\nMetrics:"+
                "\nData latency: " + String.format("%.8f", (timeInSeconds / throughput)) + " seconds for one datapoint" +
                "\nData throughput: " + String.format("%.3f", (throughput / elapsedTimeInSeconds)) + " per second" +
                "\nThread efficiency: " + String.format("%.3f", (double) amountSensors / threadAmount) +
                "\n_________________________________________________________________________________________________";
        System.out.println(output);

        writeFile(folder, "analysis", output);

    }

    private static void analyse(List<JSONObject> sentData, List<JSONObject> receivedData) {
        List<JSONObject> check = new LinkedList<>();
        boolean equals = false;
        for (JSONObject sent : sentData) {
            for (JSONObject received : receivedData) {
                if (sent.getInt("tick") == received.getInt("tick") && sent.getJSONObject("data").similar(received.getJSONObject("data"))) {
                    equals = true;
                    break;
                }
            }
            if (!equals) {
                missingEntries++;
            }
        }
        JSONObject one = new JSONObject();
        JSONObject three = new JSONObject();
        for (int i = 0; i < receivedData.size(); i++) {
            if(i != 0) {
                one = new JSONObject(sentData.get(i-1).toString());
            }
            JSONObject two = new JSONObject(receivedData.get(i).toString());
            if(i < receivedData.size() - 1) {
                three = new JSONObject(receivedData.get(i+1).toString());
            }
            if (i > 0 && i < receivedData.size() - 1) {
                int tick1 = one.getInt("tick");
                int tick2 = two.getInt("tick");
                int tick3 = three.getInt("tick");
                if(tick1 < tick2 && tick2 > tick3) {
                    earlySent++;
                }
                if(tick1 > tick2 && tick2 < tick3) {
                    lateSent++;
                }
            }
            if (two.has("WarningMessage")) {
                amountWarnings++;
            }
            if (two.getJSONObject("data").has("Error")) {
                amountErrors++;
            }
            if (check.contains(receivedData.get(i))) {
                doubleEntries++;
            }
            check.add(receivedData.get(i));
        }

    }
}
