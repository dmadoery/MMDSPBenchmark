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
        RandomData.sensors.add(new String[] {"speed", "kmp/h","mp/h"});//speed_sensor
        RandomData.sensors.add(new String[] {"g_force", "direction", "g"});//g_sensor
        RandomData.sensors.add(new String[] {"fuel_pump", "temperature celsius", "ml/min"});//fuel_pump_sensor
        RandomData.sensors.add(new String[] {"DRS", "on/off", "drs-zone"});//drs_sensor
        RandomData.sensors.add(new String[] {"brake", "temperature celsius","brake_pressure"});//front_left_brake
        RandomData.sensors.add(new String[] {"brake", "temperature celsius", "brake_pressure"});//front_right_brake
        RandomData.sensors.add(new String[] {"brake", "temperature celsius", "brake_pressure"});//rear_left_brake
        RandomData.sensors.add(new String[] {"brake", "temperature celsius", "brake_pressure"});//rear_right_brake
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "direction"});
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "direction"});


        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});
        RandomData.sensors.add(new String[] {"long", "temperature celsius", "brake_pressure", "test", "test"});

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
                Sensor s = sensorBuilder(i, amount, id);
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
//                System.out.println(dataInfos.length);
                Sensor s = chooseSensor(dataInfos.length, type, id, dataInfos);
                for (int j = 0; j < amount[k]; j++) {
                    if (s != null) {
                        s.generateDataPoint();
//                        System.out.println(count++);
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
                if(fileEntry.getName().contains(".json")) //TODO: change here if .csv is used
                    filenames.add(fileEntry.getName());
            }
        }
        return filenames;
    }

    public static Sensor chooseSensor(int length, String type, int id, String[] dataInfos) { //TODO: add more Sensor sizes
        Sensor s = null;
        switch (length) {
            case 0:
                return null;
            case 1:
                s = new MiniSensor(type, id, dataInfos[0]);
                break;
            case 2:
                s = new SmallSensor(type, id, dataInfos);
                break;
            case 3:
                s = new MediumSensor(type, id, dataInfos);
                break;
            //TODO: extend
//            case 4:
//                s = new NormalSensor(type, id, dataInfos);
//                break;
//            case 5:
//                s = new LongSensor(type, id, dataInfos);
//                break;
//            case 6:
//                s = new BigSensor(type, id, dataInfos);
//                break;
//            case 7:
//                s = new UltraSensor(type, id, dataInfos);
//                break;
        }
        return s;
    }

    public static Sensor sensorBuilder(int i, int[] amount, int id) {
        String[] data = RandomData.sensors.get(i);
        String type = data[0];
        String[] dataInfos = new String[data.length - 1];
        if (RandomData.sensors.get(i).length - 1 >= 0)
            System.arraycopy(data, 1, dataInfos, 0, data.length - 1);
//                System.out.println(dataInfos.length);
        Sensor s = chooseSensor(dataInfos.length, type, id, dataInfos);
        for (int j = 0; j < amount[i]; j++) {
            if (s != null) {
                s.generateDataPoint();
//                        System.out.println(count++);
            }
        }
        return s;
    }
}
