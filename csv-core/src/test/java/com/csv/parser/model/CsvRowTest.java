package com.csv.parser.model;

import com.csv.parser.exception.CsvException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsvRow Tests")
class CsvRowTest {

    @Test
    @DisplayName("should access field by index")
    void testGetByIndex() {
        CsvRow row = new CsvRow(Arrays.asList("John", "Doe", "30"), null);

        assertEquals("John", row.get(0));
        assertEquals("Doe", row.get(1));
        assertEquals("30", row.get(2));
    }

    @Test
    @DisplayName("should throw IndexOutOfBoundsException for invalid index")
    void testGetByIndexOutOfBounds() {
        CsvRow row = new CsvRow(Arrays.asList("John", "Doe"), null);

        assertThrows(IndexOutOfBoundsException.class, () -> row.get(5));
    }

    @Test
    @DisplayName("should access field by column name")
    void testGetByColumnName() {
        Map<String, Integer> headerIndex = new HashMap<>();
        headerIndex.put("Name", 0);
        headerIndex.put("LastName", 1);
        headerIndex.put("Age", 2);

        CsvRow row = new CsvRow(Arrays.asList("John", "Doe", "30"), headerIndex);

        assertEquals("John", row.get("Name"));
        assertEquals("Doe", row.get("LastName"));
        assertEquals("30", row.get("Age"));
    }

    @Test
    @DisplayName("should handle case-insensitive column names when ignoreCase is true")
    void testGetByColumnNameCaseInsensitive() {
        Map<String, Integer> headerIndex = new HashMap<>();
        headerIndex.put("Name", 0);
        headerIndex.put("Age", 1);

        CsvRow row = new CsvRow(Arrays.asList("John", "30"), headerIndex, true);

        assertEquals("John", row.get("name"));
        assertEquals("John", row.get("NAME"));
        assertEquals("John", row.get("Name"));
        assertEquals("30", row.get("age"));
    }

    @Test
    @DisplayName("should handle case-sensitive column names when ignoreCase is false")
    void testGetByColumnNameCaseSensitive() {
        Map<String, Integer> headerIndex = new HashMap<>();
        headerIndex.put("Name", 0);
        headerIndex.put("Age", 1);

        CsvRow row = new CsvRow(Arrays.asList("John", "30"), headerIndex, false);

        assertEquals("John", row.get("Name"));
        assertEquals("30", row.get("Age"));
        assertThrows(CsvException.class, () -> row.get("name"));
        assertThrows(CsvException.class, () -> row.get("NAME"));
    }

    @Test
    @DisplayName("should throw exception when accessing column name without headers")
    void testGetByColumnNameWithoutHeaders() {
        CsvRow row = new CsvRow(Arrays.asList("John", "Doe"), null);

        CsvException exception = assertThrows(CsvException.class, () -> row.get("Name"));
        assertTrue(exception.getMessage().contains("headers"));
    }

    @Test
    @DisplayName("should throw exception for non-existent column name")
    void testGetByNonExistentColumnName() {
        Map<String, Integer> headerIndex = new HashMap<>();
        headerIndex.put("Name", 0);

        CsvRow row = new CsvRow(Arrays.asList("John"), headerIndex);

        CsvException exception = assertThrows(CsvException.class, () -> row.get("Age"));
        assertTrue(exception.getMessage().contains("Column not found"));
    }

    @Test
    @DisplayName("should return immutable values list")
    void testValuesImmutable() {
        CsvRow row = new CsvRow(Arrays.asList("John", "Doe"), null);
        var values = row.values();

        assertThrows(UnsupportedOperationException.class, () ->
                values.add("Extra")
        );
    }

    @Test
    @DisplayName("should return correct row size")
    void testSize() {
        CsvRow row1 = new CsvRow(Arrays.asList("John", "Doe"), null);
        assertEquals(2, row1.size());

        CsvRow row2 = new CsvRow(Arrays.asList("A", "B", "C", "D", "E"), null);
        assertEquals(5, row2.size());

        CsvRow row3 = new CsvRow(Arrays.asList(), null);
        assertEquals(0, row3.size());
    }

    @Test
    @DisplayName("should handle empty row")
    void testEmptyRow() {
        CsvRow row = new CsvRow(Arrays.asList(), null);
        assertEquals(0, row.size());
        assertEquals("[]", row.toString());
    }

    @Test
    @DisplayName("should handle row with empty strings")
    void testRowWithEmptyStrings() {
        CsvRow row = new CsvRow(Arrays.asList("", "", ""), null);
        assertEquals(3, row.size());
        assertEquals("", row.get(0));
        assertEquals("", row.get(1));
    }

    @Test
    @DisplayName("should handle special characters in values")
    void testRowWithSpecialCharacters() {
        CsvRow row = new CsvRow(Arrays.asList("John,Doe", "New\"York", "Line\nBreak"), null);
        assertEquals("John,Doe", row.get(0));
        assertEquals("New\"York", row.get(1));
        assertEquals("Line\nBreak", row.get(2));
    }

    @Test
    @DisplayName("should generate toString representation")
    void testToString() {
        CsvRow row = new CsvRow(Arrays.asList("John", "Doe", "30"), null);
        assertEquals("[John, Doe, 30]", row.toString());
    }

}
