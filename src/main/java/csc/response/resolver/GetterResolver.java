package csc.response.resolver;

import csc.response.MainLoader;
import csc.response.expression.GetterExpression;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class GetterResolver {
    @Value("${CONSTANT.dateFormat}")
    String default_date_format;

    @Autowired
    private MainLoader loader;

    public Object resolve(String expression, Object o){
        GetterExpression ge = new GetterExpression(expression);
        String attr_ref = ge.getAttributeRef();
        String attr_name;
        CSVRecord row = loader.getAttributeRefMap().get(attr_ref);
        // reference doesn't exists, then auto map
        if (row == null)
            attr_name = attr_ref;
        else
            attr_name = row.get("attribute").trim();

        // get method name
        if (ge.isGetter())
            attr_name = "get" + attr_name;

        Object value = runMethod(attr_name, o);
        if (value instanceof Date){
            String format = row.get("format");
            if (format == null)
                format = default_date_format;
            else if (format.equals("") || format.equals("NA")){
                format = default_date_format;
            }
            value = convertDateToString((Date) value, format);
        }
        return value;
    }

    public static Object runMethod(String methodName, Object o)
    {
        if (methodName == null)
            return null;
        Class c = o.getClass();
        // MZ: Find the correct method
        for (Method method : c.getMethods())
        {
            if ((method.getName().length() == methodName.length()))
            {
                if (method.getName().toLowerCase().endsWith(methodName.toLowerCase()))
                {
                    try {
                        return method.invoke(o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw  new RuntimeException();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
            }
        }
        return null;
    }

    public static String convertDateToString(Date date, String format){
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}
