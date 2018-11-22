package csc.response;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVReader {

    public static List<CSVRecord>  parseCSV(String file_name) throws Exception {
        URL url = CSVReader.class.getClassLoader().getResource(file_name);

        CSVParser parser = new CSVParser(new FileReader(new File(url.toURI())), CSVFormat.DEFAULT.withHeader());
        List<CSVRecord> records = new ArrayList<CSVRecord>();

        for (CSVRecord record : parser)
        {
            records.add(record);
        }
        parser.close();
        return records;
    }

    public static HashMap<String, CSVRecord> parseCSV(String file_name, String key_column) throws Exception {
        URL url = CSVReader.class.getClassLoader().getResource(file_name);
        if (url == null){
            System.out.println("File " + file_name + " not found in resource");
        }

        CSVParser parser = new CSVParser(new FileReader(new File(url.toURI())), CSVFormat.DEFAULT.withHeader());
        HashMap<String, CSVRecord> recordMap = new HashMap<String, CSVRecord>();

        HashMap<String, Integer> headerMap = (HashMap<String, Integer>) parser.getHeaderMap();
        if (!(headerMap.containsKey(key_column))){
            throw new Exception("Column key " + key_column + " not found");
        }
        int key_index = headerMap.get(key_column);
        for (CSVRecord record : parser)
        {
            recordMap.put(record.get(key_index), record);
        }
        parser.close();
        return recordMap;
    }
}
