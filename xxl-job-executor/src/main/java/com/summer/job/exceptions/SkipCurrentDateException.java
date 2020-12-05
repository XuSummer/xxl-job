package com.summer.job.exceptions;

/**
 * @Desc
 * @Author Summer
 * @Date 2020/12/5 12:20
 */
public class SkipCurrentDateException extends RuntimeException {
    public SkipCurrentDateException() {
        super();
    }

    public SkipCurrentDateException(String message) {
        super(message);
    }

    public SkipCurrentDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkipCurrentDateException(Throwable cause) {
        super(cause);
    }

    protected SkipCurrentDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
