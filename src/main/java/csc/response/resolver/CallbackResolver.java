package csc.response.resolver;

import csc.response.MainLoader;
import csc.response.StaticInfoResolver;
import csc.response.expression.CallbackExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

@Component
public class CallbackResolver
{
    @Autowired
    MainLoader loader;
    @Autowired
    ApplicationContext applicationContext;

    public Object resolve(String expression){
        CallbackExpression ce = new CallbackExpression(expression);
        String callbackRef = ce.getCallbackRef();
        try {
            JSONObject info = loader.getCallbackJSONObject().getJSONObject(callbackRef);
            String class_name = info.getString("class");
            String function_name = info.getString("function").trim();
            JSONArray args = info.getJSONArray("args");

            Object result;
            result = runMethod(class_name,function_name, args);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when trying to resolve callback " + callbackRef);
            return null;
        }
    }

    private Object runMethod(String class_name, String function_name, JSONArray args) throws Exception {
        Class c = Class.forName(class_name);

        // Get meta of arguments in method
        ArrayList<Class> classParameters = new ArrayList<Class>();
        ArrayList<Object> objectParameters = new ArrayList<Object>();

        for (int i = 0; i<= args.length() - 1; i++){
            JSONObject pair = (JSONObject) args.get(i);
            String classParameterName = (String) pair.names().get(0);
            Object value = pair.get(classParameterName);

            classParameters.add(Class.forName(classParameterName));
            objectParameters.add(value);
        }

        Method method = c.getMethod(function_name, classParameters.toArray(new Class[classParameters.size()]));

        // check if method is static
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (isStatic){
            return method.invoke(null, objectParameters.toArray(new Object[objectParameters.size()]));
        }
        // managed bean
        else if (c.isAnnotationPresent(Scope.class)){
            String scope = getScope(c);

            // find bean in factory
            if (scope.equals("") || scope.equals("singleton") || scope.equals("prototype")) {
                // get bean of a specific class in spring container
                Object o = applicationContext.getAutowireCapableBeanFactory().getBean(c);
                // run method instance
                return method.invoke(o, objectParameters.toArray(new Object[objectParameters.size()]));
            }
            // find bean in request attributes
            else if (scope.equals("request")){

                HttpServletRequest request =
                        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                String bean_name = StaticInfoResolver.getBeanName(c);
                Object o = request.getAttribute(bean_name);

                // run method instance
                return method.invoke(o, objectParameters.toArray(new Object[objectParameters.size()]));
            }
            else{
                throw new UnsupportedOperationException("Only support for singleton, prototype and request bean");
            }

        }
        // normal class
        else{
            throw new UnsupportedOperationException("Don't dupport callback for an  unmanaged class");
        }

    }


    public String getScope(Class aClass){
        Scope scope = (Scope) aClass.getAnnotation(Scope.class);
        return scope.value();
    }
}
