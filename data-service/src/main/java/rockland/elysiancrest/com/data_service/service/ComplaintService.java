package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.ComplaintDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ComplaintService extends CrudService<ComplaintDto, Long>{
    Page<ComplaintDto> findAllComplaints(PageRequest pageRequest, Long userId, Long orderId);
    ComplaintDto createComplaint(ComplaintDto dto, MultipartFile attachment);

    @Transactional
    ComplaintDto updateStatusAndComment(Long complaintId, String status, String adminComment, String userComment, String userType);

    ComplaintDto closeTicket(Long complaintId, String status);
    String generateTemporaryUrl(String blobName, int expiryTimeInMinutes);
    void createComplaintLog(Long complaintId, String username, String message, String role);
}
