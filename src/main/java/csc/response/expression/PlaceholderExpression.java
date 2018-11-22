package csc.response.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderExpression implements Expression {
    // inspired from standard placeholder of Java, embed value of a property
    private static String pattern = "^\\$\\{\\s*(?<resourceProperty>[^@.:].*)}$";

    private String propertyName;


    public static boolean match(String expression){
        return expression.matches(pattern);
    }

    public PlaceholderExpression(String expression){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(expression);
        m.matches();
        propertyName = m.group("resourceProperty").trim();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
