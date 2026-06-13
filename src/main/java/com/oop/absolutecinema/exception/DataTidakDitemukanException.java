package com.oop.absolutecinema.exception;

public class DataTidakDitemukanException extends RuntimeException {
    public DataTidakDitemukanException(String pesan) {
        super(pesan); 
    }
}