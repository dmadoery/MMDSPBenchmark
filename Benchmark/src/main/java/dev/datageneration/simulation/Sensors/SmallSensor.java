package dev.datageneration.simulation.Sensors;

import dev.datageneration.aggregate.DataPoint;
import dev.datageneration.simulation.types.DataType;

import java.util.List;

public class SmallSensor extends Sensor {

    String di1;
    String di2;
    DataType dType1;
    DataType dType2;


    public SmallSensor(String type, int id, List<String[]> dataPoints, String di1, String dI2, DataType dType1, DataType dType2) {
        super(type, id, dataPoints);
        this.di1 = di1;
        this.di2 = dI2;
        this.dType1 = dType1;
        this.dType2 = dType2;
    }

    @Override
    public DataPoint createDataPoint() {
        return new DataPoint( List.of(dType1.sample(), dType2.sample()) );
    }

    @Override
    public String[] getHeader() {
        return new String[] {di1, di2};
    }

    @Override
    public void generateDataPoint() {
        dataPoints.add(new String[] {dType1.sample(), dType2.sample()});
    }
}
