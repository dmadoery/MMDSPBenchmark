package org.example;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.opencsv.CSVWriter;

public class SensorGenerator {
    //header 
    private String[] header;
    ArrayList<Object> data = new ArrayList <>();

    public SensorGenerator(String name, String data1, String data2) {
        header = new String[] {name, "id", "date", "time", data1, data2};
    }

    public void main(String[] args) {
        SensorGenerator sensor1 = new SensorGenerator("test", "temp", "preassure");
        File file = new File("/resources/" + sensor1.header[0] + ".csv"); 
        try { 
            // create FileWriter object with file as parameter 
            FileWriter outputfile = new FileWriter(file); 
    
            // create CSVWriter object filewriter object as parameter 
            CSVWriter writer = new CSVWriter(outputfile); 
    
            // adding header to csv  
            writer.writeNext(sensor1.header); 

            //create data
            Date date = new Date();
            System.out.println(date);

            // add data to csv 
            String[] data1 = { "Aman", "10", "620" }; 
            writer.writeNext(data1); 
            String[] data2 = { "Suraj", "10", "630" }; 
            writer.writeNext(data2); 
    
            // closing writer connection 
            writer.close(); 
        } 
        catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
    }
}
