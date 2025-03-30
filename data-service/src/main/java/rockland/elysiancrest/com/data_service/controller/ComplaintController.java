package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.ComplaintDto;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import com.commonlibrary.contract.v1.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rockland.elysiancrest.com.data_service.repo.UserRepo;
import rockland.elysiancrest.com.data_service.service.ComplaintService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/complaints")
@CrossOrigin
public class ComplaintController extends AbstractCrudController<ComplaintDto, Long, ComplaintService>{
//    private final UserRepo userRepo;

    protected ComplaintController(ComplaintService service, UserRepo userRepo) {
        super(service);
//        this.userRepo = userRepo;
    }

    @GetMapping("/allComplaints")
    public ResponseEntity<Response<Page<ComplaintDto>>> findAllComplaints(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "orderId", required = false) Long orderId){

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ComplaintDto> complaints = service.findAllComplaints(pageRequest, userId, orderId);

        // Explicitly verify each ComplaintDto includes the temporary link
        complaints.forEach(complaint -> {
            if (complaint.getAttachmentPath() != null) {
                String blobName = complaint.getAttachmentPath().substring(complaint.getAttachmentPath().lastIndexOf("/") + 1);
                String temporaryLink = service.generateTemporaryUrl(blobName, 60); // Valid for 1 hours
                complaint.setTemporaryLink(temporaryLink);
            }
        });

        return  ResponseEntity.ok(Response.<Page<ComplaintDto>>builder()
               .code(HttpStatus.OK.value())
               .status(Status.SUCCESS)
               .data(complaints)
               .build());
    }

    @PostMapping("/createComplaint")
    public ComplaintDto createComplaint(
            @ModelAttribute ComplaintDto complaintDto,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment){
        return service.createComplaint(complaintDto, attachment);
    }

    @PostMapping("/{id}/updateStatus")
    public ResponseEntity<Response<ComplaintDto>> updateStatusAndComment(
            @PathVariable("id") Long id,
            @RequestParam String status,
            @RequestParam(name = "userType", required = true) String userType,
            @RequestParam(name = "adminComment", required = false) String adminComment,
            @RequestParam(name = "userComment", required = false) String userComment) {

        // Call the service to update status and append both comments
        ComplaintDto updatedComplaint = service.updateStatusAndComment(id, status, adminComment, userComment, userType);

        // Return the response with the updated complaint DTO
        return ResponseEntity.ok(Response.<ComplaintDto>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(updatedComplaint)
                .build());
    }


    @PostMapping("/{id}/closeTicket")
    public ResponseEntity<Response<ComplaintDto>> updateCloseTicketStatus(
            @PathVariable("id") Long id,
            @RequestParam String status){

        ComplaintDto updatedComplaint = service.closeTicket(id, status);

        return ResponseEntity.ok(Response.<ComplaintDto>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(updatedComplaint)
                .build());

    }



}
