package csc.response.resolver;


import csc.response.JSONUtil;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("collectionResolver")
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CollectionResponseResolver extends GeneralResponseResolver
{
    // List<Object> contextObject;

    @Override
    protected void readJSONToken() {
        super.token = JSONUtil.chain(loader.getEndpointJSONObject(), "tests", "_collection");
    }

    @Override
    public void putContextObject(Object o){
        if (o instanceof List)
            super.contextObject = (List) o;
        else{
            throw new RuntimeException("Context object should be a list of items !!");
        }
    }

    public JSONArray fetchCollection(String format) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        GeneralResponseResolver item_resolver = newInstance(this);
        item_resolver.setJSONOToken(new JSONObject(format));

        for (Object item : (List) contextObject){
            item_resolver.putContextObject(item);
            Object jo_item = item_resolver.responseJSONToken();
            jsonArray.put(jo_item);
        }
        return jsonArray;
    }
}
