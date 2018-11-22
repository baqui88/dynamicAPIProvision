package csc.response.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallbackExpression implements Expression {
    // ${::callback] invoke a callback_ref, which is refered in config
    private static String pattern  = "^\\$\\{\\s*::(?<callback>[-a-zA-Z0-9_]+)\\s*}$";

    private String callback_ref;

    public static boolean match(String expression){
        return expression.matches(pattern);
    }

    public CallbackExpression(String expression){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(expression);
        m.matches();

        callback_ref = m.group("callback");
    }

    public String getCallbackRef() {
        return callback_ref;
    }

}
