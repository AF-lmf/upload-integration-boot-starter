package com.lmf.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception caused by service.
 *
 * @author lmf
 * @date 2021-12-25
 */
public class ServiceException extends AbstractHaloException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
