package dev.datageneration.simulation.Sensors;

import dev.datageneration.aggregate.DataPoint;
import dev.datageneration.simulation.types.DataType;

import java.util.List;

public class MediumSensor extends Sensor {

    String di1;
    String di2;
    String di3;
    DataType dType1;
    DataType dType2;
    DataType dType3;


    public MediumSensor(String type, int id, List<String[]> dataPoints, String di1, String di2, String di3,
                        DataType dType1, DataType dType2, DataType dType3) {
        super(type, id, dataPoints);
        this.di1 = di1;
        this.di2 = di2;
        this.di3 = di3;
        this.dType1 = dType1;
        this.dType2 = dType2;
        this.dType3 = dType3;
    }

    @Override
    public DataPoint createDataPoint() {
        return new DataPoint( List.of(dType1.sample(), dType2.sample(), dType3.sample()) );
    }

    @Override
    public String[] getHeader() {
        return new String[] {di1, di2, di3};
    }

    @Override
    public void generateDataPoint() {
        dataPoints.add(new String[] {dType1.sample(), dType2.sample(), dType3.sample()});
    }
}
