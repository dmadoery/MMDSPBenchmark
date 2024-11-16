package dev.datageneration.simulation.types;

import dev.datageneration.simulation.RandomData;

import java.util.List;

public record IntType(int min, int max) implements DataType {

    @Override
    public String sample() {
        return String.valueOf((int)(Math.round(RandomData.getRandom(min, max))));
    }

    @Override
    public List<String> getData() {
        return List.of();
    }
}
