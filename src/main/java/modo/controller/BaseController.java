package modo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class BaseController {
    public ResponseEntity<?> sendResponse(Object data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return new ResponseEntity<Object>(data, headers, HttpStatus.OK);
    }

    public ResponseEntity<?> sendResponse(Object data, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return new ResponseEntity<Object>(data, headers, httpStatus);
    }

}