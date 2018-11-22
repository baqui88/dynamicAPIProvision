package csc.response.resolver;

import csc.response.JSONUtil;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
public class ItemResponseResolver extends GeneralResponseResolver
{
    @Override
    protected void readJSONToken(){
        super.token = JSONUtil.chain(super.loader.getEndpointJSONObject(), "tests", "_item");
    }
}
