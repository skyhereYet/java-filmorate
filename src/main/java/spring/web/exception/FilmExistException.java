package spring.web.exception;

public class FilmExistException extends RuntimeException {
    public FilmExistException(String s) {
        super(s);
    }
}