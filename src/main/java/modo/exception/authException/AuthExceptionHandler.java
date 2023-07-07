package modo.exception.authException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.log4j.Log4j2;
import modo.controller.BaseController;
import modo.domain.dto.ErrorJson;
import modo.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class AuthExceptionHandler extends BaseController {
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleTokenIsExpiredException(ExpiredJwtException e) {
        log.error("TokenIsExpiredException");
        ErrorJson errorJson = new ErrorJson(e, ErrorCode.ExpiredJwtException);
        return sendResponse(errorJson, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ReIssueBeforeAccessTokenExpiredException.class)
    public ResponseEntity<?> handleReIssueBeforeAccessTokenExpiredException(ReIssueBeforeAccessTokenExpiredException e) {
        log.error("ReIssueBeforeAccessTokenExpiredException");
        ErrorJson errorJson = new ErrorJson(e, ErrorCode.ReIssueBeforeAccessTokenExpiredException);
        return sendResponse(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenIsNullException.class)
    public ResponseEntity<?> handleTokenIsNullException(TokenIsNullException e) {
        log.error("TokenIsNullException");
        ErrorJson errorJson = new ErrorJson(e, ErrorCode.TokenIsNullException);
        return sendResponse(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        log.error("SignatureException");
        ErrorJson errorJson = new ErrorJson(e, ErrorCode.SignatureException);
        return sendResponse(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<?> handleRefreshTokenExpiredException(RefreshTokenExpiredException e) {
        log.error("RefreshTokenExpiredException");
        ErrorJson errorJson = new ErrorJson(e, ErrorCode.RefreshTokenExpiredException);
        return sendResponse(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(Exception e) {
        log.error("UnknownException");
        ErrorJson errorJson = new ErrorJson(e, ErrorCode.UnknownException);
        return sendResponse(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
