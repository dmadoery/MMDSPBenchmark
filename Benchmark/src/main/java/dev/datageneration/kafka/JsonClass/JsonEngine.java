package dev.datageneration.kafka.JsonClass;

import org.json.JSONObject;

public class JsonEngine {
    String type;
    public int temp;
    public long rpm;
    public int id;
    public int fuelFlow;
    public boolean error;
    public int tick;
    public double oilPressure;
    public double fuelPressure;
    public double exhaust;

    public JsonEngine(String json) {
        JSONObject entry = new JSONObject(json);
        tick = entry.getInt("tick");
        if(entry.has("data")) {
            JSONObject data = entry.getJSONObject("data");
            id = data.getInt("id");
            type = data.getString("type");
            if(!data.has("Error")) {
                temp = data.getInt("temperature engine");
                rpm = data.getLong("rpm");
                fuelFlow = data.getInt("fuelFlow");
                oilPressure = data.getDouble("oil_pressure");
                fuelPressure = data.getDouble("fuel_pressure");
                exhaust = data.getDouble("exhaust");
                error = false;
            } else {
                temp = 0;
                rpm = 0;
                fuelFlow = 0;
                oilPressure = 0;
                fuelPressure = 0;
                exhaust = 0;
                error = true;
            }
        }
    }
}
