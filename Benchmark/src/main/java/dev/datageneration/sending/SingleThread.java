package dev.datageneration.sending;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;


public class SingleThread implements Runnable {
    //Topic
    String topic;
    //Blockingqueue
    BlockingQueue<JSONObject> queue;
    //Kafka
    //TODO: implement

    public SingleThread(BlockingQueue<JSONObject> messageQueue, String topic /*, Kafka kafka*/) {
    this.queue = messageQueue;
    this.topic = topic;
    //this.kafka = kafka;
    }

    @Override
    public void run() {
        while (true) {
            try {
                JSONObject message = queue.take();
                if (message.getInt("freq") == ThreadedSender.getFrequency()) {
                    JavalinTester.sending(message);
                    System.out.println("Message sent to topic " + topic + ": " + message.toString());
                } else {
                    queue.put(message);
                }
                //TODO: stopping the threads not working!! why?
                if(ThreadedSender.stop) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
                break;
            }
        }
    }
}
