package com.csv.parser.examples;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import com.csv.parser.parser.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Example demonstrating header-based column access.
 * 
 * Shows how to:
 * - Parse CSV with headers
 * - Access columns by name (case-insensitive)
 * - Use custom configurations
 */
public class HeaderAccessExample {

    public static void main(String[] args) throws IOException {
        String csvData = "Name,Age,City\n" +
                         "John,30,New York\n" +
                         "Jane,25,San Francisco\n" +
                         "Bob,35,Chicago";

        // Create parser with header configuration
        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();
        
        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        Iterator<CsvRow> rows = parser.iterator(new StringReader(csvData));
        while (rows.hasNext()) {
            CsvRow row = rows.next();
            
            // Access columns by header name (case-insensitive)
            System.out.printf("Name: %s, Age: %s, City: %s%n",
                    row.get("name"),      // "name" matches "Name" header
                    row.get("age"),       // "age" matches "Age" header
                    row.get("city"));     // "city" matches "City" header
        }
    }
}
