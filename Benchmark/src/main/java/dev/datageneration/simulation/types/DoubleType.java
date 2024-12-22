package dev.datageneration.simulation.types;

import dev.datageneration.simulation.RandomData;

import java.util.List;

public record DoubleType(double min, double max) implements DataType {

    @Override
    public String sample(String name) {
        return String.valueOf(RandomData.getRandomWithProbability(min, max, name));
    }

    @Override
    public List<String> getData() {
        return List.of();
    }
}
