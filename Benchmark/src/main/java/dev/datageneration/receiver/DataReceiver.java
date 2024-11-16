package dev.datageneration.receiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datageneration.jsonHandler.JsonFileHandler;
import dev.datageneration.sending.JavalinTester;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.datageneration.jsonHandler.JsonParser.parseToJsonNodeList;
import static dev.datageneration.jsonHandler.JsonParser.parseToJsonNodeSet;

public class DataReceiver {
    static int throughput = 0;
    static List<JSONObject> dataReceived = new LinkedList<>();
    static List<JSONObject> dataSent = new LinkedList<>();
    static final String file1 = "ALL_DATA.json";
    static final String file2 = "aggregatedData.json";
    static final String file3 = "windowedData.json";
    static int missingEntries = 0;
    static int missMatch = 0;
    static boolean match = false;
    static int amountSensors;
    static int doubleEntries;
    static int earlySent;
    static int amountWarnings;
    static long startTime;

    /*
     * TODO: create data aggregator (do something with the data) how and with kafka?
     *
     * measure the time
     * amount of data points per timeunit (throughput)
     * correctness of data
     * amount of different sensors sending data
     * loss of data
     *
     */
    //TODO: Use kafka and not Javalin

    public static void receive(boolean readAggregatedData, boolean readWindowedData) throws IOException, InterruptedException {
        long lastReceivedTime;
        while (true) {
            JSONObject obj = JavalinTester.receiving();
            if (obj.has("end") && obj.getBoolean("end")) {
                lastReceivedTime = System.currentTimeMillis();
//                System.out.println("end Time");
//                TimeUnit.SECONDS.sleep(1);
                break;
            } else if (obj.has("start")) {
//                System.out.println("start time");
                startTime = System.currentTimeMillis();
                amountSensors = obj.getInt("start");
                continue;
            } else if (obj.has("null") && obj.getBoolean("null")) {
                continue;
            }
            dataReceived.add(obj);
            throughput++;
        }
//        System.out.println(JavalinTester.getMessages().size());

        if (readAggregatedData && !readWindowedData) { //aggregated Data
            JsonFileHandler.readJsonFile(file2, dataSent);
            System.out.println("Aggregated data received");
        } else if (!readAggregatedData && readWindowedData) {
            JsonFileHandler.readJsonFile(file3, dataSent);
            System.out.println("Windowed data received");
        } else {
           JsonFileHandler.readJsonFile(file1, dataSent);
           System.out.println("All data received");
        }
        
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> receivedSet = parseToJsonNodeSet(dataReceived, mapper);
        List<JsonNode> sentNodes = parseToJsonNodeList(dataSent, mapper);
        
        // Compare the two JSONArrays if there are any missing entries or mismatches
        analyse(sentNodes, receivedSet);

        if (dataReceived.size() > dataSent.size()) {
            doubleEntries = dataReceived.size() - dataSent.size();
        } else if (dataReceived.size() < dataSent.size()) {
            missingEntries = dataSent.size() - dataReceived.size();
        }

        // End time
        long elapsedTime = lastReceivedTime - startTime;
        double elapsedTimeInSeconds = elapsedTime / 1000.0;

            // Information after Receiving Data
        System.out.println("_________________________________________________________________________________________________\n" +
                "Data received in " + String.format("%.3f seconds", elapsedTimeInSeconds) + " seconds\n" +
                "Mismatches in data: " + missMatch +
                "\nMissing Entries in data: " + missingEntries +
                "\nDouble Entries in data: " + doubleEntries +
                "\nEarly Sent in data: " + earlySent +
                "\nAmount Warnings in data: " + amountWarnings +
                "\nThroughput of data: " + throughput +
                "\nAmount of Sensors used: " + amountSensors);
    }

    private static void analyse(List<JsonNode> sentNodes, List<JsonNode> receivedSet) {
        int min = -1;
        for (JsonNode received : sentNodes) {
            JSONObject sentJson = new JSONObject(received.toString());
            JSONObject receivedJson = new JSONObject(sentJson.toString());
            if(receivedJson.has("WarningMessage")) {
                amountWarnings++;
            }
            int tick = sentJson.getInt("tick");
            // Check if the sent entry is missing in the received set
            if (!sentNodes.contains(received)) {
                missingEntries++;
            }

            // Check if the entry was sent early (tick comparison)
            if (tick < min) {
                earlySent++;
            }
            min = Math.max(min, tick);
        }
    }

}