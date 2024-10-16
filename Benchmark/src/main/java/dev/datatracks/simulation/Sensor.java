package dev.datatracks.simulation;

import dev.datatracks.RandomData;
import dev.datatracks.aggregate.DataPoint;
import dev.datatracks.simulation.types.DataType;
import dev.datatracks.simulation.types.IntType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public abstract class Sensor {

    //Info of all possible data types, with their possible configurations.
    public static Map<String, DataType> dataTypes = new HashMap<>(){{
        put( "temperature celsius", new IntType( 80, 110 ) );
        put( "pressure psi", new IntType( 25, 30 ) );
        put( "kmp/h", new IntType( 0, 380 ) );
    }};


    @Getter
    final String name;

    /**
     * To which category the sensor belongs, is it a single number or does it return a complex construct usw.
     */
    final List<SensorCategory> sensorCategories;

    public Sensor(String name, SensorCategory sensorCategory) {
        this(name, List.of(sensorCategory));
    }

    public Sensor(String name, List<SensorCategory> sensorCategories ) {
        this.sensorCategories = sensorCategories;
        this.name = name;
    }


    /**
     * Creates a single datapoint of this specific sensor using the provided random generator.
     *
     * @return the created datapoint
     */
    public abstract DataPoint createDataPoint();


    public static Sensor parse(List<String> strings) {
        String type = strings.get(0);
        String id = strings.get(1);
        List<String> arguments = strings.subList(2, strings.size());
        return new S(type, id, arguments);
    }

    public abstract String[] getHeader();


    /**
     * Creates random entries for the sensor according to the data type.
     * @param dataType String
     * @return random entry of the given data type
     */
    public static String generateDataPoint(String dataType) {
        return dataTypes.get( dataType ).sample();
    }

}
