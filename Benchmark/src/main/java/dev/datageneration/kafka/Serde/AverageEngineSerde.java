package dev.datageneration.kafka.Serde;

import dev.datageneration.kafka.AverageClass.AverageEngine;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;


import java.nio.ByteBuffer;
import java.util.Map;

public class AverageEngineSerde implements Serde<AverageEngine> {

    @Override
    public Serializer<AverageEngine> serializer() {
        return new AverageSerializer();
    }

    @Override
    public Deserializer<AverageEngine> deserializer() {
        return new AverageDeserializer();
    }

    public static class AverageSerializer implements Serializer<AverageEngine> {

        @Override
        public byte[] serialize(String topic, AverageEngine data) {
            if (data == null) {
                return null;
            }
            ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES * 3  + Integer.BYTES * 6 + Long.BYTES );
            buffer.putInt(data.temp);
            buffer.putLong(data.rpm);
            buffer.putInt(data.fuelFlow);
            buffer.putDouble(data.oilPressure);
            buffer.putDouble(data.fuelPressure);
            buffer.putDouble(data.exhaust);
            buffer.putInt(data.count);
            buffer.putInt(data.tickStart);
            buffer.putInt(data.tickEnd);
            buffer.putInt(data.id);
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

    public static class AverageDeserializer implements Deserializer<AverageEngine> {

        @Override
        public AverageEngine deserialize(String topic, byte[] data) {
            if (data == null || data.length == 0) {
                return null;
            }
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int temp = buffer.getInt();
            long rpm = buffer.getLong();
            int fuelFlow = buffer.getInt();
            double oilPressure = buffer.getDouble();
            double fuelPressure = buffer.getDouble();
            double exhaust = buffer.getDouble();
            int count = buffer.getInt();
            int tickS = buffer.getInt();
            int tickE = buffer.getInt();
            int id = buffer.getInt();
            return new AverageEngine(temp, rpm, fuelFlow, oilPressure, fuelPressure,exhaust, count, tickS, tickE, id);
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
