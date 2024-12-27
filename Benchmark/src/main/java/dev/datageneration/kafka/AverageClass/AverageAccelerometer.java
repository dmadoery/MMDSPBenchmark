package dev.datageneration.kafka.AverageClass;

public class AverageAccelerometer {
    public double throttle;
    public int count;
    public int tickStart;
    public int tickEnd;
    public int id;


    public AverageAccelerometer(double throttle, int count, int tickStart, int tickEnd, int id) {
        this.throttle = throttle;
        this.count = count;
        this.tickStart = tickStart;
        this.tickEnd = tickEnd;
        this.id = id;
    }

    public double getTemp() {
        return throttle;
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

    public double getAverage() {
        double average;
        if(count != 0) {
            average = throttle/count;
        } else {
            return throttle;
        }
//        System.out.println(average[0] + " " + average[1] + " " + average[2]);
        return average;
    }
}
