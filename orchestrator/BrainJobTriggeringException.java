package com.arabbank.hdf.uam.brain.orchestrator;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error triggering brain jobs")
public class BrainJobTriggeringException extends RuntimeException {
}
