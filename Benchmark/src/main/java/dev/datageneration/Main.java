package dev.datageneration;

import dev.datageneration.simulation.SensorGenerator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        boolean test  = true;
        int[] sensorArray = new int[] {20, 20, 20}; //TODO: change amount of Sensors and their data entries
        SensorGenerator.creator(sensorArray);
//        DataGenerator.dataGenerator();
//        DataSender send = new DataSender(test);
//        send.processData(test);
    }
}
