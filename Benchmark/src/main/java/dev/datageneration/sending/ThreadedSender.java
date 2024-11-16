package dev.datageneration.sending;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadedSender {
    static JSONArray jsonArray;
    static final String path = "src/main/resources/ALL_DATA.json";
    static final String pathAg = "src/main/resources/aggregatedData.json";
    static final String pathWi = "src/main/resources/windowedData.json";
    static BlockingQueue<JSONObject> messageQueue = new ArrayBlockingQueue<>(20000);
    static List<JSONArray> frequencyData;
    public static List<SingleThread> threads = new ArrayList<>();

    //TODO: Implement Kafka connection
    //Kafka Topic
    static String topic = "F1";

    @Getter
    static int timeStep = 1;
    @Getter
    static boolean stop = false;
    static int maxFrequency;

    public static void sendThreaded(int amountSensors, boolean sendAggregated, boolean sendWindowed) throws IOException, InterruptedException {
        JavalinTester.starting();
        if (sendAggregated) {
            jsonArray = readJsonFile(pathAg);
        } else if (sendWindowed) {
            jsonArray = readJsonFile(pathWi);
        } else {
            jsonArray = readJsonFile(path);
        }
        frequencyData = mapFrequency(jsonArray);
        addAllFrequencies();
//        addNextFrequency();

        JSONObject startMessage = new JSONObject();
        startMessage.put("start", amountSensors);
        JavalinTester.sending(startMessage);
        int threadAmount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadAmount);
        SingleThread thread = null;
        for (int i = 0; i < threadAmount; i++) {
            thread = new SingleThread(messageQueue, topic /*, kafka*/); //TODO: implement kafka
            threads.add(thread);
            executor.submit(thread);
        }
        while (true) {
            JSONObject message = messageQueue.peek();
            //System.out.println("Tick is: " + timeStep);
            if (timeStep ==  maxFrequency+1) {
//                System.out.println("Stopping While loop");
                break;
            }
            if (message == null) {
                timeStep++;
                continue;
            } else if (message.getInt("tick") > timeStep) {
                int freqNew = message.getInt("tick");
                frequencyAdder(freqNew);
//                TimeUnit.MILLISECONDS.sleep(5); //TODO: if i remove this entries get lost...
//                addNextFrequency();
//                System.out.println("Added timeStep: " + timeStep);
            }

        }
//        Thread.sleep(5000);
//        System.out.println(thread.queue.size());
//        System.out.println("End of while loop");
        JSONObject endMessage = new JSONObject();
        endMessage.put("end", true);
        JavalinTester.sending(endMessage);
//        System.out.println("Message Queue is empty");

        stop = true;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force shutdown after timeout
            }
        } catch (InterruptedException e) {
            executor.shutdownNow(); // Force shutdown if interrupted
            Thread.currentThread().interrupt();
        }
        JavalinTester.stop();
    }

    public static JSONArray readJsonFile(String path) throws IOException {
        FileReader fReader = new FileReader(path);
        BufferedReader bReader = new BufferedReader(fReader);

        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            jsonContent.append(line);
//            System.out.println(line);
        }
        bReader.close();
        return new JSONArray(jsonContent.toString());
    }

    public static List<JSONArray> mapFrequency(JSONArray jsonArray) {
        int max = 0;
        Map<Integer, JSONArray> frequencyMap = new TreeMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);

            int tick = entry.getInt("tick");
            if(tick > max) {
                max = tick;
            }
            frequencyMap //this creates a new JSONArray for each timeStep number if it does not already exist and adds the JsonObject to it.
                    .computeIfAbsent(tick, k -> new JSONArray())
                    .put(entry);

        }
        maxFrequency = max;
        return new ArrayList<>(frequencyMap.values());
    }

    public static void addAllFrequencies() {
        for (JSONArray first : frequencyData) {
            for (int j = 0; j < first.length(); j++) {
                messageQueue.add(first.getJSONObject(j));
//                System.out.println("Added Entrie: " + first.getJSONObject(j).toString());
            }
        }
    }

    public static void addNextFrequency() {
        if(timeStep <= maxFrequency) {
            JSONArray first = frequencyData.get(timeStep - 1);
//            System.out.println(first.getJSONObject(0).getInt("tick"));
//            System.out.println("Tick: " + (timeStep));
            for (int j = 0; j < first.length(); j++) {
                messageQueue.add(first.getJSONObject(j));
            }
//            frequencyData.removeFirst();
        }
    }

    public static void frequencyAdder(int f) {
        timeStep = f;
    }
}
