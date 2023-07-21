package spring.web.exception;

public class GenreExistException extends RuntimeException {
    public GenreExistException(String message) {
        super(message);
    }
}
