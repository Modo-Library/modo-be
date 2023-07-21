package modo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Log4j2
public class BooksController extends BaseController {
    private final S3Service s3Service;

    @PostMapping("/api/v1/books/{keyName}")
    public ResponseEntity<?> createPreUrl(@PathVariable String keyName) throws IOException {
        log.info("createPreUrl in BooksController is Called!");
        return sendResponse(s3Service.createPreUrl(keyName));
    }

}
