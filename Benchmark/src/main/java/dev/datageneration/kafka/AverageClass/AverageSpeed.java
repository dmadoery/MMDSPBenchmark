package dev.datageneration.kafka.AverageClass;

public class AverageSpeed {
    public double speed;
    public double wind;
    public int count;
    public int tickStart;
    public int tickEnd;
    public int id;

    public AverageSpeed(double speed, double wind, int count, int tickStart, int tickEnd, int id) {
        this.speed = speed;
        this.wind = wind;
        this.count = count;
        this.tickStart = tickStart;
        this.tickEnd = tickEnd;
        this.id = id;
    }

    public double getTemp() {
        return speed;
    }

    public double getPressure() {
        return wind;
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
            average[0] = speed/count;
            average[1] = wind/count;
        } else {
            return new double[]{speed, wind};
        }
//        System.out.println(average[0] + " " + average[1] + " " + average[2]);
        return average;
    }
}
