package csc.response.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Given a context object, get information about it <br/>
 * There are two meta : @ and @@ <br/>
 *  # ${ @<>attribute } : get an attribute of object via getter method <br/>
 *  # ${ @@<>info } : get result by call directly method <br/>
 *  For example , which context object is a list
 *  ${ @@size } will call list.size() method
 *
 *  Note that : ${@@ attr} is different from ${@ @attr}
 */
public class GetterExpression implements Expression {
    private static String pattern = "^\\$\\{\\s*@(?<meta>@?)(?<ref>.+)}$";
    private String attribute_ref;
    private boolean is_getter = true;

    public static boolean match(String expression){
        return expression.matches(pattern);
    }

    public GetterExpression(String expression)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(expression);
        m.matches();
        is_getter = m.group("meta").equals("");
        attribute_ref = m.group("ref").trim(); // remove space
    }

    public String getAttributeRef() {
        return attribute_ref;
    }

    public boolean isGetter(){
        return is_getter;
    }
}
