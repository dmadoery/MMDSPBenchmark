package dev.datageneration.sending;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;


public class SingleThread implements Runnable {
    //Topic
    String topic;
    //Blockingqueue
    BlockingQueue<JSONObject> queue;
    //Kafka
    KafkaProducer<String, String> producer;

    public SingleThread(BlockingQueue<JSONObject> messageQueue, String topic , KafkaProducer<String, String> producer) {
    this.queue = messageQueue;
    this.topic = topic;
    this.producer = producer;
    }

    @Override
    public void run() {
        while (!ThreadedSender.stop) {
            try { //TODO: solve problem, if multiple threads sometimes duplicate entries from here!!!!
                synchronized (queue) {
                    JSONObject message = queue.peek();
                    if (message == null) {
                        continue;
                    }
                    if (message.getInt("tick") == ThreadedSender.getTimeStep()) {
                        if (queue.peek().equals(message)) {
                            queue.take();
                        } else {
                            continue;
                        }
                        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message.toString());
                        producer.send(record);
//                        System.out.println("Sent: " + record.toString());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName());
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
