package com.csv.parser.tokenizer;

import com.csv.parser.exception.ExceptionMessages;
import java.io.IOException;
import java.io.Reader;

public class BufferedCsvTokenizer implements CsvTokenizer {

    private final Reader reader;
    private final char[] buffer;

    private int position;
    private int limit;

    public BufferedCsvTokenizer(Reader reader, int bufferSize) {
        if (reader == null) {
            throw new IllegalArgumentException(ExceptionMessages.READER_CANNOT_BE_NULL);
        }
        if (bufferSize <= 0) {
            throw new IllegalArgumentException(ExceptionMessages.INVALID_BUFFER_SIZE);
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