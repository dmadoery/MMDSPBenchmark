package dev.datageneration.simulation.Sensors;

import dev.datageneration.aggregate.DataPoint;
import dev.datageneration.simulation.types.*;
import lombok.Getter;

import java.util.*;

public abstract class Sensor {

    //Info of all possible data types, with their possible configurations.
    public static Map<String, DataType> dataTypes = new HashMap<>(){{
        put( "temperature celsius", new IntType( 80, 110 ) );
        put( "pressure psi", new IntType( 25, 30 ) );
        put( "kmp/h", new IntType( 0, 380 ) );
        put("mp/h", new DoubleType(0, 236.121));
        put("direction", new DoubleType(0, 359));
        put("g", new DoubleType(0, 10));
        put("brake_pressure", new IntType(0, 10));
        put("ml/min", new IntType(500, 4000));
        put("on/off", new IntType(0, 1));
        put("drs-zone", new IntType(0, 3));
        //TODO: Add more data types
    }};

    public static Map<String, Integer> frequency = new HashMap<>(){{
        put( "tyre", 10); //frequency --> 10 entries per second
        put( "heat", 10);
        put( "speed", 10);
        put( "g_force", 10);
        put( "fuel_pump", 10);
        put( "DRS", 10);
        put( "brake", 10);
        put( "long", 10);
    }};

    @Getter
    final String type;
    @Getter
    final int id;
    @Getter
    final List<String[]> dataPoints;
    @Getter
    final int frequencyValue;

    public Sensor(String type, int id, List<String[]> dataPoints) {
        this.id = id;
        this.type = type;
        this.dataPoints = dataPoints;
        this.frequencyValue = frequency.get(type);
    }

    /**
     * Creates a single datapoint of this specific sensor using the provided random generator.
     *
     */
    public abstract DataPoint createDataPoint();

//    public static Sensor parse(List<String> strings) {
//        String type = strings.get(0);
//        String id = strings.get(1);
//        List<String> arguments = strings.subList(2, strings.size());
//        //TODO: Solve Problem here
//        //return new S(type, id, arguments) {
//        //};
//        return null;
//    }

    public abstract String[] getHeader();


    public abstract void generateDataPoint();

    public void printDataPoints() {
        for (String[] dp : dataPoints) {
            System.out.println(Arrays.toString(dp));
        }
    }

}
