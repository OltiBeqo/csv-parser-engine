package com.csv.parser.parser;

import com.csv.parser.model.CsvRow;

/**
 * Functional interface for mapping CSV rows to domain objects.
 * 
 * <p>CsvRowMapper is a functional interface that converts a {@link CsvRow} into an arbitrary
 * type T. It is commonly used with stream APIs and forEach methods to transform raw CSV data
 * into application-specific objects.
 * 
 * <p>This interface enables flexible data transformation without requiring a custom parser.
 * The mapping function receives a CsvRow with access to all fields (by index or by column name)
 * and can perform any necessary transformations, validation, or object construction.
 * 
 * <p>Example usage with a custom object:
 * <pre>{@code
 * public class Person {
 *     private String name;
 *     private int age;
 *     
 *     // Constructor and getters...
 * }
 * 
 * CsvRowMapper<Person> personMapper = row -> 
 *     new Person(row.get("name"), Integer.parseInt(row.get("age")));
 * }</pre>
 * 
 * <p>If the mapping operation requires the CSV to have headers, ensure that the CSV parser
 * is configured with {@link io.csvparser.config.CsvConfig.Builder#hasHeader(boolean)} set to true.
 * 
 * @param <T> the type of object produced by this mapper
 * @see CsvRow
 */
@FunctionalInterface
public interface CsvRowMapper<T> {

    /**
     * Maps a CSV row to an object of type T.
     * 
     * <p>This method is called for each row in the CSV file during parsing. The row contains
     * all field values and, if headers are present, allows access by column name.
     * 
     * <p>The mapping function may throw any checked or unchecked exception, which will propagate
     * to the caller of the parser.
     * 
     * @param row the CSV row to map
     * @return the mapped object of type T
     * @throws Exception if mapping fails (specific exception type depends on implementation)
     */
    T map(CsvRow row);

}