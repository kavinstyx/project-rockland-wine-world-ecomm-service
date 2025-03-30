package com.commonlibrary.contract.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDto {
    private Long id;
    private String inquiryType;
    private String subject;
    private String message;
    private String attachmentPath;
    private String temporaryLink;
    private String status;
    private String adminComment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Colombo")
    private LocalDateTime createdAt;

    private Long userId;
    private String userName;
    private Long orderId;

    private List<ComplaintLogDto> complaintLogs;
}

