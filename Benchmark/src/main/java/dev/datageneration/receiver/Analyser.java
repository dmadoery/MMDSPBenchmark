package dev.datageneration.receiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datageneration.jsonHandler.JsonFileHandler;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static dev.datageneration.jsonHandler.JsonParser.parseToJsonNodeList;
import static dev.datageneration.jsonHandler.JsonParser.parseToJsonNodeSet;

public class Analyser {
    static List<JSONObject> dataSent = new LinkedList<>();
    static final File folder = new File("src/main/resources");
    static final String file1 = "ALL_DATA.json";
    static final String file2 = "aggregatedData.json";
    static final String file3 = "windowedData.json";
    static int missingEntries = 0;
    static int missMatch = 0;
    static int doubleEntries;
    static int earlySent;
    static int amountWarnings;
    static boolean allReceived = false;


    public static void analyser(boolean readAggregatedData, boolean readWindowedData, List<JSONObject> dataReceived, long startTime, long lastReceivedTime, int amountSensors, int throughput) throws IOException {
        if (readAggregatedData && !readWindowedData) { //aggregated Data
            JsonFileHandler.readJsonFile(folder, file2, dataSent);
            System.out.println("Aggregated data received");
        } else if (!readAggregatedData && readWindowedData) {
            JsonFileHandler.readJsonFile(folder, file3, dataSent);
            System.out.println("Windowed data received");
        } else {
            JsonFileHandler.readJsonFile(folder, file1, dataSent);
            System.out.println("All data received");
        }

        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> receivedSet = parseToJsonNodeSet(dataReceived, mapper);
        List<JsonNode> sentNodes = parseToJsonNodeList(dataSent, mapper);

        // Compare the two JSONArrays if there are any missing entries or mismatches
        analyse(sentNodes, receivedSet);

            if (dataReceived.size() > dataSent.size()) {
            doubleEntries = dataReceived.size() - dataSent.size();
            allReceived = true;
        } else if (dataReceived.size() < dataSent.size()) {
            missingEntries = dataSent.size() - dataReceived.size();
        } else {
            allReceived = true;
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
                "\nThroughput of data: " + throughput + " | All data received: " + allReceived +
                "\nAmount of Sensors used: " + amountSensors);
    }

private static void analyse(List<JsonNode> sentNodes, List<JsonNode> receivedSet) {
    int min = -1;
    for (JsonNode received : sentNodes) {
        JSONObject sentJson = new JSONObject(received.toString());
        JSONObject receivedJson = new JSONObject(sentJson.toString());
        if (receivedJson.has("WarningMessage")) {
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
