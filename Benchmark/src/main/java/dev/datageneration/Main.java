package dev.datageneration;

import dev.datageneration.aggregate.AggregatedData;
import dev.datageneration.aggregate.ErrorCreator;
import dev.datageneration.aggregate.FinalData;
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
        boolean aggregated  = true;
        int threadAmount = 30;
        int[] sensorArray = new int[] {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
                1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000}; //TODO: change amount of Sensors and their data entries
        SensorGenerator.creator(sensorArray);
        ErrorCreator.dataWithErrors(); //create some data loss and null entries.
        DataGenerator.dataGenerator();
        WindowedData.createWindowedData(); //creates warnings if some data is not in a wished range
        AggregatedData.aggregatedData(); //get average over a time interval
        FinalData.createFinalData();

        Thread sendThread = new Thread(() -> {
            try {
                ThreadedSender.sendThreaded(sensorArray.length, aggregated, threadAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread receiveThread = new Thread(() -> {
            try {
                DataReceiver.receive(aggregated, threadAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
        TimeUnit.SECONDS.sleep(1);
        sendThread.start();


        sendThread.join();
        receiveThread.join();
        System.out.println("Finished everything");
    }
}
