package com.csv.parser.parser;

import com.csv.parser.exception.ExceptionMessages;
import com.csv.parser.model.CsvRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowBuilder {

    private final StringBuilder fieldBuffer;
    private final List<String> fields;
    private final boolean ignoreCase;

    private Map<String, Integer> headerIndex;

    public RowBuilder() {
        this(true);
    }

    public RowBuilder(boolean ignoreCase) {
        this.fieldBuffer = new StringBuilder(64);
        this.fields = new ArrayList<>();
        this.ignoreCase = ignoreCase;
    }

    public void appendChar(char c) {
        fieldBuffer.append(c);
    }

    public void endField() {

        fields.add(fieldBuffer.toString());

        fieldBuffer.setLength(0);
    }

    public CsvRow endRow() {

        // finalize last field
        endField();

        CsvRow row = new CsvRow(new ArrayList<>(fields), headerIndex, ignoreCase);

        fields.clear();

        return row;
    }

    public void setHeader(List<String> headers) {
        if (headers == null) {
            throw new IllegalArgumentException(ExceptionMessages.HEADERS_CANNOT_BE_NULL);
        }
        headerIndex = new java.util.HashMap<>();

        for (int i = 0; i < headers.size(); i++) {
            headerIndex.put(headers.get(i), i);
        }
    }

    public int getFieldBufferLength() {
        return fieldBuffer.length();
    }

    public List<String> getFields() {
        return fields;
    }

    Map<String, Integer> getHeaderIndex() {
        return headerIndex;
    }
}
