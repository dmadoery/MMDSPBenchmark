package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonTire {
    String type;
    public double temp;
    public double pressure;
    public int wear;
    public int id;
    public int position;
    public boolean error;
    public int tick;

    public JsonTire(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                temp = data.getDouble("temperature tire");
                pressure = data.getDouble("pressure psi");
                position = data.getInt("position");
                wear = data.getInt("wear");
                error = false;
            } else {
                temp = 0;
                pressure = 0;
                position = 0;
                wear = 0;
                error = true;
            }
        }
    }
}
