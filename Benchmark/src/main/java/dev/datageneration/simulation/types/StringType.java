package dev.datageneration.simulation.types;

import dev.datageneration.simulation.RandomData;

import java.util.List;

public record StringType() implements DataType {

    @Override
    public String sample(String name) {
        String[] dir = new String[] {"left", "right", "straight"};
        int num = (int)(RandomData.getRandom(0,2));
        return dir[num];
    }

    @Override
    public List<String> getData() {
        return List.of();
    }
}
