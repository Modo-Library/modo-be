package modo.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.ErrorJson;
import modo.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.ACCEPTED, response, e.getMessage(), ErrorCode.UnknownException);
        }
    }

    public void setErrorResponse(HttpStatus httpStatus, HttpServletResponse response, String message, ErrorCode errorCode) {
        ErrorJson errorJson = ErrorJson.builder()
                .message(message)
                .errorCode(errorCode.getErrorCode())
                .build();

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        try {
            String json = errorJson.convertToJson();
            response.getWriter().write(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
