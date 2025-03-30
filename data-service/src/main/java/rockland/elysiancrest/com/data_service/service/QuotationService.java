package rockland.elysiancrest.com.data_service.service;

import org.springframework.data.domain.Page;
import rockland.elysiancrest.com.data_service.dto.QuotationDTO;
import rockland.elysiancrest.com.data_service.entity.quotation.QuotationMaster;

public interface QuotationService extends BaseService<QuotationMaster, QuotationDTO> {

    QuotationDTO create(QuotationDTO quotationDTO);

    QuotationDTO update(Long id, QuotationDTO quotationDTO);

    String generateQuotationEmailHtml(Long quotationId);

    Page<QuotationDTO> getAllQuotations(int page, int size, String sortBy, String sortDirection);

    QuotationDTO getQuotationById(Long quotationId);

    byte[] generateQuotationPdf(Long quotationId);

}
