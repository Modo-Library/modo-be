package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.auth.JwtTokenProvider;
import modo.domain.dto.books.BooksResponseDto;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.books.BooksUpdateRequestDto;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.domain.entity.Books;
import modo.domain.entity.Pictures;
import modo.domain.entity.Users;
import modo.exception.booksException.UsersMismatchException;
import modo.repository.BooksRepository;
import modo.repository.PicturesRepository;
import modo.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Service
public class BooksService {
    private final BooksRepository booksRepository;
    private final UsersRepository usersRepository;
    private final PicturesRepository picturesRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public BooksResponseDto save(BooksSaveRequestDto booksSaveRequestDto) {
        // Make books entity with booksSaveRequestDto
        Books books = booksSaveRequestDto.toEntity();
        // Find users : book's Owner
        Users users = usersRepository.findById(booksSaveRequestDto.getUsersId())
                .orElseThrow(() -> new IllegalArgumentException("Users is not exist : " + booksSaveRequestDto.getUsersId()));

        // Join books and users
        books.setOwner(users);
        users.addBooks(books);

        booksSaveRequestDto.getPicturesSaveRequestDtoList().stream()
                // Convert eachDto to Pictures entity
                .map(PicturesSaveRequestDto::toEntity)
                // Set the books reference in Pictures
                .peek(pictures -> pictures.setBooks(books))
                .forEach(pictures -> {
                    // Add Pictures to the picturesList in Books
                    books.getPicturesList().add(pictures);
                    // Save Pictures
                    picturesRepository.save(pictures);
                });

        // Save books
        booksRepository.save(books);
        return new BooksResponseDto(books);
    }

    @Transactional
    public BooksResponseDto update(BooksUpdateRequestDto requestDto) {
        Books target = findBooksInRepository(requestDto.getBooksId());
        target.update(requestDto);
        return new BooksResponseDto(target);
    }

    @Transactional
    public void delete(Long booksId, String accessToken) {
        String usersId = jwtTokenProvider.getUsersId(accessToken);
        Books target = findBooksInRepository(booksId);

        if (target.isNotOwnerId(usersId))
            throw new UsersMismatchException("Request user is not Book's owner");

        target.getOwner().removeBooks(target);
        booksRepository.deleteById(booksId);
    }

    private Books findBooksInRepository(Long booksId) {
        return booksRepository.findById(booksId).orElseThrow(
                () -> new IllegalArgumentException("Books with id : " + booksId + "is not exist!")
        );
    }
}
