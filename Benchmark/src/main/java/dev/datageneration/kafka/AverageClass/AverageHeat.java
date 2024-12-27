package dev.datageneration.kafka.AverageClass;

public class AverageHeat {
    public double temp;
    public int count;
    public int tickStart;
    public int tickEnd;
    public int id;


    public AverageHeat(double temp, int count, int tickStart, int tickEnd, int id) {
        this.temp = temp;
        this.count = count;
        this.tickStart = tickStart;
        this.tickEnd = tickEnd;
        this.id = id;
    }

    public double getTemp() {
        return temp;
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
            average = temp/count;
        } else {
            return temp;
        }
//        System.out.println(average[0] + " " + average[1] + " " + average[2]);
        return average;
    }
}
