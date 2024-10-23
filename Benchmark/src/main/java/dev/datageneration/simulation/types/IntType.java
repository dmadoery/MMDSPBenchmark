package dev.datageneration.simulation.types;

import dev.datageneration.simulation.RandomData;

public record IntType(int min, int max) implements DataType {

    @Override
    public String sample() {
        return String.valueOf(RandomData.getRandom(min, max));
    }
}
