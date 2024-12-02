package dev.datageneration;

import dev.datageneration.aggregate.AggregatedData;
import dev.datageneration.aggregate.ErrorCreator;
import dev.datageneration.aggregate.WindowedData;
import dev.datageneration.jsonHandler.JsonFileHandler;
import dev.datageneration.receiver.DataReceiver;
import dev.datageneration.sending.DataSender;
import dev.datageneration.sending.JavalinTester;
import dev.datageneration.simulation.DataGenerator;
import dev.datageneration.simulation.SensorGenerator;
import dev.datageneration.sending.ThreadedSender;
//import org.eclipse.jetty.io.MappedByteBufferPool;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        JsonFileHandler.deleteAllJsonFiles();
        //boolean test  = true;
        boolean aggregated = false;
        boolean windowed = false;
        int[] sensorArray = new int[] {200, 100, 50, 60}; //TODO: change amount of Sensors and their data entries
        SensorGenerator.creator(sensorArray);
        ErrorCreator.dataWithErrors(); //create some data loss and null entries.
        DataGenerator.dataGenerator();
        AggregatedData.aggregatedData(); //get average over a time interval
        WindowedData.createWindowedData(); //creates warnings if some data is not in a wished range

        Thread sendThread = new Thread(() -> {
            try {
                ThreadedSender.sendThreaded(sensorArray.length, aggregated, windowed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread receiveThread = new Thread(() -> {
            try {
                DataReceiver.receive(aggregated, windowed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
        TimeUnit.SECONDS.sleep(2);
        sendThread.start();


        sendThread.join();
        receiveThread.join();
        System.out.println("Finished everything");
//        System.out.println("Sender Thread alive: "+ sendThread.isAlive());
//        System.out.println("Receiver Thread alive: "+ receiveThread.isAlive());
//        for (int i = 0; i < 4; i++) {
//            System.out.println("Other Threads alive: "+ ThreadedSender.threads.get(i).alive());
//            System.out.println("Length of Thread: " + ThreadedSender.threads.get(i).length());
//        }
    }
}
