package csc.response.expression;

public class MappingExpression
{
    public static boolean match(String key, String value){
        return key.equals("*") && value.equals("*");
    }
}
