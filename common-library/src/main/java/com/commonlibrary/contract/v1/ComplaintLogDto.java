package com.commonlibrary.contract.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintLogDto {
    private Long id;
    private String message;
    private String role;
    private String username;  // Changed from userId/userName to just username
    private String status;  // Added complaint status
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Colombo")
    private LocalDateTime createdAt;
    
    private Long complaintId;
} 