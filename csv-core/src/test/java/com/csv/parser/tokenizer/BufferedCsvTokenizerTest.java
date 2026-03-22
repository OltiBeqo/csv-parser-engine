package com.csv.parser.tokenizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BufferedCsvTokenizer Tests")
class BufferedCsvTokenizerTest {

    @Test
    @DisplayName("should read single character")
    void testNextCharSingle() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("A"), 1024);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals(-1, tokenizer.nextChar());
    }

    @Test
    @DisplayName("should read multiple characters in sequence")
    void testNextCharSequence() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("ABC"), 1024);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
        assertEquals('C', tokenizer.nextChar());
        assertEquals(-1, tokenizer.nextChar());
    }

    @Test
    @DisplayName("should peek at character without consuming it")
    void testPeekChar() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("ABC"), 1024);
        
        assertEquals('A', tokenizer.peekChar());
        assertEquals('A', tokenizer.peekChar());
        assertEquals('A', tokenizer.nextChar());
        assertEquals('B', tokenizer.peekChar());
    }

    @Test
    @DisplayName("should handle newline character")
    void testNewlineChar() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("A\nB"), 1024);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals('\n', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
    }

    @Test
    @DisplayName("should handle carriage return character")
    void testCarriageReturnChar() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("A\rB"), 1024);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals('\r', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
    }

    @Test
    @DisplayName("should handle Windows line endings (CRLF)")
    void testCRLF() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("A\r\nB"), 1024);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals('\r', tokenizer.nextChar());
        assertEquals('\n', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
    }

    @Test
    @DisplayName("should handle quoted characters")
    void testQuotedChars() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("\"ABC\""), 1024);
        
        assertEquals('"', tokenizer.nextChar());
        assertEquals('A', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
        assertEquals('C', tokenizer.nextChar());
        assertEquals('"', tokenizer.nextChar());
    }

    @Test
    @DisplayName("should handle empty input")
    void testEmptyInput() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader(""), 1024);
        
        assertEquals(-1, tokenizer.nextChar());
        assertEquals(-1, tokenizer.peekChar());
    }

    @Test
    @DisplayName("should handle special characters")
    void testSpecialChars() throws IOException {
        String input = "!@#$%^&*()";
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader(input), 1024);
        
        for (char c : input.toCharArray()) {
            assertEquals(c, tokenizer.nextChar());
        }
        assertEquals(-1, tokenizer.nextChar());
    }

    @Test
    @DisplayName("should work with small buffer size")
    void testSmallBufferSize() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("ABCDEFGHIJ"), 2);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
        assertEquals('C', tokenizer.nextChar());
        assertEquals('D', tokenizer.nextChar());
        assertEquals('E', tokenizer.nextChar());
    }

    @Test
    @DisplayName("should work with large input and small buffer")
    void testLargeInputSmallBuffer() throws IOException {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            input.append("X");
        }
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader(input.toString()), 8);
        
        for (int i = 0; i < 1000; i++) {
            assertEquals('X', tokenizer.nextChar());
        }
        assertEquals(-1, tokenizer.nextChar());
    }

    @Test
    @DisplayName("should detect end of input")
    void testIsEndOfInput() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("AB"), 1024);
        
        assertFalse(tokenizer.isEndOfInput());
        tokenizer.nextChar();
        assertFalse(tokenizer.isEndOfInput());
        tokenizer.nextChar();
        assertTrue(tokenizer.isEndOfInput());
    }

    @Test
    @DisplayName("should detect end of input on empty reader")
    void testIsEndOfInputEmpty() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader(""), 1024);
        
        assertTrue(tokenizer.isEndOfInput());
    }

    @Test
    @DisplayName("should throw exception for null reader")
    void testNullReader() {
        assertThrows(IllegalArgumentException.class, () ->
                new BufferedCsvTokenizer(null, 1024)
        );
    }

    @Test
    @DisplayName("should throw exception for invalid buffer size")
    void testInvalidBufferSize() {
        assertThrows(IllegalArgumentException.class, () ->
                new BufferedCsvTokenizer(new StringReader("test"), 0)
        );
    }

    @Test
    @DisplayName("should throw exception for negative buffer size")
    void testNegativeBufferSize() {
        assertThrows(IllegalArgumentException.class, () ->
                new BufferedCsvTokenizer(new StringReader("test"), -1)
        );
    }

    @Test
    @DisplayName("should handle tabs")
    void testTabCharacter() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("A\tB"), 1024);
        
        assertEquals('A', tokenizer.nextChar());
        assertEquals('\t', tokenizer.nextChar());
        assertEquals('B', tokenizer.nextChar());
    }

    @Test
    @DisplayName("should peek multiple times consecutively")
    void testMultiplePeeks() throws IOException {
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(new StringReader("TEST"), 1024);
        
        for (int i = 0; i < 5; i++) {
            assertEquals('T', tokenizer.peekChar());
        }
        assertEquals('T', tokenizer.nextChar());
    }

}
