package com.csv.parser.parser;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CSV Edge Cases Tests")
class CsvParserEdgeCasesTest {

    @Test
    @DisplayName("should handle CSV with multiple rows")
    void testMultipleRows() {
        String csv = "John,30\nJane,25\nBob,35\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(3, rows.size());
    }

    @Test
    @DisplayName("should handle CSV with very long fields")
    void testVeryLongFields() {
        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longValue.append("A");
        }
        String csv = longValue.toString() + ",B\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals(10000, rows.get(0).get(0).length());
    }

    @Test
    @DisplayName("should handle CSV with many fields (100+)")
    void testManyFields() {
        StringBuilder csv = new StringBuilder();
        int fieldCount = 100;
        for (int i = 0; i < fieldCount; i++) {
            if (i > 0) csv.append(",");
            csv.append("F").append(i);
        }
        csv.append("\n");

        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv.toString()));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals(fieldCount + 1, rows.get(0).size());
        assertEquals("F0", rows.get(0).get(0));
        assertEquals("F99", rows.get(0).get(99));
    }

    @Test
    @DisplayName("should handle fields with commas inside quotes")
    void testFieldsWithCommasInQuotes() {
        String csv = "\"A,B,C\",\"D\"\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals(3, rows.get(0).size());
        assertEquals("A,B,C", rows.get(0).get(0));
        assertEquals("D", rows.get(0).get(1));
    }

    @Test
    @DisplayName("should handle tab delimiter")
    void testTabDelimiter() {
        String csv = "John\t30\nJane\t25\n";
        CsvConfig config = CsvConfig.builder().delimiter('\t').build();
        CsvParser parser = CsvParser.builder().config(config).build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("John", rows.get(0).get(0));
        assertEquals("30", rows.get(0).get(1));
    }

    @Test
    @DisplayName("should handle single quote character as quote char")
    void testSingleQuoteDelimiter() {
        String csv = "'John','30'\n'Jane','25'\n";
        CsvConfig config = CsvConfig.builder()
                .delimiter(',')
                .quoteChar('\'')
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
    @DisplayName("should handle Windows line endings (CRLF)")
    void testWindowsLineEndings() {
        String csv = "John,30\r\nJane,25\r\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(2, rows.size());
        assertEquals("John", rows.get(0).get(0));
        assertEquals("Jane", rows.get(1).get(0));
    }

    @Test
    @DisplayName("should handle quoted fields with escaped quotes")
    void testQuotedFieldsWithEscapedQuotes() {
        String csv = "\"Name: \"\"John\"\"\",Engineer\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals("Name: \"John\"", rows.get(0).get(0));
    }

    @Test
    @DisplayName("should handle mixed quoted and unquoted fields")
    void testMixedQuotedUnquoted() {
        String csv = "John,\"Jane\",30,\"25\"\n";
        CsvParser parser = CsvParser.builder().build();

        List<CsvRow> rows = new ArrayList<>();
        var iterator = parser.iterator(new StringReader(csv));
        iterator.forEachRemaining(rows::add);

        assertEquals(1, rows.size());
        assertEquals(5, rows.get(0).size());
        assertEquals("John", rows.get(0).get(0));
        assertEquals("Jane", rows.get(0).get(1));
        assertEquals("30", rows.get(0).get(2));
        assertEquals("25", rows.get(0).get(3));
        assertEquals("", rows.get(0).get(4));
    }

}
