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

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        JsonFileHandler.deleteAllJsonFiles();
        boolean test  = true;
        boolean aggregated = false;
        boolean windowed = true;
        int[] sensorArray = new int[] {100, 150, 400}; //TODO: change amount of Sensors and their data entries
        SensorGenerator.creator(sensorArray);
        ErrorCreator.dataWithErrors();
        DataGenerator.dataGenerator();
        AggregatedData.aggregatedData();
        WindowedData.createWindowedData();

//        TimeUnit.SECONDS.sleep(10);
//        DataSender send = new DataSender(test);
//        send.processData(test);

        Thread sendThread = new Thread(() -> {
            try {
                ThreadedSender.sendThreaded(sensorArray.length, aggregated, windowed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread receiveThread = new Thread(() -> {
            try {
                DataReceiver.receive(aggregated, windowed); //TODO: change booleans for different modes
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
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
