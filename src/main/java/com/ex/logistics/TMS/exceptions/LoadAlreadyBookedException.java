package com.ex.logistics.TMS.exceptions;

public class LoadAlreadyBookedException extends RuntimeException {
    public LoadAlreadyBookedException(String message) {

        super(message);
    }
}
