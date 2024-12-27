package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonAccelerometer {
    String type;
    public double throttle;
    public int id;
    public boolean error;
    public int tick;

    public JsonAccelerometer(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                throttle = data.getDouble("throttlepedall");
                error = false;
            } else {
                throttle = 0;
                error = true;
            }
        }
    }
}
