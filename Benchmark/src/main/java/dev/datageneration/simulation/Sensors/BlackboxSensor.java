package dev.datageneration.simulation.Sensors;

import dev.datageneration.simulation.types.DataType;
import org.json.JSONObject;

import java.util.List;


public class BlackboxSensor extends Sensor {

    String di1;
    DataType dType1;

    public BlackboxSensor(String type, int id, String[] dataInfo) {
        super(type, id);
        this.di1 = dataInfo[0];
        this.dType1 = dataTypes.get(di1);
    }

    @Override
    public String[] getHeader() {
        return new String[] {di1};
    }

    @Override
    public void generateDataPoint() {
        // should only add an entry every 5th freq
        List<String> data = dType1.getData();
        //create JSON object
        JSONObject sensorDataObject = new JSONObject();
        sensorDataObject.put("entries", data); // Add each sensor data point to JSON object
        sensorDataObject.put("id", id);
        sensorDataObject.put("type", getType());

        // Wrap each JSON object with a number prefix
        JSONObject freqObject = new JSONObject();
        freqObject.put("data", sensorDataObject);
        freqObject.put("tick", tick + 4);

        // Add the prefixed object to the JSON array
        dataPoints.put(freqObject);
        tick += 5;
    }
}
