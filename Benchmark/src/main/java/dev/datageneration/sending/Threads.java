package dev.datageneration.sending;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

public class Threads implements Runnable {
    //Topic
    String topic;
    //Blockingqueue
    BlockingQueue<JSONObject> queue;
    //Kafka
    //TODO: implement

    public Threads(BlockingQueue<JSONObject> messageQueue, String topic /*, Kafka kafka*/) {
    this.queue = messageQueue;
    this.topic = topic;
    //this.kafka = kafka;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Take a message from the queue (blocking call)
                JSONObject message = queue.take();
                System.out.println("Message sent to topic " + topic + ": " + message.toString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
