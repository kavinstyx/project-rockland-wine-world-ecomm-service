package rockland.elysiancrest.com.data_service.enums;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CsvHeader {
    String value();
}
