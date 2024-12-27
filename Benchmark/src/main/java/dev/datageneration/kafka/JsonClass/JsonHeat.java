package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonHeat {
    String type;
    public double temp;
    public int id;
    public boolean error;
    public int tick;

    public JsonHeat(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                temp = data.getDouble("temperature c");
                error = false;
            } else {
                temp = 0;
                error = true;
            }
        }
    }
}
