package com.pilates.booking.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class EmailAlreadyUsedException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super(
            HttpStatus.CONFLICT,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.CONFLICT.value())
                .withType(ErrorConstants.EMAIL_ALREADY_USED_TYPE)
                .withTitle("Email is already in use!")
                .withProperty("message", "EMAIL_ALREADY_USED")
                .build(),
            null
        );
    }

    public ProblemDetailWithCause getProblemDetailWithCause() {
        return (ProblemDetailWithCause) this.getBody();
    }
}
