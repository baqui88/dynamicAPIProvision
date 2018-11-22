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

    public static boolean validate(String expression){
        if (isPlaceholderExpression(expression))
            return false;
        if (!isCallbackExpression(expression))
            return false;
        if(!isGetterExpression(expression))
            return false;
        return true;
    }


}
