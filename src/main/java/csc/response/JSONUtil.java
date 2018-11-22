package csc.response;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

public class JSONUtil {

    // convert string to a json entity
    // including JSONObject, JSONArray, String, int, double, boolean and JSONObject.NULL
    public static Object convertToToken(String json) throws IOException, JSONException {
        Object token;
        JsonNode node = convertToTreeNode(json);

        if (node.isTextual())
            token = node.textValue();
        else if (node.isInt())
            token = node.intValue();
        else if (node.isDouble()){
            token = node.doubleValue();
        }
        else if (node.isFloat()){
            token = node.floatValue();
        }
        else if (node.isBoolean())
            token = node.booleanValue();
        else if (node.isNull())
            token = JSONObject.NULL;
        else if (node.isObject())
            token =  new JSONObject(json);
        else if (node.isArray())
        {
            token = new JSONArray(json);
        }

        return null;
    }


    public static JsonNode convertToTreeNode(String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        // JsonNode tree = mapper.valueToTree(Object json);
        JsonNode tree = mapper.readTree(json);
        return tree;
    }

    public static boolean validateJSON(String json){
        try{
            convertToTreeNode(json);
            return true;
        }
        catch (IOException ex){
            return false;
        }
    }

    public static String loadFromYaml(String yaml_path) throws Exception {
        URL url = JSONUtil.class.getClassLoader().getResource(yaml_path);
        // InputStream is = ClassLoader.getSystemResourceAsStream(yaml_path);
        Yaml yaml = new Yaml();
        Object obj =  yaml.load( new FileInputStream(new File(url.toURI())));

        return writeObjectAsJsonString(obj, true);
    }

    public static String writeObjectAsJsonString(Object obj, boolean pretty) {
        if (obj instanceof JSONArray || obj instanceof JSONObject)
            return obj.toString();

        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            if (pretty)
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            else
                jsonString = mapper.writeValueAsString(obj);
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

        return jsonString;
    }

    public static <T> T convertToPOJO(JSONObject jo, Class<T> pojo)  {
        if (jo == null)
            return null;
        JsonNode jsonNode = convertToTreeNode(jo);

        ObjectMapper mapper = new ObjectMapper();
        try {
            T myPojo = mapper.readValue(new TreeTraversingParser(jsonNode), pojo);
            return myPojo;
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static <T> T convertToPOJO(JSONArray jsonArray, Class<T> pojo)  {
        if (jsonArray == null)
            return null;
        JsonNode jsonNode = convertToTreeNode(jsonArray);

        ObjectMapper mapper = new ObjectMapper();
        try {
            T myPojo = mapper.readValue(new TreeTraversingParser(jsonNode), pojo);
            return myPojo;
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static <T> T convertToPOJO(String jsonString, Class<T> pojo){
        if (jsonString == null)
            return null;
        ObjectMapper mapper = new ObjectMapper();
        try{
            T obj = mapper.readValue(jsonString, pojo);
            return obj;
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static <T> T convertToPOJO(TreeNode tree, Class<T> pojo){
        ObjectMapper mapper = new ObjectMapper();
        try{
            T obj = mapper.treeToValue(tree, pojo);
            return obj;
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    // Support for JSONArray, JSONObject, JsonNode and String
    public static boolean isTokenEmpty(Object token){
        if (token == null){
            return true;
        }
        if (token instanceof String){
            String str = token.toString();
            return str.equals("");
        }
        if (token instanceof JSONObject){
            int size = ((JSONObject)token).length();
            return size == 0;
        }
        if (token instanceof JSONArray){
            int size = ((JSONArray)token).length();
            return size == 0;
        }
        if(token instanceof JsonNode){
            throw new UnsupportedOperationException("JsonNode is not supported");
        }
        return false;
    }

    public static Object chain(JSONObject jo, String... key_chain) {
        if (key_chain == null || key_chain.length == 0)
            return null;
        try {
            Object temp = jo;
            JSONObject temp_jo = jo;
            for (String key : key_chain) {
                if (temp_jo.isNull(key))
                    return null;
                temp = temp_jo.get(key);
                if (temp instanceof JSONObject)
                    temp_jo = (JSONObject) temp;
                else
                    temp_jo = new JSONObject();
            }
            return temp;
        }
        catch (JSONException ex){
            throw new RuntimeException("Invalid key chain");
        }
    }

    public static JsonNode convertToTreeNode(JSONObject jo) {
        ObjectNode ret = JsonNodeFactory.instance.objectNode();

        @SuppressWarnings("unchecked")
        Iterator<String> iterator = jo.keys();
        for (; iterator.hasNext();) {
            String key = iterator.next();
            Object value;
            try {
                value = jo.get(key);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (jo.isNull(key))
                ret.putNull(key);
            else if (value instanceof String)
                ret.put(key, (String) value);
            else if (value instanceof Integer)
                ret.put(key, (Integer) value);
            else if (value instanceof Long)
                ret.put(key, (Long) value);
            else if (value instanceof Double)
                ret.put(key, (Double) value);
            else if (value instanceof Boolean)
                ret.put(key, (Boolean) value);
            else if (value instanceof JSONObject)
                ret.put(key, convertToTreeNode((JSONObject) value));
            else if (value instanceof JSONArray)
                ret.put(key, convertToTreeNode((JSONArray) value));
            else
                throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
        }
        return ret;
    }

    public static JsonNode convertToTreeNode(JSONArray jsonArray) {
        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value;
            try {
                value = jsonArray.get(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (jsonArray.isNull(i))
                ret.addNull();
            else if (value instanceof String)
                ret.add((String) value);
            else if (value instanceof Integer)
                ret.add((Integer) value);
            else if (value instanceof Long)
                ret.add((Long) value);
            else if (value instanceof Double)
                ret.add((Double) value);
            else if (value instanceof Boolean)
                ret.add((Boolean) value);
            else if (value instanceof JSONObject)
                ret.add(convertToTreeNode((JSONObject) value));
            else if (value instanceof JSONArray)
                ret.add(convertToTreeNode((JSONArray) value));
            else
                throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
        }
        return ret;
    }
}
