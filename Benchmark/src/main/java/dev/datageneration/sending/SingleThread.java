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
        while (!ThreadedSender.stop) {
            try {
//                if(ThreadedSender.stop) {
//                    Thread.currentThread().interrupt();
//                    return;
//                }
                JSONObject message = queue.peek();
                if(message == null) {
                    continue;
                }
                if (message.getInt("tick") == ThreadedSender.getTimeStep()) {
                    queue.take();
                    JavalinTester.sending(message);
                    //System.out.println("Message sent to topic " + topic + ": " + message.toString());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
                break;
            }
        }
    }

    public boolean alive() {
        return Thread.currentThread().isAlive();
    }

    public int length() {
        return queue.size();
    }

}
