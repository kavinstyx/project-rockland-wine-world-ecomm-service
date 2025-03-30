package rockland.elysiancrest.com.data_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.commonlibrary.contract.v1.Error;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;

import java.sql.SQLException;

@Slf4j
public class AbstractController {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Response<Error>> exceptionHandler(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.badRequest().body(Response.<Error>builder().data(Error.builder()
                .error(exception.getLocalizedMessage())
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .build()).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).status(Status.ERROR).build());
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<Response<Error>> exceptionHandler(SQLException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.badRequest().body(Response.<Error>builder().data(Error.builder()
                .error(exception.getLocalizedMessage())
                .code(String.valueOf(exception.getErrorCode()))
                .build()).code(HttpStatus.BAD_REQUEST.value()).status(Status.ERROR).build());
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Response<Error>> exceptionHandler(ResourceNotFoundException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.badRequest().body(Response.<Error>builder().data(Error.builder()
                .error(exception.getLocalizedMessage())
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .build()).code(HttpStatus.BAD_REQUEST.value()).status(Status.ERROR).build());
    }

}
