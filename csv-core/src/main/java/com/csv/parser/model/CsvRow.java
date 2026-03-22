package com.csv.parser.model;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.exception.CsvException;
import com.csv.parser.exception.ExceptionMessages;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable representation of a single row in a CSV file.
 * 
 * <p>CsvRow encapsulates the field values of a CSV row along with an optional mapping of column
 * names to their indices. This allows rows to be accessed either by index (for all rows) or by
 * column name (for rows with headers).
 * 
 * <p>Values are stored in an immutable list to prevent accidental modification. The header index
 * mapping is optional and only available for CSV files that contain headers.
 * 
 * <p>This class is thread-safe as all internal state is immutable.
 * 
 * @see CsvRowMapper
 */
public class CsvRow {

    private final List<String> values;
    private final Map<String, Integer> headerIndex;
    private final Map<String, Integer> lowerCaseHeaderIndex;
    private final boolean ignoreCase;

    public CsvRow(List<String> values, Map<String, Integer> headerIndex) {
        this(values, headerIndex, true);
    }

    public CsvRow(List<String> values, Map<String, Integer> headerIndex, boolean ignoreCase) {
        this.values = Collections.unmodifiableList(values);
        this.headerIndex = headerIndex;
        this.ignoreCase = ignoreCase;
        this.lowerCaseHeaderIndex = buildLowerCaseIndex(headerIndex, ignoreCase);
    }

    /**
     * Retrieves the field value at the specified column index.
     * 
     * @param index the zero-based column index
     * @return the field value at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public String get(int index) {
        return values.get(index);
    }

    /**
     * Retrieves the field value for the specified column name.
     * 
     * <p>This method requires that the CSV file contains headers, which must be configured
     * via {@link io.csvparser.config.CsvConfig.Builder#hasHeader(boolean)} when creating
     * the parser.
     * 
     * <p>Column name lookup is case-insensitive by default, but this can be controlled via
     * {@link io.csvparser.config.CsvConfig.Builder#ignoreCase(boolean)}.
     * 
     * @param columnName the name of the column to retrieve
     * @return the field value for the specified column name
     * @throws CsvException if the CSV does not contain headers
     * @throws CsvException if the specified column name does not exist
     */
    public String get(String columnName) {

        if (headerIndex == null) {
            throw new CsvException(ExceptionMessages.CSV_DOES_NOT_CONTAIN_HEADERS);
        }

        Integer index = ignoreCase 
            ? lowerCaseHeaderIndex.get(columnName.toLowerCase())
            : headerIndex.get(columnName);

        if (index == null) {
            throw new CsvException(ExceptionMessages.COLUMN_NOT_FOUND + columnName);
        }

        return values.get(index);
    }

    private static Map<String, Integer> buildLowerCaseIndex(
            Map<String, Integer> headerIndex, boolean ignoreCase) {
        if (!ignoreCase || headerIndex == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Integer> lowerCaseIndex = new HashMap<>();
        for (Map.Entry<String, Integer> entry : headerIndex.entrySet()) {
            lowerCaseIndex.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return Collections.unmodifiableMap(lowerCaseIndex);
    }

    /**
     * Returns an immutable list of all field values in this row.
     * 
     * @return an unmodifiable list of field values in order
     */
    public List<String> values() {
        return values;
    }

    /**
     * Returns the number of fields in this row.
     * 
     * @return the number of columns in this row
     */
    public int size() {
        return values.size();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
