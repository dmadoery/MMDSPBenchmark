package dev.datageneration.receiver;

import dev.datageneration.analyse.Analyser;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import java.io.*;
import java.time.Duration;
import java.util.*;

public class DataReceiver {
    static int throughput = 0;
    static List<JSONObject> dataReceived = new LinkedList<>();
    static long startTime;
    static long lastReceivedTime;
    static String topic = "f3";
    static String KafkaBroker = "localhost:9092";
    static Properties props = new Properties();
    static String groupID = "15";
    static long inactivity;

    public static void receive(boolean aggregated) throws IOException, InterruptedException {
        props.put("bootstrap.servers", KafkaBroker);
        props.put("group.id", groupID);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest"); // Start from the beginning of the topic if no offset is found

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(topic));
        try {
            inactivity = System.currentTimeMillis();
            boolean b = true;
            while (b) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                if(!records.isEmpty() ) {
                    for (ConsumerRecord<String, String> record : records) {
                        if(!record.value().isEmpty()) {
                            String recordValue = record.value();
                            JSONObject json = new JSONObject(recordValue);
                            dataReceived.add(json);
                            if(throughput == 0) {
                                startTime = System.currentTimeMillis();
                            }
                            throughput++;
                            lastReceivedTime = System.currentTimeMillis();
                        }

                    }
                    inactivity = System.currentTimeMillis();

                } else {
                    long now = System.currentTimeMillis();
                    if (now - inactivity > 10000) {
                        System.out.println("No messages received for 10 seconds. Ending loop.");
                        b = false;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the consumer
            consumer.close();
        }
        Analyser.analyser(aggregated, dataReceived, startTime, lastReceivedTime, throughput);
    }
}