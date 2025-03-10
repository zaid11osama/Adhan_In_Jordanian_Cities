package com.arabbank.hdf.uam.brain.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ApiErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private String trace;

    public ApiErrorResponse(Exception e) {
        message = e.getMessage();
        trace = ExceptionUtils.getStackTrace(e);
    }
}
