package com.csv.parser.tokenizer;

import java.io.IOException;

public interface CsvTokenizer {

    int nextChar() throws IOException;

    int peekChar() throws IOException;

    boolean isEndOfInput() throws IOException;

}
