package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Sensor {
    public ArrayList<String[]> sensorData;

    public Sensor(String[] data) {
        String type = data[0];
        String id = data[1];
        String date = data[2];
        String time = data[3];
        String data1Info = data[4];
        String data2Info = data[5];
        sensorData = new ArrayList<String[]>();
        sensorData.add(data);
    }

    public void print(int index) {
        System.out.println(Arrays.toString(sensorData.get(index)));
    }
}
