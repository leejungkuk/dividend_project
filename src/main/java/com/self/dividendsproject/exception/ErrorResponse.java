package com.self.dividendsproject.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}
