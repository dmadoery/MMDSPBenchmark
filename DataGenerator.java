import static java.lang.System.in;
import java.io.BufferedReader;
import java.io.FileReader;

public class DataGenerator {
    
    public Object obj;

    public static String[] split(String line) {
        String[] splited = line.split(";", 10);
        return splited;
    }


    public static void main(String[] args) throws  Exception {
        String[][] data = new String[5][20];
        try(FileReader in = new FileReader("TestData.csv"); 
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
                    for(int k = 0; k<splited.length; k++) {
                        data[j][k] = splited[k];
                    }
                    for(int i = 0; i < data.length; i++) {
                        System.out.println("Row "+ j + ": " + data[j][i]);
                    }
                }
                j++;
            }
            br.close();
        }
    }
}