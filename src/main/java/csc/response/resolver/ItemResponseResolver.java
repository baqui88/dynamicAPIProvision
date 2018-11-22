package csc.response.resolver;

import csc.response.JSONUtil;
import csc.response.expression.ExpressionChecker;
import csc.response.expression.MappingExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@Scope(value = "request")
public class ItemResponseResolver extends GeneralResponseResolver
{
    @Override
    protected void readJSONToken(){
        super.token = JSONUtil.chain(super.loader.getEndpointJSONObject(), "tests", "_item");
    }

    private Object resolveExpression(String key, String expression) throws JSONException {
        if (ExpressionChecker.isMappingExpression(key, expression)){
            return mappingResolver.resolve(super.contextObject);
        }
        return super.resolveExpression(expression);
    }

    // create a new instance, which load dependency from exist bean
    public static ItemResponseResolver newInstance(GeneralResponseResolver fromObject){
        ItemResponseResolver r = new ItemResponseResolver();
        r.callbackResolver = fromObject.callbackResolver;
        r.placeholderResolver = fromObject.placeholderResolver;
        r.getterResolver = fromObject.getterResolver;
        r.mappingResolver = fromObject.mappingResolver;
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
                    if (ExpressionChecker.isMappingExpression(key, (String) value))
                    {
                        JSONObject mjo = mappingResolver.resolve(super.contextObject);
                        Iterator<String> it = mjo.keys();
                        while(it.hasNext()){
                            String display_name = it.next();
                            Object attr_value = mjo.get(display_name);
                            ret.put(display_name, attr_value);
                        }
                    }
                    else
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

}
