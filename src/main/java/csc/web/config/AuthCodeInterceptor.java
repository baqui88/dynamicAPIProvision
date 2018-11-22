package csc.web.config;

import csc.response.JSONUtil;
import csc.web.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthCodeInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        String userName = request.getHeader("userName");
        String profileId = request.getHeader("profileId");

        if (userName == null) {
            sendError(response);
            return false;
        }

        if (userName.equals("")){
            sendError(response);
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    public void sendError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        Message message = new Message(HttpStatus.UNAUTHORIZED.value(),
                "You don't have authorization to access this resource");
        response.getWriter().println(
                JSONUtil.writeObjectAsJsonString(message, true));
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception
    {

        super.postHandle(request, response, handler, modelAndView);
    }

    private static class Message{
        private int code;
        private String message;

        Message(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }
    }
}