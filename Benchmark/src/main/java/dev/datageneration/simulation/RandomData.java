package dev.datageneration.simulation;

import dev.datageneration.simulation.Sensors.MediumSensor;
import dev.datageneration.simulation.Sensors.MiniSensor;
import dev.datageneration.simulation.Sensors.Sensor;
import dev.datageneration.simulation.Sensors.SmallSensor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.*;

public class RandomData {
    public static List<String[]> sensors = new ArrayList<>();

    //Info of all possible data types, with their possible min and max values.
//    public static String[][] dataTypes = new String[][]{{"temperature celsius", "80", "110"}, {"pressure psi", "25", "30"},
//            {"kmp/h", "0", "380"}, {"mp/h", "0", "236.121"}, {"direction", "0", "359"}, {"g", "0", "10"},
//            {"brake_pressure", "0", "10"}, {"ml/min", "500", "4000"}, {"test", "0", "10"}, {"on/off", "0", "1"}, {"drs-zone", "0", "3"},
//            {"test", "0", "10"} //TODO: add more types here
//    };

    public static Random random = new Random();
    public static long seed = 795673489;


    public static void setSeed(long s) {
        random.setSeed(s);
    }

    /**
     * Creates Sensors form different lengths, all Sensors have: type, id, date, time
     * and can have additionally up to 6 data entries.
     */
    public static void setSensors(){
        RandomData.sensors.clear();
        //RandomData.sensors.add(new String[] {"typeOfSensor", "Data1", "Data2", ..., Data6});
        RandomData.sensors.add(new String[] {"heat", "temperature celsius"}); //heat sensor
        RandomData.sensors.add(new String[] {"heat", "temperature celsius"});//heat sensor,
        RandomData.sensors.add(new String[] {"tyre", "temperature celsius", "pressure psi"});//front_left_tyre
        RandomData.sensors.add(new String[] {"tyre", "temperature celsius", "pressure psi"});//front_right_tyre
        RandomData.sensors.add(new String[] {"tyre", "temperature celsius", "pressure psi"});//rear_left_tyre
        RandomData.sensors.add(new String[] {"tyre", "temperature celsius", "pressure psi"});//rear_right_tyre
//        RandomData.sensors.add(new String[] {"speed", "kmp/h","mp/h"});//speed_sensor
//        RandomData.sensors.add(new String[] {"g_force", "direction", "g"});//g_sensor
//        RandomData.sensors.add(new String[] {"fuel_pump", "temperature celsius", "ml/min"});//fuel_pump_sensor
//        RandomData.sensors.add(new String[] {"DRS", "on/off", "drs-zone"});//drs_sensor
//        RandomData.sensors.add(new String[] {"brake", "temperature celsius","brake_pressure"});//front_left_brake
//        RandomData.sensors.add(new String[] {"brake", "temperature celsius", "brake_pressure"});//front_right_brake
//        RandomData.sensors.add(new String[] {"brake", "temperature celsius", "brake_pressure"});//rear_left_brake
//        RandomData.sensors.add(new String[] {"brake", "temperature celsius", "brake_pressure"});//rear_right_brake
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "direction"});
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "direction"});


//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
//        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});

    }

    /**
     * Creates a random number(double) between the given min and max values.
     * @param min value
     * @param max value
     * @return random number between min and max
     */
    public static double getRandom(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    /**
     * Returns the current time in the format: HH:mm:ss.SSSS
     * @return time
     */
    public static String getTime() {
        Clock clock = Clock.systemDefaultZone();
        long millisecond = clock.millis();
        return (new SimpleDateFormat("HH:mm:ss.SSSS")).format(new Date(millisecond));
    }

    /**
     * Returns the current date in the format: dd:MM:yyyy
     * @return date
     */
    public static String getDate() {
        return (new SimpleDateFormat("dd:MM:yyyy").format(new Date()));
    }

    /**
     * The real creator of the sensors.
     * Creates sensors accordingly to the chosen amount.
     * @param amount of sensors
     * @return list of sensors
     */
    public static List<Sensor> create_Sensors(int[] amount) {
        List<Sensor> sensorList = new ArrayList<>();
        int id = 0;
        int length = amount.length;
        int count = 0;
        if (length == RandomData.sensors.size()) {
            for (int i = 0; i < length; i++) {
                String[] data = RandomData.sensors.get(i);
                String type = data[0];
                String[] dataInfos = new String[data.length - 1];
                if (RandomData.sensors.get(i).length - 1 >= 0)
                    System.arraycopy(data, 1, dataInfos, 0, data.length - 1);
                System.out.println(dataInfos.length);
                Sensor s = chooseSensor(dataInfos.length, type, id, dataInfos);
                for (int j = 0; j < amount[i]; j++) {
                    if (s != null) {
                        s.generateDataPoint();
                        System.out.println(count++);
                    }
                }
                sensorList.add(s);
                id ++;
            }
            return sensorList;

        } else {
            int k = 0;
            while (length != 0) {
                int i = (int) getRandom(0, RandomData.sensors.size());
                String[] data = RandomData.sensors.get(i);
                String type = data[0];
                String[] dataInfos = new String[data.length - 1];
                if (RandomData.sensors.get(i).length - 1 >= 0)
                    System.arraycopy(data, 1, dataInfos, 0, data.length - 1);
                System.out.println(dataInfos.length);
                Sensor s = chooseSensor(dataInfos.length, type, id, dataInfos);
                for (int j = 0; j < amount[k]; j++) {
                    if (s != null) {
                        s.generateDataPoint();
                        System.out.println(count++);
                    }
                }
                k ++;
                id ++;
                sensorList.add(s);
                if (length < RandomData.sensors.size()) {
                    RandomData.sensors.remove(i);
                }
                length --;
            }
            return sensorList;
        }
    }

    public static List<String> listFilesForFolder(final File folder) {
        List<String> filenames = new LinkedList<String>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if(fileEntry.getName().contains(".csv"))
                    filenames.add(fileEntry.getName());
            }
        }
        return filenames;
    }

    public static Sensor chooseSensor(int length, String type, int id, String[] dataInfos) {
        List<String[]> dataList = new ArrayList<>();
        Sensor s = null;
        switch (length) {
            case 0:
                return null;
            case 1:
                s = new MiniSensor(type, id, dataList, dataInfos[0], Sensor.dataTypes.get(dataInfos[0]));
                break;
            case 2:
                s = new SmallSensor(type, id, dataList, dataInfos[0], dataInfos[1], Sensor.dataTypes.get(dataInfos[0]),
                        Sensor.dataTypes.get(dataInfos[1]));
                break;
            case 3:
                s = new MediumSensor(type, id, dataList, dataInfos[0], dataInfos[1], dataInfos[2], Sensor.dataTypes.get(dataInfos[0]),
                        Sensor.dataTypes.get(dataInfos[1]), Sensor.dataTypes.get(dataInfos[2]));
                break;
        }
        return s;
    }
}
