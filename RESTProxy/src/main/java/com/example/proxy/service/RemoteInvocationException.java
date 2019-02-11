package com.example.proxy.service;

public class RemoteInvocationException extends RuntimeException {

    public RemoteInvocationException() {
        // TODO Auto-generated constructor stub
    }

    public RemoteInvocationException(String message) {
        super(message);
        
    }

    public RemoteInvocationException(Throwable cause) {
        super(cause);
            }

    public RemoteInvocationException(String message, Throwable cause) {
        super(message, cause);
        
    }

    public RemoteInvocationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        
    }

}
