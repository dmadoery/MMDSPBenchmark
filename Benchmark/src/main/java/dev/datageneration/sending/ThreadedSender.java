package dev.datageneration.sending;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadedSender {
    static JSONArray jsonArray;
    static final String path = "src/main/resources/ALL_DATA.json";
    static BlockingQueue<JSONObject> messageQueue = new ArrayBlockingQueue<>(2000);
    static List<JSONArray> frequencyData;

    //TODO: Implement Kafka connection
    //Kafka Topic
    static String topic = "F1";

    @Getter
    static int frequency = 1;
    @Getter
    static boolean stop = false;

    public static void sendThreaded() throws IOException, InterruptedException {
        JavalinTester.starting();
        jsonArray = readJsonFile(path);
        frequencyData = mapFrequency(jsonArray);
        addNextFrequency();
        

        int threadAmount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadAmount);
        for (int i = 0; i < threadAmount; i++) {
            executor.submit(new SingleThread(messageQueue, topic /*, kafka*/)); //TODO: implement kafka
        }
        while (!messageQueue.isEmpty()) {
            System.out.println(messageQueue.size());
            if(messageQueue.size() < 2) {
                addNextFrequency();
                TimeUnit.SECONDS.sleep(2);
                frequencyAdder();
            }
        }
        //TODO: threads stopping not working!!!
        stop();
        executor.shutdown();
        JavalinTester.stop();
    }

    public static JSONArray readJsonFile(String path) throws IOException {
        FileReader fReader = new FileReader(path);
        BufferedReader bReader = new BufferedReader(fReader);

        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            jsonContent.append(line);
        }
        bReader.close();
        return new JSONArray(jsonContent.toString());
    }

    public static List<JSONArray> mapFrequency(JSONArray jsonArray) {
        Map<Integer, JSONArray> frequencyMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);

            int freq = entry.getInt("freq");
            frequencyMap //this creates a new JSONArray for each frequency number if it does not already exist and adds the JsonObject to it.
                    .computeIfAbsent(freq, k -> new JSONArray())
                    .put(entry);

        }
        return new ArrayList<>(frequencyMap.values());
    }

    public static void addNextFrequency() {
        if(!frequencyData.isEmpty()) {
            JSONArray first = frequencyData.getFirst();
            for (int j = 0; j < first.length(); j++) {
                messageQueue.add(first.getJSONObject(j));
            }
            frequencyData.removeFirst();
        }
    }

    public static void frequencyAdder() {
        frequency++;
    }

    public static void stop() {
        stop = true;
    }
}
