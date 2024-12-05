package dev.datageneration.receiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datageneration.jsonHandler.JsonFileHandler;
import dev.datageneration.sending.JavalinTester;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import java.io.*;
import java.time.Duration;
import java.util.*;

public class DataReceiver {
    static int throughput = 0;
    static int amountSensors;
    static List<JSONObject> dataReceived = new LinkedList<>();
    static long startTime;
    static long lastReceivedTime;
    static String topic = "f1";
    static String KafkaBroker = "localhost:9092";
    static Properties props = new Properties();
    static String groupID = "15";

    public static void receive(boolean aggregated, int threadAmount) throws IOException, InterruptedException {
        props.put("bootstrap.servers", KafkaBroker);
        props.put("group.id", groupID);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest"); // Start from the beginning of the topic if no offset is found

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(topic));
        try {
            boolean b = true;
            while (b) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {

                    String recordValue = record.value();
                    JSONObject json = new JSONObject(recordValue);
                    if (json.has("end")) {
                        lastReceivedTime = System.currentTimeMillis();
                        b = false;
                        break;
                    }
                    if (json.has("start")) {
                        startTime = System.currentTimeMillis();
                        amountSensors = json.getInt("start");
                        continue;
                    }
                    dataReceived.add(json);
//                    System.out.printf("Received message: " + record.toString() + "\n");
                    throughput++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the consumer
            consumer.close();
        }
        Analyser.analyser(aggregated, dataReceived, startTime, lastReceivedTime, amountSensors, throughput, threadAmount);
    }
}