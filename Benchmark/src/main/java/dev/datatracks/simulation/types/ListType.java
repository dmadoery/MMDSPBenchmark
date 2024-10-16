package dev.datatracks.simulation.types;

import static dev.datatracks.RandomData.getRandom;

import dev.datatracks.RandomData;
import java.util.List;

public record ListType(List<String> choices) implements DataType {

    @Override
    public String sample() {
        return choices.get( RandomData.random.nextInt() % choices.size() );
    }

}
