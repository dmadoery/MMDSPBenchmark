package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonFuelPump {
    String type;
    public double temp;
    public double flowRate;
    public int id;
    public boolean error;
    public int tick;

    public JsonFuelPump(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                temp = data.getDouble("temperature fuelP");
                flowRate = data.getDouble("ml/min");
                error = false;
            } else {
                temp = 0;
                flowRate = 0;
                error = true;
            }
        }
    }
}
