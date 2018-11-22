package csc.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleConflict(DataIntegrityViolationException ex) {
        return getMessageView(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(DocumentNotFoundException.class)
    public ModelAndView handleDocumentNotFound(DocumentNotFoundException ex) {
        return getMessageView(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(DatabaseNotSupportedException.class)
    public ModelAndView handleDatabaseNotSupported(DatabaseNotSupportedException ex) {
        return getMessageView(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ExceptionHandler(UnAuthorizedException.class)
    public ModelAndView handleAuthorization(DatabaseNotSupportedException ex) {
        return getMessageView(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    private ModelAndView getMessageView(int code, String message) {
        ModelAndView mdv = new ModelAndView();
        mdv.setViewName("message");
        mdv.addObject("code", code);
        mdv.addObject("errorMessage", message);
        return mdv;
    }
}