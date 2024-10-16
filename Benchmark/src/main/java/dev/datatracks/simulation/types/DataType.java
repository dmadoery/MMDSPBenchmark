package dev.datatracks.simulation.types;


public interface DataType {

    /**
     * Creates the next random element of that data type.
     *
     * @return the created data
     */
    String sample();
}

