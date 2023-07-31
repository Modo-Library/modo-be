package modo.controller;

import lombok.RequiredArgsConstructor;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.enums.BooksStatus;
import modo.service.BooksService;
import modo.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BooksController extends BaseController {
    private final S3Service s3Service;
    private final BooksService booksService;

    @PostMapping("/api/v1/books/preUrl")
    public ResponseEntity<?> createPreUrl(@RequestParam String keyName) throws IOException {
        return sendResponse(s3Service.createPreUrl(keyName));
    }

    @PostMapping("/api/v1/books/save")
    public ResponseEntity<?> save(@RequestBody BooksSaveRequestDto requestDto) {
        return sendResponse(booksService.save(requestDto));
    }

    @PutMapping("/api/v1/books/update")
    public ResponseEntity<?> update(@RequestBody BooksUpdateRequestDto requestDto) {
        return sendResponse(booksService.update(requestDto));
    }

    @DeleteMapping("/api/v1/books/delete")
    public ResponseEntity<?> delete(@RequestParam(value = "booksId") Long booksId, @RequestHeader(value = "token") String token) {
        booksService.delete(booksId, token);
        return sendResponse();
    }

}
