package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import modo.domain.dto.books.BooksResponseDto;
import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.entity.Books;
import modo.domain.entity.Users;
import modo.repository.BooksRepository;
import modo.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class BooksService {
    private final BooksRepository booksRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public BooksResponseDto save(BooksSaveRequestDto booksSaveRequestDto) {
        Books books = booksSaveRequestDto.toEntity();
        Users users = usersRepository.findById(booksSaveRequestDto.getUsersId())
                .orElseThrow(() -> new IllegalArgumentException("Users is not exist : " + booksSaveRequestDto.getUsersId()));

        books.setOwner(users);
        users.addBooks(books);

        booksRepository.save(books);
        return new BooksResponseDto(books);
    }
}
