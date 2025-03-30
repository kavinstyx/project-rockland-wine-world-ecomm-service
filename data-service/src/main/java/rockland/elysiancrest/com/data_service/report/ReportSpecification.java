package rockland.elysiancrest.com.data_service.report;

import org.springframework.data.domain.Page;
import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.SelectedFilterDto;
import com.commonlibrary.contract.v1.report.HeaderDto;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface ReportSpecification<T> {

    String getReportName();

    Page<T> getFilteredData(List<SelectedFilterDto> filters, int page, int size);

    List<T> getFilteredData(List<SelectedFilterDto> filters);

    List<FilterDto> getFilterOptions();

    default List<HeaderDto> getHeaders() {
        Class<?> type = getType();
        return Arrays.stream(type.getDeclaredFields())
                .map(field -> {
                    CsvHeader annotation = field.getAnnotation(CsvHeader.class);
                    String name = (annotation != null) ? annotation.value() : field.getName();
                    return new HeaderDto(name, field.getName());
                })
                .toList();
    }

    Class<T> getType();

    Map<String, Long> getTotalCounts(List<SelectedFilterDto> filters);

    record DateRange(LocalDate startDate, LocalDate endDate) {}
}
