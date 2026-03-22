package com.csv.parser.tokenizer;

import java.io.IOException;
import java.io.Reader;

public class BufferedCsvTokenizer implements CsvTokenizer {

    private final Reader reader;
    private final char[] buffer;

    private int position;
    private int limit;

    public BufferedCsvTokenizer(Reader reader, int bufferSize) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }
        this.reader = reader;
        this.buffer = new char[bufferSize];
        this.position = 0;
        this.limit = 0;
    }

    @Override
    public int nextChar() throws IOException {

        int c = peekChar();

        if (c != -1) {
            position++;
        }

        return c;
    }

    @Override
    public int peekChar() throws IOException {

        if (position >= limit) {
            fillBuffer();

            if (limit == -1) {
                return -1;
            }
        }

        return buffer[position];
    }

    @Override
    public boolean isEndOfInput() throws IOException {
        return peekChar() == -1;
    }

    private void fillBuffer() throws IOException {

        limit = reader.read(buffer);
        position = 0;
    }
}