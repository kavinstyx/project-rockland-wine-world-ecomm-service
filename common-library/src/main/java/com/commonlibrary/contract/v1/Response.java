package com.commonlibrary.contract.v1;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class Response<T> {
    int code;
    Status status;
    T data;
    public String message;

    // Method to create a success response
    public static <T> Response<T> success(T data, String message) {
        return Response.<T>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(data)
                .message(message)
                .build();
    }

    // Method to create an error response
    public static <T> Response<T> error(String message) {
        return Response.<T>builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(Status.ERROR)
                .data(null) // No data for an error response
                .message(message)
                .build();
    }
}

