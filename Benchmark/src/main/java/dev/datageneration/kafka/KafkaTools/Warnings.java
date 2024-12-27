package dev.datageneration.kafka.KafkaTools;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Warnings {

    private static final Set<String> existingTopics = new HashSet<>();
    static Properties props = new Properties();
    static Properties producerProps = new Properties();
    static Producer<String, String> producer;

    public static void getProps(Properties props) {
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE); // Ensure exactly-once semantics
    }

    public static void main(String[] args) {
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "warning-app");
        getProps(props);

        // Configure producer
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("acks", "all");  // Ensure that all replicas acknowledge the message
        producerProps.put("retries", 3);   // Retry on failure
        producerProps.put("enable.idempotence", "true");

        // Initialize the producer once
        producer = new KafkaProducer<>(producerProps);

        StreamsBuilder builder = new StreamsBuilder();

        String inputTopic = "f1";
        String finalTopic = "f3";
        KStream<String, String> sensorStream = builder.stream(inputTopic);

        // Ensure the input topic exists before processing
        checkAndCreateTopic(inputTopic);

        // Process the sensor data and send it to the appropriate topic
        sensorStream.foreach((key, value) -> {
            try {
                JSONObject json = new JSONObject(value);
                if (!json.has("data") || !(json.get("data") instanceof JSONObject)) {
                    System.err.println("Invalid message format: 'data' field missing or not a JSONObject. Skipping message: " + value);
                    return;
                }
                JSONObject data = json.getJSONObject("data");
                if (!data.has("type")) {
                    System.err.println("Invalid message format: 'type' field missing in 'data'. Skipping message: " + value);
                    return;
                }

                String warning;
                JSONObject warningJson = new JSONObject(value);
                String type = data.getString("type");

                if(warningJson.getJSONObject("data").has("Error")) {
                    producer.send(new ProducerRecord<>(finalTopic, key, warningJson.toString()), (metadata, exception) -> {
                        if (exception != null) {
                            System.err.println("Failed to send message: " + exception.getMessage());
                            // Optionally, handle the failure (e.g., retry logic)
                        } else {
                            System.out.println("Message sent to topic f3: " + value);
                        }
                    });
                } else {
                    boolean send = false;
                    switch (type) {
                        case "tire":
                            if(data.getInt("temperature tire") > 110) {
                                warning = "position:" + data.getInt("position") + " is to hot.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if (data.getInt("wear") > 90) {
                                warning = "position:" + data.getInt("position") + " is worn down.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if (data.getDouble("pressure psi") > 30) {
                                warning = "position:" + data.getInt("position") + " to high pressure.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        case "heat":
                            if(data.getInt("temperature c") > 50) {
                                warning = " to hot temperature.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        case "engine":
                            if(data.getDouble("oil_pressure") > 7) {
                                warning = " oil pressure to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if(data.getInt("temperature engine") > 600) {
                                warning = " is overheating.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if(data.getDouble("fuel_pressure") > 5) {
                                warning = " fuel pressure to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if (data.getLong("rpm") > 18000) {
                                warning = " rpm to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if(data.getDouble("exhaust") > 1.2) {
                                warning = " exhaust fumes not good.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if(data.getInt("fuelFlow") > 120) {
                                warning = " fuelFlow to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        case "fuelPump":
                            if(data.getLong("ml/min") > 4000) {
                                warning = " fuel flow is to low.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if (data.getInt("temperature fuelP") > 60) {
                                warning = " fuel-pump temperature is to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        case "brake":
                            if(data.getInt("temperature brake") > 1000) {
                                warning = " is overheating.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if(data.getInt("wear") > 90) {
                                warning = " is worn down.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if(data.getInt("brake_pressure") > 10 ) {
                                warning = " brake pressure to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        case "gForce":
                            if(data.getDouble("g-lateral") > 6) {
                                warning = " g-force lateral is high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            } else if (data.getDouble("g-longitudinal") > 5) {
                                warning = " g-force longitudinal is high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;
                        case "accelerometer":
                            if (data.getInt("throttlepedall") > 100) {
                                warning = " throttlepedall is high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        case "speed":
                            if(data.getDouble("wind speed") > 200) {
                                warning = " wind speed is to high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            if (data.getDouble("kph") > 360) {
                                warning = " kph is high.";
                                warningJson = createErrorObject(json, type, warning);
                                send = true;
                            }
                            break;

                        default:
                            send = false;
                            break;
                    }
                    // Send the message
                    if(send) {
                        producer.send(new ProducerRecord<>(finalTopic, key, warningJson.toString()), (metadata, exception) -> {
                            if (exception != null) {
                                System.err.println("Failed to send message: " + exception.getMessage());
                                // Optionally, handle the failure (e.g., retry logic)
                            } else {
                                System.out.println("Message sent to topic f3: " + value);
                            }
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the Kafka Streams application
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Check if the given topic exists. If not, create it.
     *
     * @param topic The name of the topic.
     */
    public static void checkAndCreateTopic(String topic) {
        if (!existingTopics.contains(topic)) {
            try (AdminClient adminClient = AdminClient.create(props)) {
                // Check if the topic exists
                if (!adminClient.listTopics().names().get().contains(topic)) {
                    // If not, create the topic
                    NewTopic newTopic = new NewTopic(topic, 1, (short) 1); // 1 partition, 1 replica
                    adminClient.createTopics(Collections.singleton(newTopic));
                    existingTopics.add(topic);
                    System.out.println("Topic created: " + topic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject  createErrorObject(JSONObject jsonObject, String type, String warning) {
        JSONObject error = new JSONObject();
        error.put("data", jsonObject.getJSONObject("data"));
        error.put("WarningMessage", type + " id:" + jsonObject.getJSONObject("data").getInt("id") + " " + warning);
        error.put("tick", jsonObject.getInt("tick"));
        return error;
    }
}

