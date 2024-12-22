package dev.datageneration.kafka.AverageClass;

public class AverageTire {
    public double temp;
    public double pressure;
    public int count;
    public int tickStart;
    public int tickEnd;
    public int id;
    public int position;
    public int wear;

    public AverageTire(double temp, double pressure, int count, int tickStart, int tickEnd, int id, int position, int wear) {
        this.temp = temp;
        this.pressure = pressure;
        this.count = count;
        this.tickStart = tickStart;
        this.tickEnd = tickEnd;
        this.id = id;
        this.position = position;
        this.wear = wear;
    }

    public double getTemp() {
        return temp;
    }

    public double getPressure() {
        return pressure;
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

    public int getPosition() {
        return position;
    }

    public int getCount() {
        return count;
    }

    public double[] getAverage() {
        double[] average = new double[3];
        if(count != 0) {
            average[0] = temp/count;
            average[1] = pressure/count;
            average[2] = (double)wear/count;
        } else {
            return new double[]{temp, pressure, wear};
        }

        return average;
    }
}
