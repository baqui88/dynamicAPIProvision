package csc.response.resolver;

import csc.response.expression.PlaceholderExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderResolver {
    @Autowired
    Environment env;
    public String resolve(String expression){
        PlaceholderExpression pe = new PlaceholderExpression(expression);
        return env.getProperty(pe.getPropertyName());
    }
}
