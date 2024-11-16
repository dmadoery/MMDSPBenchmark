package dev.datageneration.simulation.types;

import dev.datageneration.simulation.RandomData;

import java.util.List;

public record DoubleType(double min, double max) implements DataType {

    @Override
    public String sample() {
        return String.valueOf(RandomData.getRandom(min, max));
    }

    @Override
    public List<String> getData() {
        return List.of();
    }
}
