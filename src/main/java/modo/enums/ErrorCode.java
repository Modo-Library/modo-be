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

    // Auth ErrorCode
    ExpiredJwtException(2000),
    ReIssueBeforeAccessTokenExpiredException(2001),
    TokenIsNullException(2002),
    SignatureException(2003),

    // FirebaseAuth ErrorCode
    FirebaseAuthException(3000),

    // Bags ErrorCode
    ReturnRequestUserMismatchException(4000),
    AlreadyRentedException(4001),

    UnknownException(5000),

    // Admin ErrorCode
    UsersIsAlreadyAdminException(6000),
    UsersIsNotAdminException(6001),
    CertificationSignatureException(6002),
    CertificationTimeExpireException(6003);

    private final int errorCode;
}
