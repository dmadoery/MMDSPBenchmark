package dev.datageneration.sending;

import io.javalin.Javalin;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class JavalinTester {
    static Javalin server = null;
    @Getter
    private static final LinkedBlockingQueue<JSONObject> messages = new LinkedBlockingQueue<>();

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
        return new JSONObject(Objects.requireNonNull(messages.poll()));
    }

//    public static void main(String[] args) {
//        Javalin app = Javalin.create().start(7777);
//        app.get("/", ctx -> ctx.result("Hello, Javalin!"));
//    }
}