package modo.exception.authException;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String messages) {
        super(messages);
    }
}
