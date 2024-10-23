package dev.datageneration.simulation.Sensors;

import dev.datageneration.aggregate.DataPoint;
import dev.datageneration.simulation.types.DataType;

import java.util.List;

public class MiniSensor extends Sensor {

    String di1;
    DataType dType1;

    public MiniSensor(String type, int id, List<String[]> dataPoints, String di1, DataType dType1) {
        super(type, id, dataPoints);
        this.di1 = di1;
        this.dType1 = dType1;
    }

    @Override
    public DataPoint createDataPoint() {
       return new DataPoint( List.of(dType1.sample()));
    }

    @Override
    public String[] getHeader() {
        return new String[] {di1};
    }

    @Override
    public void generateDataPoint() {
        dataPoints.add(new String[] {dType1.sample()});
    }
}