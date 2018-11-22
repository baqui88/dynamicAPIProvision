package csc.response.expression;

public class ExpressionChecker {

    public static boolean isPlaceholderExpression(String expression){
        return PlaceholderExpression.match(expression.trim());
    }

    public static boolean isCallbackExpression(String expression){
        return CallbackExpression.match(expression.trim());
    }

    public static boolean isGetterExpression(String expression){
        return GetterExpression.match(expression.trim());
    }

    public static boolean isMappingExpression(String key, String value){
        return MappingExpression.match(key, value);
    }


}
