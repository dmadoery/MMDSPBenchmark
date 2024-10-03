import static java.lang.System.in;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;


public class DataGenerator {
    
    public Object obj;

    public static String[] split(String line) {
        String[] splited = line.split(";", 10);
        return splited;
    }


    public static void main(String[] args) throws  Exception {
        String[][] data = new String[20500][5];
        Sensor[] frontLeftTyre = new Sensor[20500];
        try(FileReader in = new FileReader("frontLeftTyre.csv"); 
            BufferedReader br = new BufferedReader(in)) {
            int j = 0;
            while(true) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                if(line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                else{
                    String[] splited = split(line);
                    //System.out.println(splited.length);
                    System.arraycopy(splited, 0, data[j], 0, splited.length);
                    // System.out.println(Arrays.toString(data[j]));
                    // System.out.println(data.length);
                    for (String item : data[j]) {
                        System.out.println("Row "+ j + ": " + item);
                    }
                    if (j > 0) {
                        int l = data[j].length - 1;
                        frontLeftTyre[j] = new Sensor(data[j][l-4], data[j][l-3], data[j][l-2], Double.parseDouble(data[j][l-1]), Double.parseDouble(data[j][l]));
                    } else {
                        frontLeftTyre[j] = new Sensor("test", "test", "test", 12.555, 24.555);
                    }
                    frontLeftTyre[j].print();
                    
                }
                j++;
            }
            br.close();
        }
    }
}