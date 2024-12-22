package dev.datageneration.kafka.Serde;

import dev.datageneration.kafka.AverageClass.AverageTire;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;


import java.nio.ByteBuffer;
import java.util.Map;

public class AverageTireSerde implements Serde<AverageTire> {

    @Override
    public Serializer<AverageTire> serializer() {
        return new AverageSerializer();
    }

    @Override
    public Deserializer<AverageTire> deserializer() {
        return new AverageDeserializer();
    }

    public static class AverageSerializer implements Serializer<AverageTire> {

        @Override
        public byte[] serialize(String topic, AverageTire data) {
            if (data == null) {
                return null;
            }
            ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES * 2 + Integer.BYTES * 6);
            buffer.putDouble(data.temp);
            buffer.putDouble(data.pressure);
            buffer.putInt(data.count);
            buffer.putInt(data.tickStart);
            buffer.putInt(data.tickEnd);
            buffer.putInt(data.id);
            buffer.putInt(data.position);
            buffer.putInt(data.wear);
            return buffer.array();
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            // No configuration needed
        }

        @Override
        public void close() {
            // No resources to close
        }
    }

    public static class AverageDeserializer implements Deserializer<AverageTire> {

        @Override
        public AverageTire deserialize(String topic, byte[] data) {
            if (data == null || data.length == 0) {
                return null;
            }
            ByteBuffer buffer = ByteBuffer.wrap(data);
            double temp = buffer.getDouble();
            double pressure = buffer.getDouble();
            int count = buffer.getInt();
            int tickS = buffer.getInt();
            int tickE = buffer.getInt();
            int id = buffer.getInt();
            int position = buffer.getInt();
            int wear = buffer.getInt();
            return new AverageTire(temp, pressure, count, tickS, tickE, id, position, wear);
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            // No configuration needed
        }

        @Override
        public void close() {
            // No resources to close
        }
    }
}
