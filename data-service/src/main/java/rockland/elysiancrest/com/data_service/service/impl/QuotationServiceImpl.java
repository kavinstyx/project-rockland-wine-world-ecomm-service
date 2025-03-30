package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Product;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import rockland.elysiancrest.com.data_service.dto.QuotationDTO;
import rockland.elysiancrest.com.data_service.dto.QuotationItemDTO;
import rockland.elysiancrest.com.data_service.entity.quotation.QuotationItem;
import rockland.elysiancrest.com.data_service.entity.quotation.QuotationMaster;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.repo.QuotationMasterRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.PdfGenerationService;
import rockland.elysiancrest.com.data_service.service.ProductMasterService;
import rockland.elysiancrest.com.data_service.service.QuotationService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuotationServiceImpl extends BaseServiceImpl<QuotationMaster, QuotationDTO> implements QuotationService {

    private final QuotationMasterRepo quotationMasterRepo;
    private final EmailService emailService;
    private final ProductMasterService productMasterService;
    private final PdfGenerationService pdfGenerationService;
    private final SpringTemplateEngine templateEngine;

    public QuotationServiceImpl(JpaRepository<QuotationMaster, Long> repository, QuotationMasterRepo quotationMasterRepo, EmailService emailService, ProductMasterService productMasterService, PdfGenerationService pdfGenerationService, @Qualifier("templateEngine") SpringTemplateEngine templateEngine) {
        super(repository);
        this.quotationMasterRepo = quotationMasterRepo;
        this.emailService = emailService;
        this.productMasterService = productMasterService;
        this.pdfGenerationService = pdfGenerationService;
        this.templateEngine = templateEngine;
    }


    @Override
    public QuotationDTO create(QuotationDTO quotationDTO) {
        QuotationMaster quotationMaster = convertToEntity(quotationDTO);

        // Set created time
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Colombo"));
        quotationMaster.setCreatedTime(zonedDateTime.toLocalDateTime());
        quotationMaster.setPaymentNote(quotationDTO.getPaymentNote());
        quotationMaster.setPaymentMethod(quotationDTO.getPaymentMethod());
        quotationMaster.setValidityPeriod(quotationDTO.getValidityPeriod());
        quotationMaster.setSalesRep(quotationDTO.getSalesRep());

        // Calculate total price
        quotationMaster.calculateTotalPrice();

        // Update volume for each item if items exist
        if (quotationDTO.getItems() != null) {
            quotationDTO.getItems().forEach(quotationItemDTO -> {
                Product product = productMasterService.findById(quotationItemDTO.getProductId());
                if (product != null) {
                    quotationItemDTO.setVolume(product.getVolume());
                }
            });
        }

        // Save quotation and map back to DTO
        QuotationMaster savedQuotation = quotationMasterRepo.save(quotationMaster);
        return mapToDto(savedQuotation);
    }


    @Override
    public QuotationDTO update(Long id, QuotationDTO quotationDTO) {
        // Find the existing entity
        QuotationMaster existingQuotation = quotationMasterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found with ID: " + id));

        // Update fields
        existingQuotation.setCustomerName(quotationDTO.getCustomerName());
        existingQuotation.setCustomerId(quotationDTO.getCustomerId());
        existingQuotation.setStatus(quotationDTO.getStatus());
        existingQuotation.setEmail(quotationDTO.getEmail());
        existingQuotation.setAddress(quotationDTO.getAddress());
        existingQuotation.setSalesRep(quotationDTO.getSalesRep());
        existingQuotation.setPaymentNote(quotationDTO.getPaymentNote());
        existingQuotation.setValidityPeriod(quotationDTO.getValidityPeriod());

        // Clear existing items and add updated ones
        existingQuotation.getItems().clear();
        List<QuotationItem> updatedItems = quotationDTO.getItems().stream().map(itemDTO -> {
            QuotationItem item = new QuotationItem();
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setQuantity(itemDTO.getQuantity());
            item.setPricePerUnit(itemDTO.getPricePerUnit());
            item.setDiscountedPrice(itemDTO.getDiscountedPrice());

            // Fetch volume from ProductMaster and set it
            Product product = productMasterService.findById(itemDTO.getProductId());
            if (product != null) {
                item.setVolume(product.getVolume());
            }

            return item;
        }).toList();
        updatedItems.forEach(existingQuotation::addItem);

        // Recalculate the total price
        existingQuotation.calculateTotalPrice();

        // Save the updated entity
        QuotationMaster updatedQuotation = quotationMasterRepo.save(existingQuotation);

        // Convert back to DTO and return
        return mapToDto(updatedQuotation);
    }


    @Override
    public String generateQuotationEmailHtml(Long quotationId) {
        // Fetch the quotation from the database by quotationId
        QuotationMaster quotation = quotationMasterRepo.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        // Creating the Thymeleaf context and adding variables
        Context context = new Context();

        // Basic details
        context.setVariable("companyName", "Wine World");
        context.setVariable("companyAddress", "102, Kumar Ratnam Road, Col - 02");
        context.setVariable("website", "www.wineworld.lk");
        context.setVariable("hotline", "011 2 431 991");

        // Quotation details
        context.setVariable("quotationNo", quotation.getId());
        context.setVariable("quotationDate", formatDate(quotation.getCreatedTime()));  // Formatting date
        context.setVariable("customerId", quotation.getCustomerId());
        context.setVariable("customerName", quotation.getCustomerName());
        context.setVariable("billingAddress", quotation.getAddress());
        context.setVariable("userEmail", quotation.getEmail());
        context.setVariable("paymentMethod", quotation.getPaymentMethod());

        // Additional details for email
        context.setVariable("currency", "LKR"); // Assuming LKR as currency or use another dynamic field

        // Order items and total price
        context.setVariable("orderItems", quotation.getItems());  // Ensure this returns a List, not a Stream
        context.setVariable("totalQuotationAmount", quotation.getTotalPrice());

        try {
            emailService.sendQuotationEmail(quotation.getEmail(),"Quotation","quotation_template", context );
            return "Email Sent Successfully to " + quotation.getEmail();
        } catch (MessagingException e) {

            log.error(e.getMessage(),e);
            return "Email Sent Failed";
        }

    }


    // Helper method to format date in a desired format
    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    private QuotationMaster convertToEntity(QuotationDTO dto) {
        QuotationMaster master = new QuotationMaster();
        master.setCustomerName(dto.getCustomerName());
        master.setCustomerId(dto.getCustomerId());
        master.setStatus(dto.getStatus());
        master.setEmail(dto.getEmail()); // Added
        master.setAddress(dto.getAddress()); // Added

        List<QuotationItem> items = dto.getItems().stream().map(itemDTO -> {
            QuotationItem item = new QuotationItem();
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setQuantity(itemDTO.getQuantity());
            item.setPricePerUnit(itemDTO.getPricePerUnit());
            item.setDiscountedPrice(itemDTO.getDiscountedPrice());
            return item;
        }).toList();

        items.forEach(master::addItem);
        return master;
    }


    @Override
    public Page<QuotationDTO> getAllQuotations(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create a PageRequest object with the given parameters
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Fetch paginated data and map it to QuotationDTO
        return quotationMasterRepo.findAll(pageRequest)
                .map(this::mapToDto);
    }

    private QuotationDTO mapToDto(QuotationMaster quotation) {
        QuotationDTO dto = new QuotationDTO();
        dto.setId(quotation.getId());
        dto.setCustomerName(quotation.getCustomerName());
        dto.setCustomerId(quotation.getCustomerId());
        dto.setStatus(quotation.getStatus());
        dto.setEmail(quotation.getEmail());
        dto.setAddress(quotation.getAddress());
        dto.setTotalPrice(quotation.getTotalPrice());
        dto.setCreatedTime(quotation.getCreatedTime());
        dto.setSalesRep(quotation.getSalesRep());
        dto.setPaymentNote(quotation.getPaymentNote());
        dto.setValidityPeriod(quotation.getValidityPeriod());

        // Map items if present
        if (quotation.getItems() != null) {
            dto.setItems(quotation.getItems().stream()
                    .map(this::mapItemToDto) // Assuming a method to map items
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private QuotationItemDTO mapItemToDto(QuotationItem item) {
        QuotationItemDTO dto = new QuotationItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPricePerUnit(item.getPricePerUnit());
        dto.setDiscountedPrice(item.getDiscountedPrice());
        dto.setTotalPriceAfterDiscount(item.getTotalPriceAfterDiscount());
        return dto;
    }


    public QuotationDTO getQuotationById(Long quotationId) {
        // Fetch the quotation entity from the repository
        QuotationMaster quotation = quotationMasterRepo.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found with ID: " + quotationId));

        // Map the entity to a DTO
        return mapToDto(quotation);
    }

    @Override
    public byte[] generateQuotationPdf(Long quotationId) {
        try {
            // Fetch the quotation from the database by quotationId
            QuotationMaster quotation = quotationMasterRepo.findById(quotationId)
                    .orElseThrow(() -> new RuntimeException("Quotation not found"));

            // Creating the Thymeleaf context and adding variables
            Context context = new Context();

            // Basic details
            context.setVariable("companyName", "Wine World");
            context.setVariable("companyAddress", "102, Kumar Ratnam Road, Col - 02");
            context.setVariable("website", "www.wineworld.lk");
            context.setVariable("hotline", "011 2 431 991");

            // Quotation details
            context.setVariable("quotationNo", quotation.getId());
            context.setVariable("quotationDate", formatDate(quotation.getCreatedTime()));  // Formatting date
            context.setVariable("customerId", quotation.getCustomerId());
            context.setVariable("customerName", quotation.getCustomerName());
            context.setVariable("billingAddress", quotation.getAddress());
            context.setVariable("userEmail", quotation.getEmail());
            context.setVariable("paymentMethod", quotation.getPaymentMethod());

            // Additional details for the quotation
            context.setVariable("currency", "LKR"); // Assuming LKR as currency or use another dynamic field

            // Order items and total price
            context.setVariable("orderItems", quotation.getItems());  // Ensure this returns a List, not a Stream
            context.setVariable("totalQuotationAmount", quotation.getTotalPrice());

            // Generate HTML content from Thymeleaf template
            String htmlContent = templateEngine.process("quotation_pdf_template.html", context); // Use your Thymeleaf template name

            // Convert HTML to PDF
            return pdfGenerationService.convertHtmlToPdf(htmlContent);

        } catch (Exception ex) {
            // Log error and throw runtime exception
            ex.printStackTrace();
            throw new RuntimeException("Error generating quotation PDF", ex);
        }
    }

}
