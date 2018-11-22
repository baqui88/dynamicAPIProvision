package csc.response.resolver;

// import com.google.gson.JsonObject;
import csc.response.MainLoader;
import csc.response.JSONUtil;
import csc.response.expression.ExpressionChecker;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;

public class GeneralResponseResolver {
    // how to autowire bean from singleton to request bean ??
    protected CallbackResolver callbackResolver;
    protected PlaceholderResolver placeholderResolver;
    protected GetterResolver getterResolver;
    protected MappingResolver mappingResolver;
    protected MainLoader loader;

    protected Object token;

    // context object , which response is related
    // object may be an item or collection
    protected Object contextObject;

    // call bean dependency manually
    public void setApplicationContext(ApplicationContext applicationContext) {

        BeanFactory factory=  applicationContext.getAutowireCapableBeanFactory();
        callbackResolver = factory.getBean(CallbackResolver.class);
        placeholderResolver = factory.getBean(PlaceholderResolver.class);
        getterResolver = factory.getBean(GetterResolver.class);
        loader = factory.getBean(MainLoader.class);
        mappingResolver = factory.getBean(MappingResolver.class);

        readJSONToken();
    }

    protected void readJSONToken() { }

    public void putContextObject(Object object){
        this.contextObject = object;
    }

    protected Object resolveExpression(String expression) {
        Object obj;
        expression = expression.trim();
        if (ExpressionChecker.isCallbackExpression(expression)){
            obj = callbackResolver.resolve(expression);
        }
        else if (ExpressionChecker.isPlaceholderExpression(expression)){
            obj = placeholderResolver.resolve(expression);
        }
        else if (ExpressionChecker.isGetterExpression(expression))
        {
            obj = getterResolver.resolve(expression, contextObject);
        }
        else {
            obj = expression;
        }
        return obj;
    }

    public Object responseJSONToken(){
        if (token instanceof String || token instanceof Integer || token instanceof Long ||
                token instanceof Double || token instanceof Boolean)
            return token;
        if (token == JSONObject.NULL)
            return token;

        if (token instanceof JSONObject) {
            JSONObject jo_response = null;
            try {
                jo_response = reconstruct((JSONObject) token, true);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(" Failed when reconstruct json object");
            }
            return jo_response;
        }

        if (token instanceof JSONArray) {
            JSONArray ja_response = null;
            try {
                ja_response = reconstruct((JSONArray) token, true);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(" Failed when reconstruct json array");
            }
            return ja_response;
        }

        throw new RuntimeException("Cannot resolve a json token");
    }

    public String responseJSONString(){
        return JSONUtil.writeObjectAsJsonString(responseJSONToken(), true);
    }

    public void setJSONOToken(Object jo) {
        this.token = jo;
    }

    // create a new instance, which load dependency from exist bean
    public static GeneralResponseResolver newInstance(GeneralResponseResolver fromObject){
        GeneralResponseResolver r = new GeneralResponseResolver();
        r.callbackResolver = fromObject.callbackResolver;
        r.placeholderResolver = fromObject.placeholderResolver;
        r.getterResolver = fromObject.getterResolver;
        r.loader = fromObject.loader;
        return r;
    }

    protected JSONObject reconstruct(JSONObject jo, boolean resolve) throws JSONException {
        JSONObject ret = new JSONObject();

        @SuppressWarnings("unchecked")
        Iterator<String> iterator = jo.keys();
        for (; iterator.hasNext();) {
            String key = iterator.next();
            Object value;
            value = jo.get(key);

            if (value == null || value == JSONObject.NULL) {
                ret.put(key, JSONObject.NULL);
            }
            else if (value instanceof String) {
                if (resolve) {
                    Object resolve_value = resolveExpression((String) value);
                    putValueToJsonObject(ret, key, resolve_value, false);
                }
                else
                    ret.put(key, (String) value);
            }
            else{
                putValueToJsonObject(ret, key, value, resolve);
            }
        }
        return ret;
    }

    protected void putValueToJsonObject(JSONObject jo, String key, Object value, boolean resolve) throws JSONException {
        if (value == null || value == JSONObject.NULL){
            jo.put(key, JSONObject.NULL);
        }
        else if (value instanceof String) {
            jo.put(key, (String) value);
        }
        else if (value instanceof Integer)
            jo.put(key, (Integer) value);
        else if (value instanceof Long)
            jo.put(key, (Long) value);
        else if (value instanceof Double)
            jo.put(key, (Double) value);
        else if (value instanceof Boolean)
            jo.put(key, (Boolean) value);
        else if (value instanceof JSONObject)
            jo.put(key, reconstruct((JSONObject) value, resolve));
        else if (value instanceof JSONArray)
            jo.put(key, reconstruct((JSONArray) value, resolve));
        else
            throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
    }

    protected  JSONArray reconstruct(JSONArray jsonArray, boolean resolve) throws JSONException
    {
        JSONArray ret = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value;
            value = jsonArray.get(i);

            if (value == null || value == JSONObject.NULL)
                ret.put(JSONObject.NULL);
            else if (value instanceof String){
                if (resolve) {
                    Object resolved_value = resolveExpression((String) value);
                    putValueToArrayNode(ret, resolved_value, contextObject, false);
                }
                else
                    ret.put((String) value);
            }
            else{
                putValueToArrayNode(ret, value, contextObject, resolve);
            }
        }
        return ret;
    }

    protected void putValueToArrayNode(JSONArray jsonArray, Object value, Object item, boolean resolve) throws JSONException {
        if (value == null || value == JSONObject.NULL){
            jsonArray.put(null);
        }
        if (value instanceof String){
            jsonArray.put((String) resolveExpression((String) value));
        }
        else if (value instanceof Integer)
            jsonArray.put((Integer) value);
        else if (value instanceof Long)
            jsonArray.put((Long) value);
        else if (value instanceof Double)
            jsonArray.put((Double) value);
        else if (value instanceof Boolean)
            jsonArray.put((Boolean) value);
        else if (value instanceof JSONObject)
            jsonArray.put(reconstruct((JSONObject) value, resolve));
        else if (value instanceof JSONArray)
            jsonArray.put(reconstruct((JSONArray) value, resolve));
        else
            throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
    }

}
