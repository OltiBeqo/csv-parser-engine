package com.csv.parser.exception;

public class CsvParseException extends CsvException {

    public CsvParseException(String message) {
        super(message);
    }

    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
