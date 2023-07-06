package modo.auth;

import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.ErrorJson;
import modo.enums.ErrorCode;
import modo.exception.authException.TokenIsExpiredException;
import modo.exception.authException.TokenIsNullException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenIsExpiredException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e, ErrorCode.TokenIsExpiredException);
        } catch (TokenIsNullException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e, ErrorCode.TokenIsNullException);
        } catch (SignatureException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e, ErrorCode.SignatureException);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e, ErrorCode.UsernameNotFoundException);
        } catch (Exception e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e, ErrorCode.UnknownException);
        }
    }

    public void setErrorResponse(HttpStatus httpStatus, HttpServletResponse response, Exception e, ErrorCode errorCode) {

        ErrorJson errorJson = new ErrorJson(e, errorCode);

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        try {
            String json = errorJson.convertToJson();
            response.getWriter().write(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        log.warn("* Error raised! message : {}, errorCode : {}, name : {} *", e.getMessage(), errorCode.getErrorCode(), errorCode.name());
    }
}
