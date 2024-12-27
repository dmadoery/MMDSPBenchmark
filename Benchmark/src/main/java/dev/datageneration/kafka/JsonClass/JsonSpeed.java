package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonSpeed {
    String type;
    public double speed;
    public double wind;
    public int id;
    public boolean error;
    public int tick;

    public JsonSpeed(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                speed = data.getDouble("kph");
                wind = data.getDouble("wind speed");
                error = false;
            } else {
                speed = 0;
                wind = 0;
                error = true;
            }
        }
    }
}
