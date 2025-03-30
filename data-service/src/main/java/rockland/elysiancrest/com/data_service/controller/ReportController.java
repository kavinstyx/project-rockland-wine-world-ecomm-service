package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.report.ReportResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.SelectedFiltersWrapper;
import com.commonlibrary.contract.v1.report.HeaderDto;
import rockland.elysiancrest.com.data_service.service.impl.ReportService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@CrossOrigin
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAvailableReports() {
        List<String> reportNames = reportService.getAvailableReportNames();
        return ResponseEntity.ok(reportNames);
    }


    @PostMapping("/{reportName}")
    public <T> ResponseEntity<ReportResponse<T>> getReport(
            @PathVariable String reportName,
            @RequestBody SelectedFiltersWrapper filtersWrapper,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        List<HeaderDto> headers = reportService.getHeaders(reportName);
        Page<T> reportData = reportService.getReportData(reportName, filtersWrapper.getFilters(), page, size);
        Map<String, Long> totals = reportService.getTotalSmsAndResponsesCount(reportName, filtersWrapper.getFilters());
        return ResponseEntity.ok(new ReportResponse<>(headers, reportData, totals));
    }


    // CSV download endpoint
    @PostMapping("/{reportName}/download")
    public void downloadReport(
            @PathVariable String reportName,
            @RequestBody SelectedFiltersWrapper filtersWrapper,
            HttpServletResponse response) throws IOException {

        // Set the content type to Excel MIME type
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // Optionally, add timestamp to the filename to prevent caching issues
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = reportName + "_" + timestamp + ".xlsx";

        // Set the Content-Disposition header to prompt download with the correct filename
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Retrieve the report data based on report name and filters
        List<?> reportData = reportService.getAllReportData(reportName, filtersWrapper.getFilters());
        Map<String, Long> totals = reportService.getTotalSmsAndResponsesCount(reportName, filtersWrapper.getFilters());

        // Stream the Excel file directly to the response's output stream
        try (OutputStream outputStream = response.getOutputStream()) {
            reportService.writeExcel(reportName, reportData, totals, outputStream);
            // No need to manually flush or close the stream; try-with-resources handles it
        } catch (IOException e) {
            // Log the error and set an appropriate HTTP status code
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report.");
        }
    }

    @GetMapping("/{reportName}/filters")
    public ResponseEntity<List<FilterDto>> getFilterOptions(@PathVariable String reportName) {
        List<FilterDto> filters = reportService.getFilterOptions(reportName);
        return ResponseEntity.ok(filters);
    }
}
