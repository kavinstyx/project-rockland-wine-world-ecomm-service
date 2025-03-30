package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;
import rockland.elysiancrest.com.data_service.dto.QuotationDTO;
import rockland.elysiancrest.com.data_service.repo.QuotationMasterRepo;
import rockland.elysiancrest.com.data_service.service.PdfGenerationService;
import rockland.elysiancrest.com.data_service.service.QuotationService;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @PostMapping
    public ResponseEntity<Response<QuotationDTO>> createQuotation(@RequestBody QuotationDTO quotationDTO) {
        QuotationDTO createdQuotation = quotationService.create(quotationDTO);
        return ResponseEntity.ok(
                Response.success(createdQuotation, "Quotation created successfully")
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<QuotationDTO>> updateQuotation(@PathVariable Long id, @RequestBody QuotationDTO quotationDTO) {
        // Set the ID to the DTO to ensure the update is tied to the correct record
        quotationDTO.setId(id);

        // Update the quotation using the service layer
        QuotationDTO updatedQuotation = quotationService.update(id, quotationDTO);

        return ResponseEntity.ok(
                Response.success(updatedQuotation, "Quotation updated successfully")
        );
    }

    @GetMapping("/quotation/{quotationId}/email")
    public ResponseEntity<Response<String>> generateQuotationEmail(@PathVariable Long quotationId) {
        try {
            // Generate the quotation email HTML (not sending to front-end)
            String status = quotationService.generateQuotationEmailHtml(quotationId);

            // Build and return success response
            Response<String> response = Response.success("Quotation email generation for ID: " + quotationId +" " + status, status);
            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            // Build and return error response
            ex.printStackTrace();
            Response<String> response = Response.success(null, "Quotation not found with ID: " + quotationId);
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Response<Page<QuotationDTO>>> getAllQuotations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        // Fetch quotations using the service layer with pagination
        Page<QuotationDTO> quotations = quotationService.getAllQuotations(page, size, sortBy, sortDirection);

        // Return success response with paginated quotations
        return ResponseEntity.ok(
                Response.success(quotations, "Quotations fetched successfully")
        );
    }

    @GetMapping("/{quotationId}")
    public ResponseEntity<Response<QuotationDTO>> getQuotationById(@PathVariable Long quotationId) {
        try {
            // Fetch the quotation by ID using the service layer
            QuotationDTO quotation = quotationService.getQuotationById(quotationId);

            // Return success response
            return ResponseEntity.ok(
                    Response.success(quotation, "Quotation fetched successfully")
            );
        } catch (RuntimeException ex) {
            // Handle case when quotation is not found
            ex.printStackTrace();
            return ResponseEntity.status(404).body(
                    Response.error("Quotation not found with ID: " + quotationId)
            );
        }
    }

    @DeleteMapping("/quotations/{id}")
    public ResponseEntity<String> deleteQuotation(@PathVariable Long id) {
        quotationService.deleteById(id);
        return ResponseEntity.ok("Quotation deleted successfully.");
    }


    @GetMapping("/{quotationId}/download")
    public ResponseEntity<byte[]> downloadQuotation(@PathVariable Long quotationId) {
        try {
            // Call the service to generate the PDF
            byte[] pdfBytes = quotationService.generateQuotationPdf(quotationId);

            // Return the PDF as a downloadable response
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=quotation_" + quotationId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException ex) {
            // Handle error and send error message as a response
            ex.printStackTrace();
            return ResponseEntity.status(400).body(null);  // Return an error response if quotation not found
        }
    }


}
