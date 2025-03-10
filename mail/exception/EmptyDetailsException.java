package com.arabbank.hdf.uam.brain.mail.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Mailing request must have all required details")
public class EmptyDetailsException extends MailingException {
}
