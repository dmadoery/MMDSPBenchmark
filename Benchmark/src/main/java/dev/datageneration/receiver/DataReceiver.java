package dev.datageneration.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datageneration.sending.JavalinTester;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;;

public class DataReceiver {
    static int throughput = 0;
    static JSONArray dataReceived = new JSONArray();
    static JSONArray dataSent = new JSONArray();
    static final File folder = new File("src/main/resources");
    static final String file = "ALL_DATA.json";
    static int missingEntries = 0;
    static int missMatch = 0;
    static int amountSensors;
    /*
     * measure the time
     * amount of data points per timeunit (throughput)
     * correctness of data
     * amount of different sensors sending data
     * loss of data
     *
     */
    //TODO: Use kafka and not Javalin

    public static void receive(long startTime, int amountSens) throws IOException {
        amountSensors = amountSens;
        do {
            JSONObject obj = JavalinTester.receiving();
            dataReceived.put(obj);
            throughput ++;
        } while (!JavalinTester.getMessages().isEmpty());

        FileReader fReader = new FileReader(new File(folder, file));
        BufferedReader bReader = new BufferedReader(fReader);

        // Read the entire content of the JSON file that was sent
        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            jsonContent.append(line);
        }
        bReader.close();

        // Parse the JSON content
        dataSent = new JSONArray(jsonContent.toString());

        ObjectMapper mapper = new ObjectMapper();
        // Compare the two JSONArrays if there are any missing entries or mismatches
        for (int i = 0; i < dataSent.length(); i++) {
            JSONObject objSent = dataSent.getJSONObject(i);
            JSONObject objRec;
            if(dataReceived.get(i) != null && dataReceived.length() > i) {
                objRec = dataReceived.getJSONObject(i);
                if(mapper.readTree(String.valueOf(objRec)).equals(mapper.readTree(String.valueOf(objSent)))) {
                    missMatch++;
                }
            }
            else {
                missingEntries++;
            }
        }

        // End time
        long endTime = System.currentTimeMillis() - startTime;

        // Information after Receiving Data
        System.out.println("Data received in " + (endTime / 1000) + " seconds\n" +
                "Mismatches in data: " + missMatch +
                "\nMissing Entries in data: " + missingEntries +
                "\nThroughput of data: " + throughput +
                "\nAmount of Sensors used: " + amountSensors);
    }
}