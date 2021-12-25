package com.lmf.exception;

/**
 * repeat type com.lmf.exception
 *
 * @author lmf
 * @date 2021-12-25
 */
public class RepeatTypeException extends ServiceException {
    public RepeatTypeException(String message) {
        super(message);
    }

    public RepeatTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
