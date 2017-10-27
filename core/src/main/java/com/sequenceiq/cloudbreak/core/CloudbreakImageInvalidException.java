package com.sequenceiq.cloudbreak.core;

public class CloudbreakImageInvalidException extends Exception {

    public CloudbreakImageInvalidException(String message) {
        super(message);
    }

    public CloudbreakImageInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudbreakImageInvalidException(Throwable cause) {
        super(cause);
    }
}
