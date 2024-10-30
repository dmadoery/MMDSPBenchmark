package dev.datageneration.sending;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadedSender {
    JSONArray jsonArray;
    static final String path = "src/main/resources/ALL_DATA.json";
    int maxFrequency = 100;
    BlockingQueue<JSONObject> messageQueue = new ArrayBlockingQueue<>(2000);
    List<JSONArray> frequencyData;
    //Kafka Topic
    String topic = "F1";

    public ThreadedSender() throws IOException {
        jsonArray = readJsonFile(path);
        frequencyData = mapFrequency(jsonArray);
        addNextFrequency();

        int threadAmount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadAmount);
        for (int i = 0; i < threadAmount; i++) {
            executor.submit(new Threads(messageQueue, topic /*, kafka*/)); //TODO: implement kafka
        }
        while (!messageQueue.isEmpty()) {
            if(messageQueue.size() < 2) {
                addNextFrequency();
            }
        }
    }

    public JSONArray readJsonFile(String path) throws IOException {
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

    public List<JSONArray> mapFrequency(JSONArray jsonArray) {
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

    public void addNextFrequency() {
        if(!frequencyData.isEmpty()) {
            JSONArray first = frequencyData.getFirst();
            for (int j = 0; j < first.length(); j++) {
                messageQueue.add(first.getJSONObject(j));
            }
            frequencyData.removeFirst();
        }
    }
}
