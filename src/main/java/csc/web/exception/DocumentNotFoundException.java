package csc.web.exception;

public class DocumentNotFoundException extends RuntimeException {

    private String id;

    public DocumentNotFoundException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Document " + id + " not found";
    }
}
