package com.csv.parser.examples;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import com.csv.parser.parser.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

/**
 * Example demonstrating stream-based CSV parsing.
 * 
 * Shows how to:
 * - Use streams for functional-style processing
 * - Filter rows with predicates
 * - Transform data using map operations
 * - Collect results
 */
public class StreamParsingExample {

    public static void main(String[] args) throws IOException {
        String csvData = "name,age,city\n" +
                         "John,30,New York\n" +
                         "Jane,25,San Francisco\n" +
                         "Bob,35,Chicago\n" +
                         "Alice,28,Boston";

        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();
        
        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        try (Stream<CsvRow> rows = parser.stream(new StringReader(csvData))) {
            System.out.println("=== All rows ===");
            rows.forEach(row -> System.out.printf("%s (%s) - %s%n",
                    row.get("name"), row.get("age"), row.get("city")));
        }

        // Filter rows where age >= 30
        try (Stream<CsvRow> rows = parser.stream(new StringReader(csvData))) {
            System.out.println("\n=== People aged 30+ ===");
            rows.filter(row -> Integer.parseInt(row.get("age")) >= 30)
                .forEach(row -> System.out.printf("%s (%s) - %s%n",
                        row.get("name"), row.get("age"), row.get("city")));
        }

        // Map to custom format
        try (Stream<CsvRow> rows = parser.stream(new StringReader(csvData))) {
            System.out.println("\n=== Custom format ===");
            rows.map(row -> String.format("%s lives in %s", 
                    row.get("name"), row.get("city")))
                .forEach(System.out::println);
        }

        // Count rows
        try (Stream<CsvRow> rows = parser.stream(new StringReader(csvData))) {
            System.out.println("\n=== Total people ===");
            long count = rows.count();
            System.out.printf("Total: %d%n", count);
        }
    }
}
