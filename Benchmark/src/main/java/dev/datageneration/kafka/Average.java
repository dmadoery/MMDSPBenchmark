package dev.datageneration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Properties;

public class Average {

    private static final int INTERVAL = 5; // Number of entries to consider for averaging

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregated-data-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        StreamsBuilder builder = new StreamsBuilder();

        String inputTopic = "f1";
        String outputTopic = "aggregated";

        KStream<String, String> sensorStream = builder.stream(inputTopic);

        KGroupedStream<String, String> groupedStream = sensorStream.groupBy(
                (key, value) -> {
                    JSONObject json = new JSONObject(value);
                    JSONObject data = json.getJSONObject("data");
                    return data.getString("type") + "-" + data.getInt("id");
                },
                Grouped.with(Serdes.String(), Serdes.String())
        );

        KTable<String, String> aggregatedTable = groupedStream.aggregate(
                () -> new JSONObject().toString(), // Initial empty JSON
                (key, value, aggregate) -> aggregateData(value, aggregate),
                Materialized.<String, String>as(Stores.inMemoryKeyValueStore("aggregated-store"))
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.String())
        );

        aggregatedTable.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static String aggregateData(String newValue, String aggregate) {
        JSONObject newJson = new JSONObject(newValue);
        JSONObject aggregateJson = aggregate.isEmpty() ? new JSONObject() : new JSONObject(aggregate);

        JSONObject newData = newJson.getJSONObject("data");
        String type = newData.getString("type");

        JSONObject dataObject = aggregateJson.has("data") ? aggregateJson.getJSONObject("data") : new JSONObject();
        LinkedList<Double> buffer = dataObject.has(type) ?
                new LinkedList<>(dataObject.optJSONArray(type).toList().stream().map(o -> ((Number) o).doubleValue()).toList()) :
                new LinkedList<>();

        for (String metric : newData.keySet()) {
            if (!newData.has(metric) || metric.equals("type") || metric.equals("id")) continue;

            double value = newData.getDouble(metric);
            buffer.add(value);

            if (buffer.size() > INTERVAL) buffer.removeFirst();

            double average = buffer.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            dataObject.put("average" + capitalize(metric), average);
        }

        dataObject.put("type", type);
        aggregateJson.put("data", dataObject);

        int tick = newJson.getInt("tick");
        aggregateJson.put("tick", tick);
        aggregateJson.put("interval", new int[]{Math.max(1, tick - INTERVAL + 1), tick});

        return aggregateJson.toString();
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
