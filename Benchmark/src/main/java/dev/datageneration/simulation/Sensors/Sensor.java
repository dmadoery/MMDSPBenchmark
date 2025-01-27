package dev.datageneration.simulation.Sensors;

import dev.datageneration.simulation.types.*;
import lombok.Getter;
import org.json.JSONArray;

import java.util.*;

@Getter
public abstract class Sensor {

    //Info of all possible data types, with their possible configurations.
    public static Map<String, DataType> dataTypes = new HashMap<>(){{
        put( "temperature tire", new IntType( 80, 110 ) );
        put( "temperature brake", new IntType( 900, 1000 ) );
        put( "temperature c", new IntType( 1, 50 ) );
        put( "temperature engine", new IntType( 500, 600 ) );
        put( "temperature fuelP", new IntType( 20, 60 ) );
        put( "pressure psi", new DoubleType( 25, 30 ) );
        put( "kph", new DoubleType( 80, 360 ) );
        put( "mph", new DoubleType(60, 236.121));
        put( "direction", new IntType( 0, 4));
        put( "brake_pressure", new IntType(1, 10));
        put( "ml/min", new LongType(3000, 4000));
        put( "on/off", new IntType(0, 1));
        put( "drs-zone", new IntType(0, 3));
        put( "test", new LongType(1, 10));
        put( "wear", new IntType(1, 90));
        put( "liability", new IntType(1, 96));
        put( "acceleration", new DoubleType(1, 30));
        put( "wind speed", new DoubleType(1, 200));
        put( "g-lateral", new DoubleType(1, 6));
        put( "g-longitudinal", new DoubleType(1, 5));
        put( "throttlepedall", new IntType(1, 100));
        put( "rpm", new LongType(7000, 18000));
        put( "fuelFlow", new IntType(20, 120));//kg/h
        put( "oil_pressure", new DoubleType(1.5, 7));
        put( "fuel_pressure", new DoubleType(3, 5));
        put( "exhaust", new DoubleType(0.7, 1.2));//lambda ratio
        put( "turning_degree", new IntType(1, 180));
        put( "array_of_data", new StringArrayType());
        put( "position", new IntType(0, 4));

        //TODO: Add more data types
    }};

    public static Map<String, Integer> ticks = new HashMap<>(){{
        put( "tire", 1);                //tick --> 10 entries per timeunit
        put( "heat", 1);
        put( "speed", 1);
        put( "gForce", 1);
        put( "fuelPump", 1);
        put( "DRS", 1);
        put( "brake", 1);
        put( "steering", 1);
        put( "accelerometer", 1);
        put( "engine", 1);
        put( "blackbox", -5);           // every 5th tick an entry
    }};

    final String type;
    final int id;
    final JSONArray dataPoints = new JSONArray();
    final int tickValue;
    public  int counter = 0;
    public  int tick = 1;

    public Sensor(String type, int id) {
        this.id = id;
        this.type = type;
        this.tickValue = ticks.get(type);
    }

    /**
     * Gets the information of the different data entries.
     **/
    public abstract String[] getHeader();

    // Creates Json object.
    public abstract void generateDataPoint();

}
