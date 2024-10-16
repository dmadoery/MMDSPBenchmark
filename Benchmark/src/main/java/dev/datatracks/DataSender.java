package dev.datatracks;

import com.opencsv.CSVReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.util.*;

public class DataSender {
    private static final String name = "ALL_DATA";
    private static final File inputFile = new File("src/main/resources/" + name + ".csv");
    private static final String TOPIC = "F1Sensors";
    private static final String KAFKA_BROKER = "localhost:9092"; // Replace with your Kafka broker address
    private KafkaProducer<String, String> producer;

    public DataSender(boolean isTestMode) {
        // Initialize Kafka Producer only if not in test mode
        if (!isTestMode) {
            Properties props = new Properties();
            props.put("bootstrap.servers", KAFKA_BROKER);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            producer = new KafkaProducer<>(props);
        }
    }

    /**
     * Reads the data form the csv file and sends it to Kafka.
     * @param isTestMode boolean
     * @throws Exception
     */
    public void processData(boolean isTestMode) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(inputFile))) {
            String[] header = reader.readNext(); // Read header line
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (isTestMode) {
                    mockSendToKafka(line);
                } else {
                    sendToKafka(line);
                }
            }
        }

        // Close Kafka producer only if it was initialized
        if (!isTestMode) {
            producer.close();
        }
    }

    // Method to send data to Kafka
    private void sendToKafka(String[] dataEntry) {
        String message = String.join(";", dataEntry);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
        producer.send(record);
        System.out.printf("Sent message to topic %s: %s%n", TOPIC, message);
    }

    // Mock send method
    private void mockSendToKafka(String[] dataEntry) {
        String message = String.join(";", dataEntry);
        System.out.printf("Mock sent message to Kafka: %s%n", message);
    }
}


