package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        boolean test  = true;
        int[] sensorArray = new int[] {100, 1000, 10, 105, 20, 55}; //TODO: change amount of Sensors and their data entries
        SensorGenerator.creator(sensorArray);
        DataGenerator.dataGenerator();
        DataSender send = new DataSender(test);
        send.processData(test);
    }
}
