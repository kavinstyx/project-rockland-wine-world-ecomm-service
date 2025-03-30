package rockland.elysiancrest.com.data_service.dto;

import lombok.Getter;

@Getter
public enum Status {
    SUCCESS("success"),
    ERROR("error"),
    WARNING("warning");

    final String status;

    Status(String status)
    {
        this.status = status;
    }

}