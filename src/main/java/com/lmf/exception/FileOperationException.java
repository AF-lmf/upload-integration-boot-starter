package com.lmf.exception;

/**
 * File operation com.lmf.exception.
 *
 * @author lmf
 * @date 2021-12-25
 */
public class FileOperationException extends ServiceException {
    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
