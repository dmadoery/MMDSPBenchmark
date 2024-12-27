package dev.datageneration.simulation;

import dev.datageneration.simulation.Sensors.*;
import dev.datageneration.simulation.types.IntType;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

public class RandomData {
    public static List<String[]> sensors = new ArrayList<>();


    public static Random random = new Random();
    public static long seed = 795673489;
    @Setter
    public static double peek;


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
        RandomData.sensors.add(new String[] {"heat", "temperature c"}); //heat sensor
        RandomData.sensors.add(new String[] {"heat", "temperature c"});//heat sensor,
        RandomData.sensors.add(new String[] {"tire", "temperature tire", "pressure psi", "wear", "liability", "position"});//front_left_tyre
        RandomData.sensors.add(new String[] {"tire", "temperature tire", "pressure psi", "wear", "liability", "position"});//front_right_tyre
        RandomData.sensors.add(new String[] {"tire", "temperature tire", "pressure psi", "wear", "liability", "position"});//rear_left_tyre
        RandomData.sensors.add(new String[] {"tire", "temperature tire", "pressure psi", "wear", "liability", "position"});//rear_right_tyre
        RandomData.sensors.add(new String[] {"speed", "kph","mph", "acceleration", "wind speed"});//speed_sensor
        RandomData.sensors.add(new String[] {"gForce", "g-lateral", "g-longitudinal"});//g_sensor
        RandomData.sensors.add(new String[] {"fuelPump", "temperature fuelP", "ml/min"});//fuel_pump_sensor
        RandomData.sensors.add(new String[] {"DRS", "on/off", "drs-zone"});//drs_sensor
        RandomData.sensors.add(new String[] {"brake", "temperature brake","brake_pressure", "wear"});//front_left_brake
        RandomData.sensors.add(new String[] {"brake", "temperature brake", "brake_pressure", "wear"});//front_right_brake
        RandomData.sensors.add(new String[] {"brake", "temperature brake", "brake_pressure", "wear"});//rear_left_brake
        RandomData.sensors.add(new String[] {"brake", "temperature brake", "brake_pressure", "wear"});//rear_right_brake
        RandomData.sensors.add(new String[] {"accelerometer", "throttlepedall"});
        RandomData.sensors.add(new String[] {"engine", "temperature engine", "rpm", "fuelFlow", "oil_pressure", "fuel_pressure", "exhaust"});
        RandomData.sensors.add(new String[] {"blackbox", "array_of_data"});
        RandomData.sensors.add(new String[] {"steering", "direction", "turning_degree"});
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

    public static Map<String, double[]> probabilities = new HashMap<>(){{
        put("temperature tire", new double[]{0.999, 0.001}); //TODO: adjust the probabilities
        put("temperature c", new double[]{0.999, 0.001});
        put("pressure psi", new double[]{0.999, 0.001});
        put("liability", new double[]{0.999, 0.001});
        put("kmp/h", new double[]{0.999, 0.001});
        put("mp/h", new double[]{0.999, 0.001});
        put("acceleration", new double[]{0.999, 0.001});
        put("wind speed", new double[]{0.999, 0.001});
        put("g-lateral", new double[]{0.999, 0.001});
        put("g-longitudinal", new double[]{0.999, 0.001});
        put("temperature fuelP", new double[]{0.999, 0.001});
        put("ml/min", new double[]{0.999, 0.001});
        put("temperature brake", new double[]{0.999, 0.001});
        put("brake_pressure", new double[]{0.999, 0.001});
        put("wear", new double[]{0.999, 0.001});
        put("temperature engine", new double[]{0.999, 0.001});
        put("rpm", new double[]{0.999, 0.001});
        put("fuelFlow", new double[]{0.999, 0.001});
        put("oil_pressure", new double[]{0.999, 0.001});
        put("fuel_pressure", new double[]{0.999, 0.001});
        put("exhaust", new double[]{0.999, 0.001});
        put("throttlepedall", new double[]{0.999, 0.001});
    }};

    public static double getRandomWithProbability(double min, double max, String name) {
        double range = max - min;
        if(probabilities.containsKey(name)) {
            double[] prob = probabilities.get(name);
            double boundary = range * prob[0];
            double[] highProb = new double[]{min, boundary};
            double[] lowProb = new double[]{boundary, max};
            if(random.nextDouble() <= prob[0]) {
                return random.nextDouble() * (highProb[1] - highProb[0]) + highProb[0];
            } else {
                return peek * (random.nextDouble() * (lowProb[1] - lowProb[0]) + lowProb[0]);
            }
        } else {
            return getRandom(min, max);
        }
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
        int k = 0;
        while (length != 0) {
            int i = (int) getRandom(0, RandomData.sensors.size());
            String[] data = RandomData.sensors.get(i);
            String type = data[0];
            String[] dataInfos = new String[data.length - 1];
            if (RandomData.sensors.get(i).length - 1 >= 0)
                System.arraycopy(data, 1, dataInfos, 0, data.length - 1);
            System.out.println(dataInfos.length);
            System.out.println(Arrays.toString(dataInfos));
            Sensor s = chooseSensor(dataInfos.length, type, id, dataInfos);
            for (int j = 0; j < amount[k]; j++) {
                if (s != null) {
                    s.generateDataPoint();
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

    public static List<String> listFilesForFolder(final File folder) {
        List<String> filenames = new LinkedList<String>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if(fileEntry.getName().contains(".json"))
                    filenames.add(fileEntry.getName());
            }
        }
        return filenames;
    }

    public static Sensor chooseSensor(int length, String type, int id, String[] dataInfos) {
        Sensor s = null;
        if(type.equals("blackbox")) {
            s = new BlackboxSensor(type, id, dataInfos);
            return s;
        }
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
            case 4:
                s = new NormalSensor(type, id, dataInfos);
                break;
            case 5:
                s = new LongSensor(type, id, dataInfos);
                break;
            case 6:
                s = new BigSensor(type, id, dataInfos);
                break;
        }
        return s;
    }
}
