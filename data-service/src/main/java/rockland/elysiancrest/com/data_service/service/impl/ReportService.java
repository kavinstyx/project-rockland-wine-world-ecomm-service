package rockland.elysiancrest.com.data_service.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.SelectedFilterDto;
import com.commonlibrary.contract.v1.report.HeaderDto;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;
import rockland.elysiancrest.com.data_service.report.ReportSpecification;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final Map<String, ReportSpecification<?>> reportSpecifications;

    public ReportService(List<ReportSpecification<?>> specifications) {
        this.reportSpecifications = specifications.stream()
                .collect(Collectors.toMap(ReportSpecification::getReportName, spec -> spec));
    }

    public List<String> getAvailableReportNames() {
        return reportSpecifications.keySet()
                .stream()
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <T> Page<T> getReportData(String reportName, List<SelectedFilterDto> filters, int page, int size) {
        ReportSpecification<T> specification = (ReportSpecification<T>) getSpecification(reportName);
        return specification.getFilteredData(filters, page, size);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllReportData(String reportName, List<SelectedFilterDto> filters) {
        ReportSpecification<T> specification = (ReportSpecification<T>) getSpecification(reportName);
        return specification.getFilteredData(filters);
    }

    public List<FilterDto> getFilterOptions(String reportName) {
        ReportSpecification<?> specification = getSpecification(reportName);
        return specification.getFilterOptions()
                .stream()
                .filter(FilterDto::isValid)
                .toList();
    }

    public List<HeaderDto> getHeaders(String reportName) {
        ReportSpecification<?> specification = getSpecification(reportName);
        return specification.getHeaders();
    }

    public Map<String, Long> getTotalSmsAndResponsesCount(String reportName, List<SelectedFilterDto> filters) {
        ReportSpecification<?> specification = getSpecification(reportName);
        return specification.getTotalCounts(filters);
    }

    private ReportSpecification<?> getSpecification(String reportName) {
        ReportSpecification<?> specification = reportSpecifications.get(reportName);
        if (specification == null) {
            throw new IllegalArgumentException("Report not found: " + reportName);
        }
        return specification;
    }

    public void writeExcel(String reportName, List<?> data, Map<String, Long> totals, OutputStream outputStream) throws IOException {
        if (data == null || data.isEmpty()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(reportName);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("No data available for report: " + reportName);
            workbook.write(outputStream);
            workbook.close();
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(reportName);

        Class<?> clazz = data.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();

        // Define header, data, and total styles
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
        dataStyle.setWrapText(true);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.cloneStyleFrom(headerStyle);
        totalStyle.setAlignment(HorizontalAlignment.LEFT);
        totalStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

        CreationHelper createHelper = workbook.getCreationHelper();

        List<HeaderDto> headers = getHeadersFromClass(clazz);

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i).getName());
            cell.setCellStyle(headerStyle);
        }

        // Populate data rows
        int rowIndex = 1;
        for (Object item : data) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Cell cell = row.createCell(i);
                try {
                    Object value = field.get(item);
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                            cell.setCellStyle(dataStyle);
                        } else if (value instanceof Date) {
                            CellStyle dateStyle = workbook.createCellStyle();
                            dateStyle.cloneStyleFrom(dataStyle);
                            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                            cell.setCellValue((Date) value);
                            cell.setCellStyle(dateStyle);
                        } else {
                            cell.setCellValue(value.toString());
                            cell.setCellStyle(dataStyle);
                        }
                    } else {
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    }
                } catch (IllegalAccessException e) {
                    cell.setCellValue("ERROR");
                    cell.setCellStyle(dataStyle);
                }
            }
        }

        // Iterate through the totals map to add total rows dynamically
        int totalRowIndex = rowIndex + 1;

        for (Map.Entry<String, Long> totalEntry : totals.entrySet()) {
            String totalName = totalEntry.getKey();
            Long totalValue = totalEntry.getValue();
            addTotalRow(sheet, totalName, totalValue, totalStyle, totalRowIndex);
            totalRowIndex++;
        }

        // Auto size columns
        for (int i = 0; i < fields.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Freeze the header row
        sheet.createFreezePane(0, 1);

        // Write the workbook to output stream
        workbook.write(outputStream);
        workbook.close();
    }

    private void addTotalRow(Sheet sheet, String totalName, Long totalValue, CellStyle totalStyle, int rowIndex) {
        Row row = sheet.createRow(rowIndex);

        // Set the name of the total (first column)
        Cell nameCell = row.createCell(0);
        nameCell.setCellValue(totalName);
        nameCell.setCellStyle(totalStyle);

        // Set the value of the total in the second column
        Cell valueCell = row.createCell(1);
        if (totalValue != null) {
            valueCell.setCellValue(totalValue);
        } else {
            valueCell.setCellValue(0);
        }
        valueCell.setCellStyle(totalStyle);
    }

    public List<HeaderDto> getHeadersFromClass(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    CsvHeader annotation = field.getAnnotation(CsvHeader.class);
                    String name = (annotation != null) ? annotation.value() : field.getName();
                    return new HeaderDto(name, field.getName());
                })
                .toList();
    }
}