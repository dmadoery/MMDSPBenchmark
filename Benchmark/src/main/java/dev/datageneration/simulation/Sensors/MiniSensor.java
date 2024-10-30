package dev.datageneration.simulation.Sensors;

import dev.datageneration.simulation.types.DataType;
import org.json.JSONObject;

public class MiniSensor extends Sensor {

    String di1;
    DataType dType1;

    public MiniSensor(String type, int id, String di1) {
        super(type, id);
        this.di1 = di1;
        this.dType1 = dataTypes.get(di1);
    }

    @Override
    public String[] getHeader() {
        return new String[] {di1};
    }

    @Override
    public void generateDataPoint() {
        String data = dType1.sample();

        //create JSON object
        String header = di1;

        int f = getFrequencyValue();
        if(counter == f) {
            freq ++;
            counter = 0;
        }
        JSONObject sensorDataObject = new JSONObject();
        sensorDataObject.put(header, Double.valueOf(data));

        // Wrap each JSON object with a number prefix
        JSONObject freqObject = new JSONObject();
        freqObject.put(getType(), sensorDataObject);
        freqObject.put("freq", freq);
        counter ++;

        // Add the prefixed object to the JSON array
        dataPoints.put(freqObject);
    }
}