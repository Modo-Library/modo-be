package modo;

import modo.domain.dto.books.BooksSaveRequestDto;
import modo.domain.dto.pictures.PicturesSaveRequestDto;
import modo.domain.dto.users.Users.UsersSaveRequestDto;
import modo.enums.BooksStatus;

import java.util.List;

public class StaticResources {

    public static final String senderId = "senderId";
    public static final String senderNickname = "senderNickname";
    public static final double senderLatitude = 1.1;
    public static final double senderLongitude = 1.1;

    public static final String receiverId = "receiverId";
    public static final String receiverNickname = "senderNickname";
    public static final double receiverLatitude = 1.1;
    public static final double receiverLongitude = 1.1;

    public static final UsersSaveRequestDto senderSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(senderId)
            .nickname(senderNickname)
            .latitude(senderLatitude)
            .longitude(senderLongitude)
            .build();

    public static final UsersSaveRequestDto receiverSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId(receiverId)
            .nickname(receiverNickname)
            .latitude(receiverLatitude)
            .longitude(receiverLongitude)
            .build();

    public static final UsersSaveRequestDto thirdUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId("thirdUsers")
            .nickname("thirdUsers")
            .latitude(receiverLatitude)
            .longitude(receiverLongitude)
            .build();

    public static final UsersSaveRequestDto fourthUsersSaveRequestDto = UsersSaveRequestDto.builder()
            .usersId("fourthUsers")
            .nickname("fourthUsers")
            .latitude(receiverLatitude)
            .longitude(receiverLongitude)
            .build();

    public static final String testName = "스프링으로 하는 마이크로서비스 구축";
    public static final Long testPrice = 40000L;
    public static final BooksStatus testStatus = BooksStatus.AVAILABLE_RENT;
    public static final String testDescription = "완전 새 책";
    public static final String testImgUrl = "s3://testImgUrl.com";
    public static final String testFilename = "testFilename.jpg";

    static final PicturesSaveRequestDto requestDto1 = PicturesSaveRequestDto.builder()
            .imgUrl(testImgUrl + "1")
            .filename(testFilename + "1")
            .build();

    static final PicturesSaveRequestDto requestDto2 = PicturesSaveRequestDto.builder()
            .imgUrl(testImgUrl + "2")
            .filename(testFilename + "2")
            .build();

    static final List<PicturesSaveRequestDto> picturesSaveRequestDtoList = List.of(requestDto1, requestDto2);

    public static final BooksSaveRequestDto booksSaveRequestDto = BooksSaveRequestDto.builder()
            .name(testName)
            .price(testPrice)
            .status(testStatus.toString())
            .description(testDescription)
            .imgUrl(testImgUrl)
            .usersId(receiverId)
            .picturesSaveRequestDtoList(picturesSaveRequestDtoList)
            .build();

    public static final String testMessages = "testMessages";

}
