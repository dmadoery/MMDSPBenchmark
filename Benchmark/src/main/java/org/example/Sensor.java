package org.example;
public class Sensor {
    private String id;
    private String date;
    private String timeStamp;
    private double temp;
    private double preasure;

    public Sensor (String i, String d, String time, double t, double p){
        id = i;
        date = d;
        timeStamp = time;
        temp = t;
        preasure = p;
    }

    public void print() {
        System.out.println("ID: " + id + "Date: " + date + "Time: " + timeStamp + "Temperature: " + temp + "Preassure: "+ preasure);
    }
}