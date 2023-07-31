package modo.exception.booksException;

public class UsersMismatchException extends RuntimeException {
    public UsersMismatchException(String messages) {
        super(messages);
    }
}
