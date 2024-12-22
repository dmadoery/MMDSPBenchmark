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
        String data = dType1.sample(di1);

        //create JSON object
        String header = di1;

        int f = getTickValue();
        if(counter == f) {
            tick ++;
            counter = 0;
        }
        JSONObject sensorDataObject = new JSONObject();
        sensorDataObject.put(header, Double.valueOf(data));
        sensorDataObject.put("id", id);
        sensorDataObject.put("type", getType());

        // Wrap each JSON object with a number prefix
        JSONObject freqObject = new JSONObject();
        freqObject.put("data", sensorDataObject);
        freqObject.put("tick", tick);
        counter ++;

        // Add the prefixed object to the JSON array
        dataPoints.put(freqObject);
    }
}