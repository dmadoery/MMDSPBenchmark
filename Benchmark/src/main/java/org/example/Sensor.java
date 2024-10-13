package org.example;

import java.util.ArrayList;
import java.util.List;


public class Sensor {
    String type;
    String id;
    String data1Info;
    String data2Info;
    String data3Info;
    String data4Info;
    String data5Info;
    String data6Info;
    List<String[]> dataPoints = new ArrayList<>();

    public Sensor(String type, String id, String dataInfo) {
        this.type = type;
        this.id = id;
        this.data1Info = dataInfo;
    }

    public Sensor(String type, String id, String data1Info, String data2Info) {
        this.type = type;
        this.id = id;
        this.data1Info = data1Info;
        this.data2Info = data2Info;
    }

    public Sensor(String type, String id, String data1Info, String data2Info, String data3Info) {
        this.type = type;
        this.id = id;
        this.data1Info = data1Info;
        this.data2Info = data2Info;
        this.data3Info = data3Info;
    }

    public Sensor(String type, String id, String data1Info, String data2Info, String data3Info, String data4Info) {
        this.type = type;
        this.id = id;
        this.data1Info = data1Info;
        this.data2Info = data2Info;
        this.data3Info = data3Info;
        this.data4Info = data4Info;
    }

    public Sensor(String type, String id, String data1Info, String data2Info, String data3Info, String data4Info, String data5Info) {
        this.type = type;
        this.id = id;
        this.data1Info = data1Info;
        this.data2Info = data2Info;
        this.data3Info = data3Info;
        this.data4Info = data4Info;
        this.data5Info = data5Info;
    }

    public Sensor(String type, String id, String data1Info, String data2Info, String data3Info, String data4Info, String data5Info, String data6Info) {
        this.type = type;
        this.id = id;
        this.data1Info = data1Info;
        this.data2Info = data2Info;
        this.data3Info = data3Info;
        this.data4Info = data4Info;
        this.data5Info = data5Info;
        this.data6Info = data6Info;
    }

    public static Sensor parse(List<String> strings) {
        int l = strings.size();
        if (l == 3) {
            String type = strings.get(0);
            String id = strings.get(1);
            String data1Info = strings.get(2);
            return new Sensor(type, id, data1Info);
        } else if (l == 4) {
            String type = strings.get(0);
            String id = strings.get(1);
            String data1Info = strings.get(2);
            String data2Info = strings.get(3);
            return new Sensor(type, id, data1Info, data2Info);
        } else if (l == 5) {
            String type = strings.get(0);
            String id = strings.get(1);
            String data1Info = strings.get(2);
            String data2Info = strings.get(3);
            String data3Info = strings.get(4);
            return new Sensor(type, id, data1Info, data2Info, data3Info);
        } else if (l == 6) {
            String type = strings.get(0);
            String id = strings.get(1);
            String data1Info = strings.get(2);
            String data2Info = strings.get(3);
            String data3Info = strings.get(4);
            String data4Info = strings.get(5);
            return new Sensor(type, id, data1Info, data2Info, data3Info, data4Info);
        } else if (l == 7) {
            String type = strings.get(0);
            String id = strings.get(1);
            String data1Info = strings.get(2);
            String data2Info = strings.get(3);
            String data3Info = strings.get(4);
            String data4Info = strings.get(5);
            String data5Info = strings.get(6);
            return new Sensor(type, id, data1Info, data2Info, data3Info, data4Info, data5Info);
        } else {
            String type = strings.get(0);
            String id = strings.get(1);
            String data1Info = strings.get(2);
            String data2Info = strings.get(3);
            String data3Info = strings.get(4);
            String data4Info = strings.get(5);
            String data5Info = strings.get(6);
            String data6Info = strings.get(7);
            return new Sensor(type, id, data1Info, data2Info, data3Info, data4Info, data5Info, data6Info);
        }
    }

    public void add(String[] dataPoint) {
        this.dataPoints.add(dataPoint);
    }
}
