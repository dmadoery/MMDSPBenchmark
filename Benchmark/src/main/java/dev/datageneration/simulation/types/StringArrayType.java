package dev.datageneration.simulation.types;

import dev.datageneration.simulation.RandomData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record StringArrayType() implements DataType {

    @Override
    public String sample(String name) {

        String[] data = new String[] {"left", "right", "break", "accelerate", "straight"};
        int rand = (int)RandomData.getRandom(0,4);
        return data[rand];
    }

    @Override
    public List<String> getData() {
        List<String> data = new ArrayList<>();
        Random random = new Random();
        int entries = random.nextInt((50 - 5) + 1) + 5;
        for (int i = 0; i < entries; i++) {
            data.add(sample(""));
        }
        return data;
    }
}
