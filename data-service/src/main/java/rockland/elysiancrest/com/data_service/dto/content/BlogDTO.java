package rockland.elysiancrest.com.data_service.dto.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDTO extends BlogSummaryDTO {
    private String content;
}
