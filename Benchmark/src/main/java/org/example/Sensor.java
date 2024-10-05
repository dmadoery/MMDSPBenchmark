package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Sensor {
    private static String[] header;
    private static String type;
    private static String id;
    private static String date;
    private static String time;
    private static String data1Info;
    private static String data2Info;
    public static ArrayList<String[]> sensorData;

    public Sensor (String t, String i, String d, String ti, String dif1, String dif2){
        header = new String[]{t, i, d, ti, dif1, dif2}; // format: [type, id, date, time, data1, data2]
        type = t;
        id = i;
        date = d;
        time = ti;
        data1Info = dif1;
        data2Info = dif2;
        sensorData = new ArrayList<String[]>();
    }

    public static void print(int index) {
        System.out.println(Arrays.toString(sensorData.get(index)));
    }

//    public static String[] getDataAsStringArray(){
//        return new String[]{type, id, date, timeGenerated, String.valueOf(data1), String.valueOf(data2)};
//    }
}