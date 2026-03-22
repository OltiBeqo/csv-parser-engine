package com.csv.parser.examples;

import com.csv.parser.exception.CsvException;
import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import com.csv.parser.parser.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Example demonstrating error handling in CSV parsing.
 * 
 * Shows how to:
 * - Handle parsing exceptions
 * - Validate CSV data
 * - Implement error recovery strategies
 */
public class ErrorHandlingExample {

    public static void main(String[] args) {
        System.out.println("=== Basic Error Handling ===");
        basicErrorHandling();

        System.out.println("\n=== Validation Error Handling ===");
        validationErrorHandling();

        System.out.println("\n=== Graceful Degradation ===");
        gracefulDegradation();
    }

    private static void basicErrorHandling() {
        String malformedCsv = "name,age,city\n" +
                              "John,30,New York\n" +
                              "Jane,not_a_number,San Francisco";

        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();

        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        Iterator<CsvRow> rows = parser.iterator(new StringReader(malformedCsv));
        int rowNumber = 0;
        while (rows.hasNext()) {
            rowNumber++;
            CsvRow row = rows.next();
            
            try {
                int age = Integer.parseInt(row.get("age"));
                System.out.printf("Row %d: %s is %d years old%n", 
                        rowNumber, row.get("name"), age);
            } catch (NumberFormatException e) {
                System.err.printf("Row %d: Invalid age value '%s' for %s%n",
                        rowNumber, row.get("age"), row.get("name"));
            }
        }
    }

    private static void validationErrorHandling() {
        String incompleteData = "name,age,city\n" +
                                "John,30\n" +  // Missing city
                                "Jane,25,San Francisco";

        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();

        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        Iterator<CsvRow> rows = parser.iterator(new StringReader(incompleteData));
        int rowNumber = 0;
        while (rows.hasNext()) {
            rowNumber++;
            CsvRow row = rows.next();
            
            // Validate row has all expected columns
            if (row.size() < 3) {
                System.err.printf("Row %d: Expected 3 columns but got %d%n",
                        rowNumber, row.size());
                continue;
            }

            System.out.printf("Row %d: %s, %s, %s%n",
                    rowNumber, row.get(0), row.get(1), row.get(2));
        }
    }

    private static void gracefulDegradation() {
        String csvWithBlanks = "name,email,phone\n" +
                               "John,john@example.com,555-1234\n" +
                               "Jane,,555-5678\n" +  // Missing email
                               "Bob,bob@example.com,";  // Missing phone

        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();

        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        Iterator<CsvRow> rows = parser.iterator(new StringReader(csvWithBlanks));
        int rowNumber = 0;
        while (rows.hasNext()) {
            rowNumber++;
            CsvRow row = rows.next();
            
            String name = row.get("name");
            String email = row.get("email");
            String phone = row.get("phone");
            
            // Use default values for missing data
            email = email != null && !email.isEmpty() ? email : "[no email]";
            phone = phone != null && !phone.isEmpty() ? phone : "[no phone]";
            
            System.out.printf("Row %d: %s - %s, %s%n",
                    rowNumber, name, email, phone);
        }
    }
}
