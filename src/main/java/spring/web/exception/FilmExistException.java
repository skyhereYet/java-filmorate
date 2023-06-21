package spring.web.exception;

public class FilmExistException extends IllegalArgumentException {
    public FilmExistException(String s) {
        super(s);
    }
}