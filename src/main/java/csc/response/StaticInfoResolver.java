package csc.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class StaticInfoResolver {
    private static ApplicationContext context;

    private static String item_href;
    private static String base_href;

    @Autowired
    public StaticInfoResolver(ApplicationContext context){
        StaticInfoResolver.context = context;
    }

    private static String getHref(String uri) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String scheme = request.getScheme();             // http
        String serverName = request.getServerName();     // hostname.com
        int serverPort = request.getServerPort();        // portNumber
        String contextPath = request.getContextPath();   // /myapp

        // Reconstruct original requesting URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        url.append(":").append(serverPort);
        url.append(contextPath).append(uri);

        return url.toString();
    }

    public static String getItemHref(){
        if (item_href != null)
            return item_href;
        Environment env = context.getEnvironment();
        String item_uri = env.getProperty("CONSTANT.item_uri");
        item_href = getHref(item_uri);
        return item_href;
    }

    public static String getBaseHref(){
        if (base_href != null)
            return base_href;
        Environment env = context.getEnvironment();
        String base_uri = env.getProperty("CONSTANT.base_uri");
        base_href = getHref(base_uri);
        return base_href;
    }

    public  static String getBeanName(Class aClass){
        String bean_name = ((Component)aClass.getAnnotation(Component.class)).value();
        // lower case first character
        if (bean_name.equals("")){
            String[] vars = aClass.getName().split(".");
            bean_name = vars[vars.length - 1];

            char c[] = bean_name.toCharArray();
            c[0] = Character.toLowerCase(c[0]);
            bean_name = new String(c);
        }
        return bean_name;
    }

    public static Object getController(String name) throws JSONException {
        MainLoader loader = context.getBean(MainLoader.class);
        String[] con = name.split("\\.");
        return loader.getEndpointJSONObject().getJSONObject(name);
    }
}
