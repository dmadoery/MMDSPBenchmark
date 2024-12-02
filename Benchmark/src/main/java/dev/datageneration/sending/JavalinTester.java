package dev.datageneration.sending;

import io.javalin.Javalin;
import lombok.Getter;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class JavalinTester {
    static Javalin server = null;
    @Getter
    private static final ConcurrentLinkedQueue<JSONObject> messages = new ConcurrentLinkedQueue<>();
    @Getter
    private static final ConcurrentLinkedQueue<JSONObject> receivedMessages = new ConcurrentLinkedQueue<>();
    @Getter
    static int throughput = 0;
    @Getter
    static long lastReceivedTime;
    @Getter
    static long startTime;
    @Getter
    static List<JSONObject> dataReceived = new LinkedList<>();
    @Getter
    static int amountSensors;

    public static void starting() {
        if (server == null) {
            server = Javalin.create().start(7777);
            server.post("/", ctx -> {
                try {
                    JSONObject message = new JSONObject(ctx.body());
                    System.out.println("Received: " + message);
                    receivedMessages.add(message);
                    receiving(); // Ensure this does not throw errors
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    public static void sending(JSONObject message) {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://localhost:7777/");
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(message.toString()));
            client.execute(post, new BasicHttpClientResponseHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void receiving() {
        JSONObject obj = JavalinTester.getReceivedMessages().poll();
        if(obj == null) {
        }
        if (obj.has("end") && obj.getBoolean("end")) {
            lastReceivedTime = System.currentTimeMillis();
        } else if (obj.has("start")) {
            startTime = System.currentTimeMillis();
            amountSensors = obj.getInt("start");
        } else if (obj.has("null") && obj.getBoolean("null")) {
        }
        dataReceived.add(obj);
        throughput++;
    }
}