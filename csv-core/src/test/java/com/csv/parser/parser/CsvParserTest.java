package com.csv.parser.parser;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsvParser Tests")
class CsvParserTest {

    @Test
    @DisplayName("should parse simple CSV with default config")
    void testParseSimpleCSV() {
        String csv = "John,30\nJane,25\n";
        CsvParser parser = CsvParser.builder().build();
        
        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("John", rows.get(0).get(0));
        assertEquals("30", rows.get(0).get(1));
        assertEquals("Jane", rows.get(1).get(0));
        assertEquals("25", rows.get(1).get(1));
    }

    @Test
    @DisplayName("should parse CSV with quoted fields")
    void testParseCSVWithQuotedFields() {
        String csv = "\"John Doe\",\"New York\"\n\"Jane Smith\",\"Los Angeles\"\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("John Doe", rows.get(0).get(0));
        assertEquals("New York", rows.get(0).get(1));
        assertEquals("Jane Smith", rows.get(1).get(0));
        assertEquals("Los Angeles", rows.get(1).get(1));
    }

    @Test
    @DisplayName("should parse CSV with escaped quotes")
    void testParseCSVWithEscapedQuotes() {
        String csv = "\"John \"\"Doe\"\"\",Engineer\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals("John \"Doe\"", rows.get(0).get(0));
        assertEquals("Engineer", rows.get(0).get(1));
    }

    @Test
    @DisplayName("should parse CSV with different delimiter")
    void testParseCSVWithDifferentDelimiter() {
        String csv = "John;30\nJane;25\n";
        CsvConfig config = CsvConfig.builder()
                .delimiter(';')
                .build();
        CsvParser parser = CsvParser.builder().config(config).build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("John", rows.get(0).get(0));
        assertEquals("30", rows.get(0).get(1));
    }

    @Test
    @DisplayName("should parse CSV with headers")
    void testParseCSVWithHeaders() {
        String csv = "Name,Age\nJohn,30\nJane,25\n";
        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();
        CsvParser parser = CsvParser.builder().config(config).build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("John", rows.get(0).get("Name"));
        assertEquals("30", rows.get(0).get("Age"));
        assertEquals("Jane", rows.get(1).get("Name"));
        assertEquals("25", rows.get(1).get("Age"));
    }

    @Test
    @DisplayName("should parse CSV with newlines in quoted fields")
    void testParseCSVWithNewlinesInQuotedFields() {
        String csv = "\"Line1\nLine2\",Value\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals("Line1\nLine2", rows.get(0).get(0));
        assertEquals("Value", rows.get(0).get(1));
    }

    @Test
    @DisplayName("should parse empty CSV")
    void testParseEmptyCSV() {
        String csv = "";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(0, rows.size());
    }

    @Test
    @DisplayName("should use stream API")
    void testStreamAPI() {
        String csv = "John,30\nJane,25\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = parser.stream(new StringReader(csv))
                .collect(Collectors.toList());

        assertEquals(2, rows.size());
        assertEquals("John", rows.get(0).get(0));
    }

    @Test
    @DisplayName("should throw exception for null reader in iterator")
    void testNullReaderIterator() {
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.iterator((java.io.Reader) null)
        );
    }

    @Test
    @DisplayName("should throw exception for null file in iterator")
    void testNullFileIterator() throws IOException {
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.iterator((File) null)
        );
    }

    @Test
    @DisplayName("should throw exception for null reader in stream")
    void testNullReaderStream() {
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.stream((java.io.Reader) null)
        );
    }

    @Test
    @DisplayName("should throw exception for null file in stream")
    void testNullFileStream() throws IOException {
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.stream((File) null)
        );
    }

    @Test
    @DisplayName("should support mapping with mapper function")
    void testMapIterator() {
        String csv = "John,30\nJane,25\n";
        CsvParser parser = CsvParser.builder().build();

        CsvRowMapper<String> mapper = row -> row.get(0) + " (" + row.get(1) + ")";
        
        List<String> results = new ArrayList<>();
        var iterator = parser.mapIterator(new StringReader(csv), mapper);
        iterator.forEachRemaining(results::add);

        assertEquals(2, results.size());
        assertEquals("John (30)", results.get(0));
        assertEquals("Jane (25)", results.get(1));
    }

    @Test
    @DisplayName("should support mapping with stream")
    void testMapStream() {
        String csv = "John,30\nJane,25\n";
        CsvParser parser = CsvParser.builder().build();

        CsvRowMapper<String> mapper = row -> row.get(0);
        
        List<String> results = parser.mapStream(new StringReader(csv), mapper)
                .collect(Collectors.toList());

        assertEquals(2, results.size());
        assertEquals("John", results.get(0));
        assertEquals("Jane", results.get(1));
    }

    @Test
    @DisplayName("should throw exception for null mapper in mapIterator")
    void testNullMapperIterator() {
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.mapIterator(new StringReader("a,b"), null)
        );
    }

    @Test
    @DisplayName("should throw exception for null mapper in mapStream")
    void testNullMapperStream() {
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.mapStream(new StringReader("a,b"), null)
        );
    }

    @Test
    @DisplayName("should handle trailing commas")
    void testTrailingCommas() {
        String csv = "John,30\nJane,25\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals(3, rows.get(0).size());
        assertEquals("John", rows.get(0).get(0));
        assertEquals("30", rows.get(0).get(1));
        assertEquals("", rows.get(0).get(2));
    }

    @Test
    @DisplayName("should handle multiple empty fields")
    void testMultipleEmptyFields() {
        String csv = ",,\n,A,\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("", rows.get(0).get(0));
        assertEquals("", rows.get(0).get(1));
        assertEquals("", rows.get(0).get(2));
    }

}
