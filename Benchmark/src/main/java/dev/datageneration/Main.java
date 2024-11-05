package dev.datageneration;

import dev.datageneration.receiver.DataReceiver;
import dev.datageneration.sending.DataSender;
import dev.datageneration.simulation.DataGenerator;
import dev.datageneration.simulation.SensorGenerator;
import dev.datageneration.sending.ThreadedSender;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
//        boolean test  = true;
        int[] sensorArray = new int[] {20, 20, 20}; //TODO: change amount of Sensors and their data entries
//        SensorGenerator.creator(sensorArray);
//        DataGenerator.dataGenerator();
//        DataSender send = new DataSender(test);
//        send.processData(test);

        //Sending with SingleThread
        long startTime = System.currentTimeMillis(); //start of sending here, start time for data receiver?
        ThreadedSender.sendThreaded();
        DataReceiver.receive(startTime, sensorArray.length);
    }
}
