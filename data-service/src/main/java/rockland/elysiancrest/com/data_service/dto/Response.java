package rockland.elysiancrest.com.data_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> {
    int code;
    Status status;
    T data;
    private String message;

}
