package dev.datageneration.simulation.Sensors;

import dev.datageneration.simulation.types.DataType;
import org.json.JSONObject;


public class SmallSensor extends Sensor {

    String di1;
    String di2;
    DataType dType1;
    DataType dType2;


    public SmallSensor(String type, int id, String[] dataInfo) {
        super(type, id);
        this.di1 = dataInfo[0];
        this.di2 = dataInfo[1];
        this.dType1 = dataTypes.get(di1);
        this.dType2 = dataTypes.get(di2);
    }

    @Override
    public String[] getHeader() {
        return new String[] {di1, di2};
    }

    @Override
    public void generateDataPoint() {
        String[] data = new String[] {dType1.sample(), dType2.sample()};

        //create JSON object
        String[] header = getHeader();
        int f = getFrequencyValue();
        if(counter == f) {
            freq ++;
            counter = 0;
        }
        JSONObject sensorDataObject = new JSONObject();
        for (int j = 0; j < header.length; j++) {
            sensorDataObject.put(header[j], Double.valueOf(data[j])); // Add each sensor data point to JSON object
        }
        // Wrap each JSON object with a number prefix
        JSONObject freqObject = new JSONObject();
        freqObject.put(getType(), sensorDataObject);
        freqObject.put("freq", freq);
        counter ++;

        // Add the prefixed object to the JSON array
        dataPoints.put(freqObject);
    }
}
