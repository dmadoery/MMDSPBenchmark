package dev.datageneration.analyse;

import dev.datageneration.jsonHandler.JsonFileHandler;
import lombok.Setter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Comparer {
    static List<JSONObject> dataSent = new LinkedList<>();
    static List<JSONObject> averagedData = new LinkedList<>();
    static List<JSONObject> windowedData = new LinkedList<>();
    @Setter
    static File folder;
    static final String file1 = "ALL_DATA.json";
    static final String file2 = "averagedData.json";//"finalData.json";
    static final String file3 = "windowedData.json";

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
            message += "\nAll " + dataReceived.size() + " data received. Completeness: " + percentage;
        }
        return message;
    }

    private static String compareAveraged(List<JSONObject> windowList, boolean received) throws IOException {
        //TODO: Comment out the not implemented averages in kafka!
        int[] peeks = new int[7];
        List<List<JSONObject>> peekWindows = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            peekWindows.add(new ArrayList<>());
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
                    //not in Kafka
//                    if (data.getJSONObject("data").getInt("wear") > 90) {
//                        peeks[0]++;
//                    }
                    break;

                //not in Kafka yet
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
                    //not in Kafka
//                    if (data.getJSONObject("data").getDouble("averageExhaust") > 1.2) {
//                        peeks[2]++;
//                    }
                    break;

                // not in Kafka
                case "break":
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

                //not in Kafka
                case "fuelPump":
                    peekWindows.get(4).add(data);
                    if (data.getJSONObject("data").getDouble("averageTemp") > 1000) {
                        peeks[4]++;
                    }
                    if (data.getJSONObject("data").getDouble("ml/min") > 4000) {
                        peeks[4]++;
                    }
                    break;

                //not in Kafka
                case "accelerometer":
                    peekWindows.get(5).add(data);
                    if (data.getJSONObject("data").getDouble("throttlepedall") > 100) {
                        peeks[5]++;
                    }
                    break;

                //not in Kafka
                case "speed":
                    peekWindows.get(6).add(data);
                    if (data.getJSONObject("data").getDouble("wind speed") > 200) {
                        peeks[6]++;
                    }
                    if (data.getJSONObject("data").getDouble("kph") > 360) {
                        peeks[6]++;
                    }
                    break;
            }
        }
        StringBuilder message = new StringBuilder();
        String receivedSended = "sent";
        if(received) {
            receivedSended = "received";
        }
        for(int i = 0; i < peekWindows.size(); i++) {
            if (!peekWindows.get(i).isEmpty()) {
                String type = peekWindows.get(i).getFirst().getJSONObject("data").getString("type");
                message.append("Amount of windows ").append(receivedSended).append(" from: ").append(type).append(" ").append(peekWindows.get(i).size()).append(".\n");
                message.append("Amount of peeks ").append(receivedSended).append(" from: ").append(type).append(" ")
                        .append(peeks[i]).append(".\n");
                double percent = ((double) 100 / peekWindows.get(i).size()) * peeks[i];
                message.append("Percentage of ").append(type).append(": ").append(percent).append(" %\n\n");
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

        windowedData.removeIf(received ->
                dataReceived.stream().anyMatch(existing -> areJsonObjectsEqual(received, existing))
        );
        if(windowedData.isEmpty()) {
            return "\nAll errors and Warnings received correctly.\n";
        } else {
            List<JSONObject> errors = new LinkedList<>();
            List<JSONObject> warnings = new LinkedList<>();
            for (JSONObject entry: windowedData) {
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
        int rightWindow = 0;
        int falseWindow = 0;
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
                          //not in Kafka
//                      if (data.getInt("wear") > 90) {
//
//                      }
                        break;

                    //not in Kafka yet
                    case "heat":
                        peekWindows.get(1).add(data);
                        if (data.getDouble("averageTemp") > 50) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    case "engine":
                        peekWindows.get(2).add(data);
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
                          //not in Kafka
//                        if (data.getJSONObject("data").getDouble("averageExhaust") > 1.2) {
//                            if (peekChecker(id, start, end)) {
//                                rightWindow++;
//                            } else {
//                                falseWindow++;
//                            }
//                        }
                        break;

                    // not in Kafka
                    case "break":
                        peekWindows.get(3).add(data);
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

                    //not in Kafka
                    case "fuelPump":
                        peekWindows.get(4).add(data);
                        if (data.getDouble("averageTemp") > 1000) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("ml/min") > 4000) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    //not in Kafka
                    case "accelerometer":
                        peekWindows.get(5).add(data);
                        if (data.getDouble("throttlepedall") > 100) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        break;

                    //not in Kafka
                    case "speed":
                        peekWindows.get(6).add(data);
                        if (data.getDouble("wind speed") > 200) {
                            if (peekChecker(id, start, end)) {
                                rightWindow++;
                            } else {
                                falseWindow++;
                            }
                        }
                        if (data.getDouble("kph") > 360) {
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
        if(falseWindow == 0) {
            return "All " + rightWindow +" peeks correct";
        } else {
            return "Amount wrong peeks wrong: " + falseWindow;
        }
    }

    private static boolean peekChecker(int id, int start, int end) {
        for (JSONObject data : windowedData) {
            if(data.has("WarningMessage")) {
                if (data.getJSONObject("data").getInt("id") == id
                        && data.getInt("tick") >= start && data.getInt("tick") <= end) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean areJsonObjectsEqual(JSONObject obj1, JSONObject obj2) {
        return obj1.similar(obj2); // Checks if the content of two JSONObjects is the same
    }

}
