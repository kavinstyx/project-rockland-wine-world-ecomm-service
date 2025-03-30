package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.content.BlogSummaryDTO;
import rockland.elysiancrest.com.data_service.dto.content.BlogDTO;

public interface BlogService extends CrudService<BlogSummaryDTO, Long> {
    BlogDTO findBlogBySlug(String slug);
    BlogDTO updateContent(Long id, BlogDTO blogDTO);
    BlogDTO createContent(BlogDTO blogDTO);
}
