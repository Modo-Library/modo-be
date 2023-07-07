package modo.exception.authException;

public class ReIssueBeforeAccessTokenExpiredException extends RuntimeException {
    public ReIssueBeforeAccessTokenExpiredException(String messages) {
        super(messages);
    }
}
