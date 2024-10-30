package dev.datageneration.simulation.Sensors;

import dev.datageneration.aggregate.DataPoint;
import dev.datageneration.simulation.types.*;
import lombok.Getter;
import org.json.JSONArray;

import java.util.*;

@Getter
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
        put("test", new LongType(0, 10));

        //TODO: Add more data types
    }};

    public static Map<String, Integer> frequency = new HashMap<>(){{
        put( "tyre", 5); //frequency --> 10 entries per second
        put( "heat", 1);
        put( "speed", 2);
        put( "g_force", 3);
        put( "fuel_pump", 1);
        put( "DRS", 1);
        put( "brake", 4);
        put( "long", 10);
    }};

    final String type;
    final int id;
    final JSONArray dataPoints = new JSONArray();
    final int frequencyValue;
    public int counter = 0;
    public int freq = 1;

    public Sensor(String type, int id) {
        this.id = id;
        this.type = type;
        this.frequencyValue = frequency.get(type);
    }

    /**
     * Gets the information of the different data entries.
     **/
    public abstract String[] getHeader();

    // Creates Json object.
    public abstract void generateDataPoint();

//    public void printDataPoints() {
//        for (String[] dp : dataPoints) {
//            System.out.println(Arrays.toString(dp));
//        }
//    }

}
