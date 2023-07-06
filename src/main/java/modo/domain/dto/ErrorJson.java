package modo.domain.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import modo.enums.ErrorCode;

@Builder
@Getter
public class ErrorJson {
    public String message;
    public int errorCode;
    public String name;

    public String convertToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public ErrorJson(Exception e, ErrorCode errorCode) {
        this.message = e.getMessage();
        this.errorCode = errorCode.getErrorCode();
        this.name = errorCode.name();
    }
}