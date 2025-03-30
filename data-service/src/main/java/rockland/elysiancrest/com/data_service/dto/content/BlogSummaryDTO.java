package rockland.elysiancrest.com.data_service.dto.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogSummaryDTO {
    private Long id;
    private String title;
    private String slug;
    private String imageUrl;
    private Boolean published;
    private String createdOn;
    private String updatedAt;
}
