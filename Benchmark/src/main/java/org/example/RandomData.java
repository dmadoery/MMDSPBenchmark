package org.example;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;

public class RandomData {
    public static ArrayList<String[]> sensors = new ArrayList<>();

    public static void setSensors(){
        RandomData.sensors.clear();
        //RandomData.sensors.add(new String[] {"Name", "typeOfSensor", "Data1", "dataRange_min1", "dataRange_max1", "Data2", "dataRange_min2", "dataRange_max2"});
        RandomData.sensors.add(new String[] {"front_left_tyre", "tyre", "temperature celsius", "80", "110", "pressure psi", "25", "30"});
        RandomData.sensors.add(new String[] {"front_right_tyre", "tyre", "temperature celsius", "80", "110", "pressure psi", "25", "30"});
        RandomData.sensors.add(new String[] {"rear_left_tyre", "tyre", "temperature celsius", "80", "110", "pressure psi", "20", "25"});
        RandomData.sensors.add(new String[] {"rear_right_tyre", "tyre", "temperature celsius", "80", "110", "pressure psi", "20", "25"});
        RandomData.sensors.add(new String[] {"speed_sensor", "speed", "kmp/h", "0", "380", "mp/h", "0", "236.121"});
        RandomData.sensors.add(new String[] {"g_sensor", "g_force", "direction", "0", "359", "g", "0", "10"});
        RandomData.sensors.add(new String[] {"fuel_pump_sensor", "fuel_pump", "temperature celsius", "-40", "85", "ml/min", "500", "4000"});
        RandomData.sensors.add(new String[] {"drs_sensor", "DRS", "on/off", "0", "1", "drs-zone", "0", "3"});
        RandomData.sensors.add(new String[] {"front_left_brake", "brake", "temperature celsius", "0", "1000", "brake_pressure", "0", "10"});
        RandomData.sensors.add(new String[] {"front_right_brake", "brake", "temperature celsius", "0", "1000", "brake_pressure", "0", "10"});
        RandomData.sensors.add(new String[] {"rear_left_brake", "brake", "temperature celsius", "0", "1000", "brake_pressure", "0", "10"});
        RandomData.sensors.add(new String[] {"rear_right_brake", "brake", "temperature celsius", "0", "1000", "brake_pressure", "0", "10"});
    }

    public static double getRandom(double min, double max) {
        double rand = (Math.random()*(max-min))+min;
        return rand;
    }

    public static String getTime() {
        Clock clock = Clock.systemDefaultZone();
        long millisecond = clock.millis();
        return (new SimpleDateFormat("hh:mm:ss:SSS")).format(new Date(millisecond));
    }

    public static String getDate() {
        return (new SimpleDateFormat("dd:MM:yyyy").format(new Date()));
    }

    public static ArrayList<String[]> create_Sensors(int j) {
        if (j == RandomData.sensors.size()) {
            return RandomData.sensors;
        } else if (j < RandomData.sensors.size()) {
            ArrayList<String[]> returner = new ArrayList<>();
            while (j != 0) {
                int i = (int) getRandom(0, (double) RandomData.sensors.size());
                returner.add(RandomData.sensors.get(i));
                RandomData.sensors.remove(i);
                j --;
            }
            return returner;
        }
        else {
            ArrayList<String[]> returner1 = new ArrayList<>();
            while (j != 0) {
                int i = (int) getRandom(0, (double) RandomData.sensors.size());
                returner1.add(RandomData.sensors.get(i));
                j --;
            }
            return returner1;
        }
    }
}
