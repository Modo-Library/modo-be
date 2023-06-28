package modo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum UsersHistoryAddRequestType {
    ADD_RENTING_COUNT,
    ADD_RETURNING_COUNT,
    ADD_BUY_COUNT,
    ADD_SELL_COUNT;

    @JsonCreator
    public static UsersHistoryAddRequestType from(String input) {
        return Stream.of(UsersHistoryAddRequestType.values())
                .filter(type -> type.toString().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

}
