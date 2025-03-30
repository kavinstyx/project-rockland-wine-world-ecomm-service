package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.ComplaintDto;
import com.commonlibrary.contract.v1.ComplaintLogDto;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.entity.Complaint;
import rockland.elysiancrest.com.data_service.entity.ComplaintLog;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.repo.ComplaintLogRepo;
import rockland.elysiancrest.com.data_service.repo.ComplaintRepo;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.repo.UserRepo;
import rockland.elysiancrest.com.data_service.service.BlobStorageService;
import rockland.elysiancrest.com.data_service.service.ComplaintService;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.util.AuthenticatedUserUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComplaintServiceImpl extends CrudServiceImpl<Complaint, Long, ComplaintRepo, ComplaintDto> implements ComplaintService {

    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final BlobStorageService blobStorageService;
    private final EmailService emailService;
    private final ComplaintLogRepo complaintLogRepo;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public ComplaintServiceImpl(ComplaintRepo repository, ModelMapper modelMapper, UserRepo userRepo, OrderRepo orderRepo, BlobStorageService blobStorageService, EmailService emailService, ComplaintLogRepo complaintLogRepo, AuthenticatedUserUtil authenticatedUserUtil) {
        super(repository, modelMapper);
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.blobStorageService = blobStorageService;
        this.emailService = emailService;
        this.complaintLogRepo = complaintLogRepo;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @Override
    public void createComplaintLog(Long complaintId, String username, String message, String role) {
        Complaint complaint = repository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
                
        ComplaintLog log = new ComplaintLog();
        log.setMessage(message);
        log.setRole(role);
        log.setUsername(username);
        log.setStatus(complaint.getStatus());  // Set the current complaint status
        log.setComplaint(complaint);
        complaintLogRepo.save(log);
    }

    @Transactional
    public ComplaintDto createComplaint(ComplaintDto dto, MultipartFile attachment) {
        Complaint entity = convertToEntity(dto);

        ZonedDateTime nowInColombo = ZonedDateTime.now(ZoneId.of("Asia/Colombo"));
        entity.setCreatedAt(nowInColombo.toLocalDateTime());

        //set user id manually
        if (dto.getUserId() != null) {
            entity.setUser(userRepo.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User with id: " + dto.getUserId() + " does not exist.")));
        }
        //set order id manually
        if (dto.getOrderId() != null) {
            entity.setOrder(orderRepo.findById(dto.getOrderId()).orElseThrow(() -> new RuntimeException("Order with id: " + dto.getOrderId() + " does not exist.")));
        }

        //handle file upload and generate temporary link
        if (attachment != null && !attachment.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "-" + attachment.getOriginalFilename();
                String fileUrl = blobStorageService.uploadFile(fileName, attachment.getInputStream(), attachment.getSize());
                entity.setAttachmentPath(fileUrl);

                //generate the temporary link for the uploded file
                String temporaryLink = blobStorageService.generateTemporaryUrl(fileName, 60); //temporary URL valid for 10 hours
                dto.setTemporaryLink(temporaryLink);
            } catch (Exception e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }
        }

        Complaint savedEntity = repository.save(entity);
        // Create complaint log for user message
        createComplaintLog(savedEntity.getId(), dto.getUserName(), dto.getMessage(), "USER");

        dto.setId(savedEntity.getId());
        dto.setAttachmentPath(savedEntity.getAttachmentPath());

        if (dto.getUserId() != null) {
            Optional<User> user = userRepo.findById(dto.getUserId());
            if (user.isPresent()) {
                User userEntity = user.get();

                Context context = new Context();
                context.setVariable("username", userEntity.getName() != null ? userEntity.getName() : "Sir/Ma'am");
                context.setVariable("complaintId", savedEntity.getId());
                context.setVariable("complaintSubject", savedEntity.getSubject());
                context.setVariable("complaintStatus", savedEntity.getStatus());
                context.setVariable("complaintMessage", savedEntity.getMessage());
                context.setVariable("complaintType", savedEntity.getInquiryType());

                try {
                    emailService.sendComplaintMail(userEntity.getEmail(), "Wine World Complaint management",
                            "complaint_request.html", context);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }



        return dto;
    }

    @Transactional
    @Override
    public ComplaintDto updateStatusAndComment(Long complaintId, String status, String adminComment, String userComment, String userType) {
        // Fetch the complaint
        Complaint complaint = repository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint with id: " + complaintId + " does not exist."));

        // Update status
        complaint.setStatus(status);

        // Append admin comment if provided
        if (adminComment != null && !adminComment.isEmpty()) {
            complaint.setAdminComment(adminComment);
        }

        // Append user comment if provided
        if (userComment != null && !userComment.isEmpty()) {
            complaint.setMessage(userComment);
        }


        Complaint updatedComplaint = repository.save(complaint);

        // Save the updated complaint (if applicable)
        String currentUsername = authenticatedUserUtil.getCurrentUsername();
        if ("ADMIN".equals(userType)) {
            createComplaintLog(complaintId, currentUsername, adminComment, "ADMIN");
        }
        if ("USER".equals(userType)) {
            createComplaintLog(complaintId, currentUsername, userComment, "USER");
        }

        if (updatedComplaint.getUser() != null) {
            User user = updatedComplaint.getUser();

            Context context = new Context();
            context.setVariable("username", user.getName() != null ? user.getName() : "Sir/Ma'am");
            context.setVariable("complaintId", updatedComplaint.getId());
            context.setVariable("updatedStatus", updatedComplaint.getStatus());
            context.setVariable("complaintSubject", updatedComplaint.getSubject());
            context.setVariable("orderId",
                    updatedComplaint.getOrder() != null ? updatedComplaint.getOrder().getId() : "");
            context.setVariable("adminComment", updatedComplaint.getAdminComment());
            context.setVariable("complaintMessage", updatedComplaint.getMessage());
            context.setVariable("complaintType", updatedComplaint.getInquiryType());

            try {
                emailService.sendComplaintMail(user.getEmail(), "Wine World Complaint management",
                        "complaint_status.html", context);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return convertToDto(updatedComplaint);
    }

    @Override
    public ComplaintDto closeTicket(Long complaintId, String status) {
        //fetch the complaint
        Complaint complaint = repository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint with id: " + complaintId + " does not exist."));
        //update status
        complaint.setStatus(status);

        //save the updated complaint
        Complaint updatedComplaint = repository.save(complaint);

        if (updatedComplaint.getUser() != null) {
            User user = updatedComplaint.getUser();

            Context context = new Context();
            context.setVariable("username", user.getName() != null ? user.getName() : "Sir/Ma'am");
            context.setVariable("complaintId", updatedComplaint.getId());
            context.setVariable("updatedStatus", updatedComplaint.getStatus());
            context.setVariable("adminComment", updatedComplaint.getAdminComment());

            try {
                emailService.sendComplaintMail(user.getEmail(), "Wine World Complaint management",
                        "complaint_status.html", context);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        //convert to dto and return
        return convertToDto(updatedComplaint);
    }

    @Override
    public String generateTemporaryUrl(String blobName, int expiryTimeInMinutes) {
        return blobStorageService.generateTemporaryUrl(blobName, expiryTimeInMinutes);
    }

    @Override
    @Transactional
    public ComplaintDto update(Long id, ComplaintDto dto) {
        Complaint existingEntity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entity not found"));

        // Map the incoming DTO onto the existing entity for patching
        modelMapper.map(dto, existingEntity);

        //set user id manually
        if (dto.getUserId() != null) {
            existingEntity.setUser(userRepo.findById(dto.getUserId()).orElse(null));
        }
        //set order id manually
        if (dto.getOrderId() != null) {
            existingEntity.setOrder(orderRepo.findById(dto.getOrderId()).orElse(null));
        }

        Complaint updatedEntity = repository.save(existingEntity);
        return convertToDto(updatedEntity);
    }

    @Override
    protected ComplaintDto convertToDto(Complaint entity) {
        ComplaintDto dto = super.convertToDto(entity);

        //set the formatted date and time
        dto.setCreatedAt(entity.getCreatedAt());
        //set user id manually
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getName());
        }
        //set order id manually
        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }

        //generate the temporary link for attachment
        if (entity.getAttachmentPath() != null) {
            String blobName = entity.getAttachmentPath().substring(entity.getAttachmentPath().lastIndexOf("/") + 1);
            String temporaryLink = blobStorageService.generateTemporaryUrl(blobName, 60);
            dto.setTemporaryLink(temporaryLink); //valid for 10 hours
        }

        // Convert and set complaint logs
        if (entity.getComplaintLogs() != null) {
            dto.setComplaintLogs(entity.getComplaintLogs().stream()
                .map(complaintLog ->
                     modelMapper.map(complaintLog, ComplaintLogDto.class)
                )
                .sorted(Comparator.comparing(ComplaintLogDto::getCreatedAt).reversed())
                .toList());
        }

        return dto;
    }

    @Override
    @Transactional
    public Page<ComplaintDto> findAllComplaints(PageRequest pageRequest, Long userId, Long orderId) {
        List<Complaint> allComplaints = repository.findAll();

        // Apply filters if userId or orderId is provided
        List<Complaint> filteredComplaints = allComplaints.stream()
                .filter(complaint -> (userId == null || (complaint.getUser() != null && complaint.getUser().getId().equals(userId))))
                .filter(complaint -> (orderId == null || (complaint.getOrder() != null && complaint.getOrder().getId().equals(orderId))))
                .sorted(Comparator.comparing(Complaint::getId).reversed())
                .toList();

        // Convert to DTO and apply pagination
        List<ComplaintDto> complaintDtos = filteredComplaints.stream()
                .map(this::convertToDto)
                .toList();

        // Return a paginated result
        int start = Math.min((int) pageRequest.getOffset(), complaintDtos.size());
        int end = Math.min(start + pageRequest.getPageSize(), complaintDtos.size());

        return new PageImpl<>(complaintDtos.subList(start, end), pageRequest, complaintDtos.size());
    }

    @Override
    @Transactional
    public ComplaintDto findById(Long id) {
        Complaint complaint = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint with id: " + id + " does not exist."));

        return convertToDto(complaint);
    }

    @Override
    protected Complaint convertToEntity(ComplaintDto dto) {
        Complaint entity = super.convertToEntity(dto);

        //set user id manually
        if (dto.getUserId() != null) {
            entity.setUser(userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User with id: " + dto.getUserId() + " does not exist.")));
        }

        //set order id manually
        if (dto.getOrderId() != null) {
            entity.setOrder(orderRepo.findById(dto.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order with id: " + dto.getOrderId() + " does not exist.")));
        }

        //set

        return entity;
    }
}
