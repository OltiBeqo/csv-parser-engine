package com.csv.parser.parser;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.exception.CsvParseException;
import com.csv.parser.exception.ExceptionMessages;
import com.csv.parser.model.CsvRow;
import com.csv.parser.tokenizer.CsvTokenizer;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CsvStateMachine implements Iterator<CsvRow> {

    private final CsvTokenizer tokenizer;
    private final CsvConfig config;
    private final RowBuilder rowBuilder;

    private CsvRow nextRow;
    private boolean endOfInput = false;
    private boolean headerProcessed = false;
    private int lineNumber = 1;
    private int fieldNumber = 0;

    private static final int START_FIELD = 1;
    private static final int IN_FIELD = 2;
    private static final int IN_QUOTED_FIELD = 3;
    private final static int ESCAPED_QUOTE = 4;

    public CsvStateMachine(CsvTokenizer tokenizer, CsvConfig config) {
        if (tokenizer == null) {
            throw new IllegalArgumentException(ExceptionMessages.TOKENIZER_CANNOT_BE_NULL);
        }
        if (config == null) {
            throw new IllegalArgumentException(ExceptionMessages.CONFIG_CANNOT_BE_NULL);
        }
        this.tokenizer = tokenizer;
        this.config = config;
        this.rowBuilder = new RowBuilder(config.isIgnoreCase());
        prepareNextRow();
    }

    private void prepareNextRow() {
        try {
            if (endOfInput) {
                nextRow = null;
                return;
            }

            nextRow = parseRow();

            if (nextRow == null) {
                endOfInput = true;
            }

        } catch (IOException e) {
            throw new CsvParseException(ExceptionMessages.ERROR_PARSING_CSV, e);
        }
    }

    @Override
    public boolean hasNext() {
        return nextRow != null;
    }

    @Override
    public CsvRow next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        CsvRow current = nextRow;
        prepareNextRow();
        return current;
    }

    private CsvRow parseRow() throws IOException {
        final char DELIM = config.getDelimiter();
        final char QUOTE = config.getQuoteChar();

        int state = START_FIELD;
        boolean rowFinished = false;
        fieldNumber = 0;

        while (!rowFinished) {
            int next = tokenizer.nextChar();

            if (tokenizer.isEndOfInput()) { // EOF
                if (state == IN_QUOTED_FIELD) {
                    throw new CsvParseException(
                            String.format(ExceptionMessages.UNCLOSED_QUOTED_FIELD, lineNumber, fieldNumber)
                    );
                }

                // If we're at the start with no data, it's EOF with no row to return
                if (state == START_FIELD && rowBuilder.getFieldBufferLength() == 0 && rowBuilder.getFields().isEmpty()) {
                    return null;
                }

                if (state != START_FIELD || rowBuilder.getFieldBufferLength() > 0) {
                    rowBuilder.endField();
                }
                rowFinished = true;
                break;
            }

            char c = (char) next;

            switch (state) {

                case START_FIELD:
                    fieldNumber++;
                    if (c == QUOTE) {
                        state = IN_QUOTED_FIELD;
                    } else if (c == DELIM) {
                        rowBuilder.endField();
                    } else if (c == '\n' || c == '\r') {
                        if (c == '\r' && tokenizer.peekChar() == '\n') {
                            tokenizer.nextChar(); // consume \n after \r
                        }
                        rowBuilder.endField();
                        rowFinished = true;
                        lineNumber++;
                    } else {
                        rowBuilder.appendChar(c);
                        state = IN_FIELD;
                    }
                    break;

                case IN_FIELD:
                    if (c == DELIM) {
                        rowBuilder.endField();
                        state = START_FIELD;
                    } else if (c == '\n' || c == '\r') {
                        if (c == '\r' && tokenizer.peekChar() == '\n') tokenizer.nextChar();
                        rowBuilder.endField();
                        rowFinished = true;
                        lineNumber++;
                    } else {
                        rowBuilder.appendChar(c);
                    }
                    break;

                case IN_QUOTED_FIELD:
                    if (c == QUOTE) {
                        state = ESCAPED_QUOTE;
                    } else {
                        rowBuilder.appendChar(c);
                    }
                    break;

                case ESCAPED_QUOTE:
                    if (c == QUOTE) {             // escaped quote
                        rowBuilder.appendChar(c);
                        state = IN_QUOTED_FIELD;
                    } else if (c == DELIM) {      // end of quoted field
                        rowBuilder.endField();
                        state = START_FIELD;
                    } else if (c == '\n' || c == '\r') { // end of row
                        if (c == '\r' && tokenizer.peekChar() == '\n') tokenizer.nextChar();
                        rowBuilder.endField();
                        rowFinished = true;
                        lineNumber++;
                    } else if (!config.isStrictQuoteValidation() && Character.isWhitespace(c)) {
                        // Skip whitespace after closing quote in lenient mode
                        // Continue in ESCAPED_QUOTE state until we find delimiter or newline
                    } else if (!config.isStrictQuoteValidation()) {
                        // In lenient mode, only treat whitespace as skippable
                        // Other characters still need to be delim/newline
                        throw new CsvParseException(
                                String.format(ExceptionMessages.INVALID_CHAR_AFTER_QUOTE_STRICT, c, lineNumber, fieldNumber)
                        );
                    } else {
                        throw new CsvParseException(
                                String.format(ExceptionMessages.INVALID_CHAR_AFTER_QUOTE_STRICT, c, lineNumber, fieldNumber)
                        );
                    }
                    break;
            }
        }

        // first row may be header
        if (!headerProcessed && config.hasHeader()) {
            rowBuilder.setHeader(rowBuilder.getFields());
            headerProcessed = true;
            rowBuilder.getFields().clear(); // Clear fields before recursive call
            return parseRow(); // skip header, read next row
        }

        return rowBuilder.endRow();
    }
}