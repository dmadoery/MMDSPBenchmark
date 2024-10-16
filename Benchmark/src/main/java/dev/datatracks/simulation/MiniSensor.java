package dev.datatracks.simulation;

import dev.datatracks.RandomData;
import dev.datatracks.aggregate.DataPoint;
import dev.datatracks.simulation.types.DataType;
import java.util.List;
import lombok.Getter;
import org.apache.kafka.common.protocol.types.Field.Str;

public class MiniSensor extends Sensor {

    String name1;
    DataType dataType1;

    String name2;
    DataType dataType2;


    public MiniSensor(
            String name,
            List<SensorCategory> sensorCategories,
            String name1,
            DataType dataType1,
            String name2,
            DataType dataType2  ) {
        super( name, sensorCategories );
        this.name1 = name1;
        this.dataType1 = dataType1;
        this.name2 = name2;
        this.dataType2 = dataType2;
    }


    @Override
    public DataPoint createDataPoint() {
        return new DataPoint( List.of(dataType1.sample(), dataType2.sample()) );
    }


    @Override
    public String[] getHeader() {
        return new String[] {name1, name2};
    }


}
