package modo.controller;

import lombok.RequiredArgsConstructor;
import modo.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BooksController extends BaseController {
    private final S3Service s3Service;

    @PostMapping("/api/v1/books/preUrl/{keyName}")
    public ResponseEntity<?> createPreUrl(@PathVariable String keyName) throws IOException {
        return sendResponse(s3Service.createPreUrl(keyName));
    }

}
