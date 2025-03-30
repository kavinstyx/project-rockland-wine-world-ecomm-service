package rockland.elysiancrest.com.data_service.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.repo.BlogRepository;
import rockland.elysiancrest.com.data_service.service.BlogService;
import rockland.elysiancrest.com.data_service.entity.content.Blog;
import rockland.elysiancrest.com.data_service.dto.content.BlogDTO;
import rockland.elysiancrest.com.data_service.dto.content.BlogSummaryDTO;

@Service
@Transactional
public class BlogServiceImpl extends CrudServiceImpl<Blog, Long, BlogRepository, BlogSummaryDTO> implements BlogService {

    private final BlogRepository blogRepository;

    public BlogServiceImpl(BlogRepository repository, ModelMapper modelMapper) {
        super(repository, modelMapper);
        this.blogRepository = repository;
    }

    @Override
    public BlogDTO findBlogBySlug(String slug) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElse(null);
        if (blog == null) {
            return null;
        }
        return modelMapper.map(blog, BlogDTO.class);
    }
    
    @Override
    public BlogDTO updateContent(Long id, BlogDTO blogDTO) {
        Blog blog = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        
        blog.setTitle(blogDTO.getTitle());
        blog.setSlug(blogDTO.getSlug());
        blog.setContent(blogDTO.getContent());
        blog.setImageUrl(blogDTO.getImageUrl());
        blog.setPublished(blogDTO.getPublished());
        
        Blog updatedBlog = repository.save(blog);
        return modelMapper.map(updatedBlog, BlogDTO.class);
    }

    @Override
    public BlogDTO createContent(BlogDTO blogDTO) {
        Blog blog = new Blog();
        blog.setTitle(blogDTO.getTitle());
        blog.setSlug(blogDTO.getSlug());
        blog.setContent(blogDTO.getContent());
        blog.setImageUrl(blogDTO.getImageUrl());
        blog.setPublished(blogDTO.getPublished());
        
        Blog createdBlog = repository.save(blog);
        return modelMapper.map(createdBlog, BlogDTO.class);
    }
}
