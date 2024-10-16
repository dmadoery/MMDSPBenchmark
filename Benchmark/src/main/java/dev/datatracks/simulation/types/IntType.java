package dev.datatracks.simulation.types;


import dev.datatracks.RandomData;


/**
 * Datatype which samples a random element between a maximum and a minimum, using a normal distribution.
 *
 * @param min The minimal value
 * @param max The maximum value
 */
public record IntType(int min, int max) implements DataType {

    @Override
    public String sample() {
        return String.valueOf(RandomData.getRandom(min, max));
    }

}
