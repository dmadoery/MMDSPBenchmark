package dev.datageneration.aggregate;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;


    /**
     * Creates a Sensor with the right amount of data information slots.
     */
    @Getter
    @AllArgsConstructor
    public class DataPoint {

        List<String> arguments;

        public static DataPoint parse(List<String> arguments) {
            return new DataPoint(arguments);
        }

        public String[] asString(){
            return arguments.toArray( new String[0] );
        }
}
