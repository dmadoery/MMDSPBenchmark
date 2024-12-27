package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonBrake {
    String type;
    public int temp;
    public int pressure;
    public int wear;
    public int id;
    public boolean error;
    public int tick;

    public JsonBrake(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                temp = data.getInt("temperature brake");
                pressure = data.getInt("brake_pressure");
                wear = data.getInt("wear");
                error = false;
            } else {
                temp = 0;
                pressure = 0;
                wear = 0;
                error = true;
            }
        }
    }
}
