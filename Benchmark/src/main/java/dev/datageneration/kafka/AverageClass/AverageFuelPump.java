package dev.datageneration.kafka.AverageClass;

public class AverageFuelPump {
    public double temp;
    public double flowRate;
    public int count;
    public int tickStart;
    public int tickEnd;
    public int id;

    public AverageFuelPump(double temp, double flowRate, int count, int tickStart, int tickEnd, int id) {
        this.temp = temp;
        this.flowRate = flowRate;
        this.count = count;
        this.tickStart = tickStart;
        this.tickEnd = tickEnd;
        this.id = id;
    }

    public double getTemp() {
        return temp;
    }

    public double getPressure() {
        return flowRate;
    }

    public int getID() {
        return id;
    }

    public int getTickStart() {
        return tickStart;
    }

    public int getTickEnd() {
        return tickEnd;
    }

    public int getCount() {
        return count;
    }

    public double[] getAverage() {
        double[] average = new double[2];
        if(count != 0) {
            average[0] = temp/count;
            average[1] = flowRate/count;
        } else {
            return new double[]{temp, flowRate};
        }
//        System.out.println(average[0] + " " + average[1] + " " + average[2]);
        return average;
    }
}
