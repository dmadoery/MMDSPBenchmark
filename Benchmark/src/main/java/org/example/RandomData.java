package org.example;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;

public class RandomData {

    public static double getRandom(double min, double max) {
        double rand = (Math.random()*(max-min))+min;
        return rand;
    }

    public static String getTime() {
        Clock clock = Clock.systemDefaultZone();
        long millisecond = clock.millis();
        return (new SimpleDateFormat("hh:mm:ss:SSS")).format(new Date(millisecond));
    }

}
