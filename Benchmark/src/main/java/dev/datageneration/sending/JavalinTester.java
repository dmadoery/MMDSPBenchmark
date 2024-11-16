package dev.datageneration.sending;

import io.javalin.Javalin;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JavalinTester {
    static Javalin server = null;
    @Getter
    private static final ConcurrentLinkedQueue<JSONObject> messages = new ConcurrentLinkedQueue<>();

    public static void starting() {
        if (server == null) {
            server = Javalin.create().start(7777);
            server.get("/", ctx -> {
                JSONArray jsonArray = new JSONArray(messages);
                ctx.json(jsonArray.toString());
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
        messages.add(message);
    }

    public static JSONObject receiving() {
        JSONObject message = messages.poll();
        if (Objects.isNull(message)) {
            JSONObject error = new JSONObject();
            error.put("null", true);
            return error;
        }
        return message;
    }
}