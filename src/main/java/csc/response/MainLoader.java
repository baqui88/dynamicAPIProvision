package csc.response;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@Component
public class MainLoader {
    private JSONObject endpoint_jo;
    private HashMap<String, CSVRecord> attr_ref_map;
    private JSONObject callback_jo;
    private List<CSVRecord> mapping;

    @Autowired
    public MainLoader(Environment env) throws Exception {
        String endpoint_json  = JSONUtil.loadFromYaml("endpoint.yaml");
        String callback_json = JSONUtil.loadFromYaml("callback.yaml");
        endpoint_jo = new JSONObject(endpoint_json);
        callback_jo = new JSONObject(callback_json);

        PrintWriter out = new PrintWriter("endpoint.json");
        out.println(endpoint_json);
        out.close();

        attr_ref_map = CSVReader.parseCSV("attribute-ref.csv", "_ref");
        mapping = CSVReader.parseCSV("mapping.csv");
    }

    public JSONObject getEndpointJSONObject(){
        return endpoint_jo;
    }

    public JSONObject getCallbackJSONObject(){
        return callback_jo;
    }

    public HashMap<String, CSVRecord> getAttributeRefMap(){
        return attr_ref_map;
    }

    public List<CSVRecord> getMapping(){
        return mapping;
    }
}

