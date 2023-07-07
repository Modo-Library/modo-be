package modo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Users ErrorCode
    UsersIdAlreadyExistException(1000),
    UsersIdOrPasswordInvalidException(1001),
    DuplicateUserSaveException(1003),
    UsernameNotFoundException(1004),

    // Auth ErrorCode
    ExpiredJwtException(2000),
    ReIssueBeforeAccessTokenExpiredException(2001),
    TokenIsNullException(2002),
    SignatureException(2003),
    RefreshTokenExpiredException(2004),

    UnknownException(5000);

    private final int errorCode;
}
