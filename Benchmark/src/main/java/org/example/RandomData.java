package org.example;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.*;

public class RandomData {
    public static List<String[]> sensors = new ArrayList<>();

    //Info of all possible data types, with their possible min and max values.
    public static String[][] dataTypes = new String[][]{{"temperature celsius", "80", "110"}, {"pressure psi", "25", "30"},
            {"kmp/h", "0", "380"}, {"mp/h", "0", "236.121"}, {"direction", "0", "359"}, {"g", "0", "10"},
            {"brake_pressure", "0", "10"}, {"ml/min", "500", "4000"}, {"test", "0", "10"}, {"on/off", "0", "1"}, {"drs-zone", "0", "3"},
            {"test", "0", "10"} //TODO: add more types here
    };
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

        if (length == RandomData.sensors.size()) {
            for (int i = 0; i < length; i++) {
                String[] header = create_header(i, id);
                Sensor s = Sensor.parse(List.of(header));
                for (int j = 0; j < amount[i]; j++) {
                    create_data(s, header.length + 2);
                }
                sensorList.add(s);
                id ++;
            }
            return sensorList;

        } else {
            int k = 0;
            while (length != 0) {
                int i = (int) getRandom(0, RandomData.sensors.size());
                String [] header = create_header(i, id);
                Sensor s = Sensor.parse(List.of(header));
                for (int j = 0; j < amount[k]; j++) {
                    create_data(s, header.length + 2);
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

    private static void create_data(Sensor sensor, int l) {
        String[] data = new String[l];
        data[0] = sensor.type;
        data[1] = sensor.id;
        data[2] = getDate();
        data[3] = getTime();

        // For sensor data generation, avoid iterating over all data types unnecessarily
        if (sensor.data1Info != null) {
            data[4] = generateSensorData(sensor.data1Info);
        }
        if (sensor.data2Info != null) {
            data[5] = generateSensorData(sensor.data2Info);
        }
        if (sensor.data3Info != null) {
            data[6] = generateSensorData(sensor.data3Info);
        }
        if (sensor.data4Info != null) {
            data[7] = generateSensorData(sensor.data4Info);
        }
        if (sensor.data5Info != null) {
            data[8] = generateSensorData(sensor.data5Info);
        }
        if (sensor.data6Info != null) {
            data[9] = generateSensorData(sensor.data6Info);
        }

        sensor.add(data);
    }

    /**
     * Creates random entries for the sensor according to the data type.
     * @param dataType String
     * @return random entry of the given data type
     */
    private static String generateSensorData(String dataType) {
        for (String[] d : dataTypes) {
            if (d[0].equals(dataType)) {
                return String.valueOf(getRandom(Double.parseDouble(d[1]), Double.parseDouble(d[2])));
            }
        }
        return "N/A"; // Default case if no matching data type found
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

    public static String[] create_header(int i, int id) {
        String[] sens = RandomData.sensors.get(i);
        String[] header = new String[sens.length + 1];
        header[0] = sens[0];
        header[1] = String.valueOf(id);
        System.arraycopy(sens, 1, header, 2, sens.length - 1);
        return header;
    }
}
