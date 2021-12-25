package com.lmf.exception;

/**
 * Image format com.lmf.exception.
 *
 * @author lmf
 * @date 2021-12-25
 */
public class ImageFormatException extends BadRequestException {

    public ImageFormatException(String message) {
        super(message);
    }

    public ImageFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
