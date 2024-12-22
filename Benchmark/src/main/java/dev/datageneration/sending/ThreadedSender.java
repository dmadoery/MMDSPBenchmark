package dev.datageneration.sending;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadedSender {
    static JSONArray jsonArray;
    @Setter
    static File pathNormal;
    static final String name = "/ALL_DATA.json";
    static BlockingQueue<JSONObject> messageQueue = new ArrayBlockingQueue<>(200000000);
    static List<JSONArray> frequencyData;
    public static List<SingleThread> threads = new ArrayList<>();

    //Kafka Topic
    static String topic = "f1";
    static KafkaProducer<String, String> producer;
    private static final String KAFKA_BROKER = "localhost:9092";

    @Getter
    static int timeStep = 0;
    @Getter
    static boolean stop = false;
    static int maxFrequency;
    static long loopStart = 0;

    public static void sendThreaded(boolean aggregated, int threadAmount, long durationTimeStep) throws IOException, InterruptedException {
        if (!aggregated) {
            topic = "f3";
        }
        jsonArray = readJsonFile(pathNormal + name);
        frequencyData = mapFrequency(jsonArray);
        addAllFrequencies();

        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);

        ExecutorService executor = Executors.newFixedThreadPool(threadAmount);
        SingleThread thread = null;
        for (int i = 0; i < threadAmount; i++) {
            thread = new SingleThread(messageQueue, topic , producer);
            threads.add(thread);
            executor.submit(thread);
        }
        while (true) {
            JSONObject message = messageQueue.peek();
            loopStart = System.nanoTime();
            if (timeStep ==  maxFrequency + 1) {
                break;
            }
            if (message == null) {
                timeStep++;
                continue;
            }
            if (message.getInt("tick") > timeStep) {
                int freqNew = message.getInt("tick");
                while(freqNew != timeStep) {
                    long difference = System.nanoTime() - loopStart;
                    TimeUnit.NANOSECONDS.sleep(durationTimeStep * 1000000 - difference);
                    timeStep++;
                }
            }
        }
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
            }
        }
    }

    public static void addNextFrequency() {
        if(timeStep <= maxFrequency) {
            JSONArray first = frequencyData.get(timeStep - 1);
            for (int j = 0; j < first.length(); j++) {
                messageQueue.add(first.getJSONObject(j));
            }
        }
    }

    public static void frequencyAdder(int f) {
        timeStep = f;
    }
}
