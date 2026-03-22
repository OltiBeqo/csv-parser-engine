package com.csv.parser.config;

import com.csv.parser.exception.CsvConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsvConfig Tests")
class CsvConfigTest {

    @Test
    @DisplayName("should create default configuration")
    void testDefaultConfiguration() {
        CsvConfig config = CsvConfig.builder().build();

        assertEquals(',', config.getDelimiter());
        assertEquals('"', config.getQuoteChar());
        assertEquals('"', config.getEscapeChar());
        assertFalse(config.hasHeader());
        assertEquals(8192, config.getBufferSize());
        assertTrue(config.isIgnoreCase());
        assertTrue(config.isStrictQuoteValidation());
    }

    @Test
    @DisplayName("should create custom configuration")
    void testCustomConfiguration() {
        CsvConfig config = CsvConfig.builder()
                .delimiter(';')
                .quoteChar('\'')
                .escapeChar('\\')
                .hasHeader(true)
                .bufferSize(16384)
                .ignoreCase(false)
                .strictQuoteValidation(false)
                .build();

        assertEquals(';', config.getDelimiter());
        assertEquals('\'', config.getQuoteChar());
        assertEquals('\\', config.getEscapeChar());
        assertTrue(config.hasHeader());
        assertEquals(16384, config.getBufferSize());
        assertFalse(config.isIgnoreCase());
        assertFalse(config.isStrictQuoteValidation());
    }

    @Test
    @DisplayName("should throw exception when delimiter equals quote character")
    void testDelimiterEqualsQuoteChar() {
        assertThrows(CsvConfigurationException.class, () ->
                CsvConfig.builder()
                        .delimiter(',')
                        .quoteChar(',')
                        .build()
        );
    }

    @Test
    @DisplayName("should throw exception when escape character equals delimiter")
    void testEscapeCharEqualsDelimiter() {
        assertThrows(CsvConfigurationException.class, () ->
                CsvConfig.builder()
                        .delimiter(';')
                        .escapeChar(';')
                        .build()
        );
    }

    @Test
    @DisplayName("should throw exception when buffer size is zero")
    void testBufferSizeZero() {
        assertThrows(CsvConfigurationException.class, () ->
                CsvConfig.builder()
                        .bufferSize(0)
                        .build()
        );
    }

    @Test
    @DisplayName("should throw exception when buffer size is negative")
    void testBufferSizeNegative() {
        assertThrows(CsvConfigurationException.class, () ->
                CsvConfig.builder()
                        .bufferSize(-1)
                        .build()
        );
    }

    @Test
    @DisplayName("should allow different delimiters")
    void testDifferentDelimiters() {
        char[] delimiters = {',', ';', '\t', '|'};
        for (char delimiter : delimiters) {
            CsvConfig config = CsvConfig.builder()
                    .delimiter(delimiter)
                    .quoteChar('"')
                    .build();
            assertEquals(delimiter, config.getDelimiter());
        }
    }

    @Test
    @DisplayName("should allow different quote characters")
    void testDifferentQuoteChars() {
        char[] quoteChars = {'"', '\'', '`'};
        for (char quoteChar : quoteChars) {
            CsvConfig config = CsvConfig.builder()
                    .delimiter(',')
                    .quoteChar(quoteChar)
                    .build();
            assertEquals(quoteChar, config.getQuoteChar());
        }
    }

    @Test
    @DisplayName("should allow large buffer sizes")
    void testLargeBufferSize() {
        CsvConfig config = CsvConfig.builder()
                .bufferSize(1_000_000)
                .build();
        assertEquals(1_000_000, config.getBufferSize());
    }

}
