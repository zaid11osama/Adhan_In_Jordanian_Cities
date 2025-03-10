package com.arabbank.hdf.uam.brain.orchestrator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobStatusDto {
    private String jobCode;
    private JobStatus status;
}
