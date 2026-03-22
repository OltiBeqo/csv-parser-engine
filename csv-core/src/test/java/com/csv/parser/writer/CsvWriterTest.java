package com.csv.parser.writer;

import com.csv.parser.config.CsvConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsvWriter Tests")
class CsvWriterTest {

    @Test
    @DisplayName("should write simple row")
    void testWriteSimpleRow() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John", "30");
        }
        
        assertEquals("John,30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should write multiple rows")
    void testWriteMultipleRows() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John", "30");
            writer.writeRow("Jane", "25");
        }
        
        assertEquals("John,30\r\nJane,25\r\n", sw.toString());
    }

    @Test
    @DisplayName("should quote field containing delimiter")
    void testQuoteFieldWithDelimiter() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John,Doe", "30");
        }
        
        assertEquals("\"John,Doe\",30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should quote field containing quote character")
    void testQuoteFieldWithQuote() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John \"Johnny\" Doe", "30");
        }
        
        assertEquals("\"John \"\"Johnny\"\" Doe\",30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should quote field containing newline")
    void testQuoteFieldWithNewline() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John\nDoe", "30");
        }
        
        assertEquals("\"John\nDoe\",30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should quote field containing carriage return")
    void testQuoteFieldWithCarriageReturn() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John\rDoe", "30");
        }
        
        assertEquals("\"John\rDoe\",30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should write header row")
    void testWriteHeader() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeHeader("Name", "Age");
            writer.writeRow("John", "30");
        }
        
        assertEquals("Name,Age\r\nJohn,30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should write header from list")
    void testWriteHeaderFromList() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeHeader(Arrays.asList("Name", "Age", "City"));
            writer.writeRow("John", "30", "NYC");
        }
        
        assertEquals("Name,Age,City\r\nJohn,30,NYC\r\n", sw.toString());
    }

    @Test
    @DisplayName("should write row from list")
    void testWriteRowFromList() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow(Arrays.asList("John", "30"));
        }
        
        assertEquals("John,30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle custom delimiter")
    void testCustomDelimiter() throws IOException {
        StringWriter sw = new StringWriter();
        CsvConfig config = CsvConfig.builder().delimiter(';').build();
        try (CsvWriter writer = new CsvWriter(sw, config)) {
            writer.writeRow("John", "30");
        }
        
        assertEquals("John;30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle custom quote character")
    void testCustomQuoteChar() throws IOException {
        StringWriter sw = new StringWriter();
        CsvConfig config = CsvConfig.builder()
                .delimiter(',')
                .quoteChar('\'')
                .escapeChar('\'')
                .build();
        try (CsvWriter writer = new CsvWriter(sw, config)) {
            writer.writeRow("John,Doe", "30");
        }
        
        assertEquals("'John,Doe',30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle custom escape character")
    void testCustomEscapeChar() throws IOException {
        StringWriter sw = new StringWriter();
        CsvConfig config = CsvConfig.builder()
                .delimiter(',')
                .quoteChar('"')
                .escapeChar('\\')
                .build();
        try (CsvWriter writer = new CsvWriter(sw, config)) {
            writer.writeRow("John \"Johnny\" Doe", "30");
        }
        
        assertEquals("\"John \\\"Johnny\\\" Doe\",30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle empty row")
    void testEmptyRow() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow();
        }
        
        assertEquals("\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle single field")
    void testSingleField() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("OnlyField");
        }
        
        assertEquals("OnlyField\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle empty field")
    void testEmptyField() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John", "", "30");
        }
        
        assertEquals("John,,30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should handle many fields")
    void testManyFields() throws IOException {
        StringWriter sw = new StringWriter();
        String[] fields = new String[100];
        for (int i = 0; i < 100; i++) {
            fields[i] = "Field" + i;
        }
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow(fields);
        }
        
        String result = sw.toString();
        assertTrue(result.contains("Field0"));
        assertTrue(result.contains("Field99"));
        assertTrue(result.endsWith("\r\n"));
    }

    @Test
    @DisplayName("should handle field with multiple special characters")
    void testFieldWithMultipleSpecialChars() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John,Doe\"Smith\nDoe", "30");
        }
        
        assertEquals("\"John,Doe\"\"Smith\nDoe\",30\r\n", sw.toString());
    }

    @Test
    @DisplayName("should throw exception on null field value")
    void testNullFieldValue() {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            assertThrows(NullPointerException.class, () ->
                    writer.writeRow("John", null, "30")
            );
        } catch (IOException e) {
            fail("Should not throw IOException", e);
        }
    }

    @Test
    @DisplayName("should throw exception on null list field")
    void testNullListField() {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            assertThrows(IllegalArgumentException.class, () ->
                    writer.writeRow(Arrays.asList("John", null, "30"))
            );
        } catch (IOException e) {
            fail("Should not throw IOException", e);
        }
    }

    @Test
    @DisplayName("should flush without closing")
    void testFlush() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter writer = new CsvWriter(sw);
        writer.writeRow("John", "30");
        writer.flush();
        
        assertEquals("John,30\r\n", sw.toString());
        
        writer.close();
    }

    @Test
    @DisplayName("should throw exception when writing after close")
    void testWriteAfterClose() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter writer = new CsvWriter(sw);
        writer.close();
        
        assertThrows(IOException.class, () -> writer.writeRow("John", "30"));
    }

    @Test
    @DisplayName("should throw exception on flush after close")
    void testFlushAfterClose() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter writer = new CsvWriter(sw);
        writer.close();
        
        assertThrows(IOException.class, writer::flush);
    }

    @Test
    @DisplayName("should allow multiple close calls")
    void testMultipleClose() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter writer = new CsvWriter(sw);
        writer.close();
        writer.close();
    }

    @Test
    @DisplayName("should handle special characters at field boundaries")
    void testSpecialCharsAtBoundaries() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow(",John,", "30,");
        }
        
        assertEquals("\",John,\",\"30,\"\r\n", sw.toString());
    }

    @Test
    @DisplayName("should use default config when none provided")
    void testDefaultConfig() throws IOException {
        StringWriter sw1 = new StringWriter();
        StringWriter sw2 = new StringWriter();
        
        try (CsvWriter writer1 = new CsvWriter(sw1)) {
            writer1.writeRow("John,Doe", "30");
        }
        
        try (CsvWriter writer2 = new CsvWriter(sw2, CsvConfig.builder().build())) {
            writer2.writeRow("John,Doe", "30");
        }
        
        assertEquals(sw1.toString(), sw2.toString());
    }

    @Test
    @DisplayName("should handle long field values")
    void testLongFieldValue() throws IOException {
        StringWriter sw = new StringWriter();
        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longValue.append("A");
        }
        
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow(longValue.toString(), "30");
        }
        
        assertTrue(sw.toString().startsWith("AAAA"));
        assertTrue(sw.toString().contains(",30\r\n"));
    }

    @Test
    @DisplayName("should handle tab character in field")
    void testTabCharacterInField() throws IOException {
        StringWriter sw = new StringWriter();
        try (CsvWriter writer = new CsvWriter(sw)) {
            writer.writeRow("John\tDoe", "30");
        }
        
        assertEquals("John\tDoe,30\r\n", sw.toString());
    }

}
