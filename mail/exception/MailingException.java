package com.arabbank.hdf.uam.brain.mail.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MailingException extends RuntimeException {
}
