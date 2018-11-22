package csc.response.resolver;

import csc.response.MainLoader;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Component
public class MappingResolver {
    @Value("${CONSTANT.dateFormat}")
    String default_date_format;

    @Autowired
    MainLoader loader;

    //
    public JSONObject resolve(Object o) throws JSONException {
        JSONObject jo = new JSONObject();
        List<CSVRecord> list = loader.getMapping();
        for(CSVRecord record : list)
        {
            String display_name = record.get(0);
            String attr = record.get(1);
            Object value = GetterResolver.runMethod("get"+attr, o);

            if (value instanceof Date){
                String format = record.get("format");
                if (format == null)
                    format = default_date_format;
                else if (format.equals("") || format.equals("NA")){
                    format = default_date_format;
                }
                value = GetterResolver.convertDateToString((Date) value, format);
            }
            jo.put(display_name, value);
        }

        return jo;
    }

}
