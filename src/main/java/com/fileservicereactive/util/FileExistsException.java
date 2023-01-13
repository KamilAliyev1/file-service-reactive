package com.fileservicereactive.util;

public class FileExistsException extends RuntimeException{
    public FileExistsException() {
    }

    public FileExistsException(String message) {
        super(message);
    }
}
