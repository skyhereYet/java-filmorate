package spring.web.exception;

public class UserExistException extends IllegalArgumentException {
    public UserExistException(String s) {
        super(s);
    }
}