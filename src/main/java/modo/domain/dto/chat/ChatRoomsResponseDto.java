package modo.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modo.domain.entity.ChatRooms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class ChatRoomsResponseDto {
    private Long chatRoomsId;
    private List<String> usersIdList = new ArrayList<>();
    private LocalDateTime timeStamp;
    private String imgUrl;

    @Builder
    public ChatRoomsResponseDto(ChatRooms chatRooms) {
        this.chatRoomsId = chatRooms.getChatRoomsId();
        this.timeStamp = chatRooms.getTimeStamp();
        this.imgUrl = chatRooms.getImgUrl();
        this.usersIdList = chatRooms.getUsersList().stream()
                .map(each -> each.getUsersId())
                .collect(Collectors.toList());
    }
}
