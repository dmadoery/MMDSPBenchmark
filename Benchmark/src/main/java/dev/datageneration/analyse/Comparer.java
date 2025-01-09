package dev.datageneration.analyse;

import dev.datageneration.jsonHandler.JsonFileHandler;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Comparer {
    static List<JSONObject> dataSent = new LinkedList<>();
    static List<JSONObject> averagedData = new LinkedList<>();
    public static List<JSONObject> windowedData = new LinkedList<>();
    @Setter
    static File folder;
    static final String file1 = "ALL_DATA.json";
    static final String file2 = "averagedData.json";//"finalData.json";
    static final String file3 = "windowedData.json";
    @Getter
    static List<String> peeksStore = new LinkedList<>();
    @Getter
    static int rightWindow = 0;
    @Getter
    static int falseWindow = 0;
    @Getter
    static List<JSONObject> errors = new LinkedList<>();
    @Getter
    static List<JSONObject> warnings = new LinkedList<>();


    public static String comparing(List<JSONObject> aggregatedDataReceived, List<JSONObject> dataReceived, boolean aggregated) throws IOException {
        if(aggregated) {
            JsonFileHandler.readJsonFile(folder, file2, averagedData);
            JsonFileHandler.readJsonFile(folder, file3, windowedData);

            String messageAgg = compareAveraged(averagedData, false);

            String messageWarnRec = compareWarnings(dataReceived);
            String messageAggRec = compareAveraged(aggregatedDataReceived, true);

            return  messageAgg + "\n" + messageAggRec + "\n" + messageWarnRec;
        } else {
            JsonFileHandler.readJsonFile(folder, file1, dataSent);
            return compareData(dataReceived);
        }
    }

    private static String compareData(List<JSONObject> dataReceived) {
        String message = "\nData quality metrics:";
        List<JSONObject> miss = new LinkedList<>(dataSent);
        miss.removeIf(received ->
                dataReceived.stream().anyMatch(existing -> areJsonObjectsEqual(received, existing))
        );
        double percentage = 100 / (double) dataSent.size() * (double) dataReceived.size();
        if(!dataSent.isEmpty()) {
            message += "\nNot all Data received, missing " + dataSent.size() + " data." +
                        "\n                      Completeness: " + percentage + "%";
        } else {
            message += "\nReceived data " + dataReceived.size() + ". Completeness: " + percentage;
        }
        return message;
    }

    private static String compareAveraged(List<JSONObject> windowList, boolean received) throws IOException {
        int[] peeks = new int[7];
        List<List<JSONObject>> peekWindows = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            peekWindows.add(new LinkedList<>());
            peeksStore.add("");
        }
        for(JSONObject data : windowList) {
            String type = data.getJSONObject("data").getString("type");
            switch (type) {
                case "tire":
                    peekWindows.getFirst().add(data);
                    if (data.getJSONObject("data").getDouble("averageTemp") > 110) {
                        peeks[0]++;
                    }
                    if (data.getJSONObject("data").getDouble("averagePressure") > 30) {
                        peeks[0]++;
                    }
                    if (data.getJSONObject("data").getInt("averageWear") > 90) {
                        peeks[0]++;
                    }
//                    if (data.getJSONObject("data").getInt("averageLiability") > 90) {
//                        peeks[0]++;
//                    }
                    break;

                case "heat":
                    peekWindows.get(1).add(data);
                    if (data.getJSONObject("data").getDouble("averageTemp") > 50) {
                        peeks[1]++;
                    }
                    break;

                case "engine":
                    peekWindows.get(2).add(data);
                    if (data.getJSONObject("data").getDouble("averageTemp") > 600) {
                        peeks[2]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageOilPressure") > 7) {
                        peeks[2]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageFuelPressure") > 5) {
                        peeks[2]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageRPM") > 18000) {
                        peeks[2]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageFuelFlow") > 120) {
                        peeks[2]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageExhaust") > 1.2) {
                        peeks[2]++;
                    }
                    break;

                case "brake":
                    peekWindows.get(3).add(data);
                    if (data.getJSONObject("data").getDouble("averageTemp") > 1000) {
                        peeks[3]++;
                    }
                    if (data.getJSONObject("data").getDouble("averagePressure") > 10) {
                        peeks[3]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageWear") > 90) {
                        peeks[3]++;
                    }
                    break;

                case "fuelPump":
                    peekWindows.get(4).add(data);
                    if (data.getJSONObject("data").getDouble("averageTemp") > 60) {
                        peeks[4]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageFlowRate") > 4000) {
                        peeks[4]++;
                    }
                    break;

                case "accelerometer":
                    peekWindows.get(5).add(data);
                    if (data.getJSONObject("data").getDouble("averageThrottlepedall") > 100) {
                        peeks[5]++;
                    }
                    break;

                case "speed":
                    peekWindows.get(6).add(data);
                    if (data.getJSONObject("data").getDouble("averageWindSpeed") > 200) {
                        peeks[6]++;
                    }
                    if (data.getJSONObject("data").getDouble("averageSpeed kph") > 360) {
                        peeks[6]++;
                    }
                    break;
            }
        }
        int j = 0;
        StringBuilder message = new StringBuilder();
        String receivedSended = "sent";
        if(received) {
            receivedSended = "received";
            j = 7;
        }
        for(int i = 0; i < peekWindows.size(); i++) {
            if (!peekWindows.get(i).isEmpty()) {
                String type = peekWindows.get(i).getFirst().getJSONObject("data").getString("type");
                message.append("Amount of windows ").append(receivedSended).append(" from: ").append(type).append(" ").append(peekWindows.get(i).size()).append(".\n");
                message.append("Amount of peaks ").append(receivedSended).append(" from: ").append(type).append(" ")
                        .append(peeks[i]).append(".\n");
                double percent = ((double) 100 / peekWindows.get(i).size()) * peeks[i];
                message.append("Percentage of ").append(type).append(": ").append(percent).append(" %\n\n");
                peeksStore.add(i + j, receivedSended + ":" + type + ":" + peeks[i] + ":" + percent);
            }
        }

        //check peeks
        if(receivedSended.equals("received")) {
            String peekChecker = checkPeeks(peekWindows);
            return message.toString() + peekChecker;
        }

        return message.toString();
    }

    private static String compareWarnings(List<JSONObject> dataReceived) {
        List<JSONObject> missing = new LinkedList<>(windowedData);
        missing.removeIf(received ->
                dataReceived.stream().anyMatch(existing -> areJsonObjectsEqual(received, existing))
        );
        if(missing.isEmpty()) {
            return "\nAll errors and Warnings received correctly.\n";
        } else {

            for (JSONObject entry: missing) {
                if(entry.has("WarningMessage")) {
                    warnings.add(entry);
                } else {
                    errors.add(entry);
                }
            }
            for(JSONObject warning: warnings) {
                System.out.println(warning.toString());
            }

            return "\nThere are missing: " + errors.size() + " errors and " + warnings.size() + " warnings.\n";

        }
    }

    private static String checkPeeks(List<List<JSONObject>> peekWindows) {
        for(List<JSONObject> list: peekWindows)  {
            for(JSONObject peek : list) {
                int start = peek.getInt("startTime");
                int end = peek.getInt("endTime");
                JSONObject data = peek.getJSONObject("data");
                String type = data.getString("type");
                int id = data.getInt("id");
                switch (type) {
                    case "tire":
                        if (data.getDouble("averageTemp") > 110) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averagePressure") > 30) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }

                        if (data.getInt("averageWear") > 90) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        //not in Kafka
//                        if (data.getInt("averageLiability") > 90) {
    //                        if (peekChecker(id, start, end)) {
    //                            rightWindow++;
    //                        } else {
    //                            falseWindow++;
    //                        }
//                        }

                        break;

                    case "heat":
                        if (data.getDouble("averageTemp") > 50) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    case "engine":
                        if (data.getDouble("averageTemp") > 600) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageOilPressure") > 7) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageFuelPressure") > 5) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageRPM") > 18000) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageFuelFlow") > 120) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageExhaust") > 1.2) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    case "brake":
                        if (data.getDouble("averageTemp") > 1000) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averagePressure") > 10) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageWear") > 90) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    case "fuelPump":
                        if (data.getDouble("averageTemp") > 60) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageFlowRate") > 4000) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    case "accelerometer":
                        if (data.getDouble("averageThrottlepedall") > 100) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    case "speed":
                        if (data.getDouble("averageWindSpeed") > 200) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("averageSpeed kph") > 360) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;
                }
            }
        }

        String correct = "Amount right peaks: " + rightWindow + ".\n";
        String wrong = "Amount false peaks: " + falseWindow + ".";
        return correct + wrong;
    }

    private static boolean peekChecker(int id, int start, int end) {
        boolean found = false;
        for (JSONObject data : windowedData) {
            int iD = data.getJSONObject("data").getInt("id");
            int tick = data.getInt("tick");
            if(data.has("WarningMessage")) {
                if (iD == id && start <= tick  && tick <= end ) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    private static boolean areJsonObjectsEqual(JSONObject obj1, JSONObject obj2) {
        return obj1.similar(obj2); // Checks if the content of two JSONObjects is the same
    }

}
