package com.lmf.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Base com.lmf.exception of the project.
 *
 * @author lmf
 * @date 2021-12-25
 */
public abstract class AbstractHaloException extends RuntimeException {

    /**
     * Error errorData.
     */
    private Object errorData;

    public AbstractHaloException(String message) {
        super(message);
    }

    public AbstractHaloException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Http status code
     *
     * @return {@link HttpStatus}
     */
    @NonNull
    public abstract HttpStatus getStatus();

    @Nullable
    public Object getErrorData() {
        return errorData;
    }

    /**
     * Sets error errorData.
     *
     * @param errorData error data
     * @return current com.lmf.exception.
     */
    @NonNull
    public AbstractHaloException setErrorData(@Nullable Object errorData) {
        this.errorData = errorData;
        return this;
    }
}
