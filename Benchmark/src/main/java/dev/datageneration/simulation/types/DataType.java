package dev.datageneration.simulation.types;

import java.util.List;

public interface DataType {
    String sample();

    List<String> getData();
}
