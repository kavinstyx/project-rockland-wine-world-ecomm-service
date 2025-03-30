package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.content.BlogDTO;
import rockland.elysiancrest.com.data_service.dto.content.BlogSummaryDTO;
import rockland.elysiancrest.com.data_service.service.BlogService;

@Slf4j
@RestController
@RequestMapping("api/blog")
@CrossOrigin
public class BlogController extends AbstractCrudController<BlogSummaryDTO, Long, BlogService> {
    protected BlogController(BlogService service) {
        super(service);
    }

    @GetMapping("/view/{slug}")
    @Operation(summary = "Get blog by slug")
    @ApiResponse(responseCode = "200", description = "Blog fetched successfully")
    @ApiResponse(responseCode = "404", description = "Blog not found")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable String slug) {
        try {
            BlogDTO blog = service.findBlogBySlug(slug);
            if (blog == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(blog);
        } catch (Exception e) {
            log.error("Error fetching blog with slug: " + slug, e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/content/{id}")
    @Operation(summary = "Update blog")
    @ApiResponse(responseCode = "200", description = "Blog updated successfully")
    @ApiResponse(responseCode = "404", description = "Blog not found")
    public ResponseEntity<Response<BlogDTO>> updateContentResource(@PathVariable Long id, @RequestBody BlogDTO blogDTO) {
        try {
            BlogDTO updatedBlog = service.updateContent(id, blogDTO);
            return ResponseEntity.ok(Response.<BlogDTO>builder()
                    .data(updatedBlog)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error updating blog with id: " + id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating blog with id: " + id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/content")
    @Operation(summary = "Create blog")
    @ApiResponse(responseCode = "200", description = "Blog created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid blog data")
    public ResponseEntity<Response<BlogDTO>> createContentResource(@RequestBody BlogDTO blogDTO) {
        try {
            BlogDTO createdBlog = service.createContent(blogDTO);
            return ResponseEntity.ok(Response.<BlogDTO>builder()
                    .data(createdBlog)
                    .build());
        } catch (RuntimeException e) {
            log.error("Error creating blog", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating blog", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
