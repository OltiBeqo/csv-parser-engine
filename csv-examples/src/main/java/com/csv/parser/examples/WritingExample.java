package com.csv.parser.examples;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.writer.CsvWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Example demonstrating CSV writing functionality.
 * 
 * Shows how to:
 * - Write CSV data to a string/file
 * - Configure custom delimiters and quote characters
 * - Handle field quoting and escaping
 */
public class WritingExample {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Basic CSV Writing ===");
        writeBasicCsv();

        System.out.println("\n=== Writing with Headers ===");
        writeWithHeaders();

        System.out.println("\n=== Custom Delimiter ===");
        writeWithCustomDelimiter();

        System.out.println("\n=== Fields with Special Characters ===");
        writeWithSpecialCharacters();
    }

    private static void writeBasicCsv() throws IOException {
        StringWriter output = new StringWriter();
        CsvWriter writer = new CsvWriter(output);

        // Write rows as varargs
        writer.writeRow("John", "30", "New York");
        writer.writeRow("Jane", "25", "San Francisco");
        writer.writeRow("Bob", "35", "Chicago");

        writer.close();
        System.out.println(output.toString());
    }

    private static void writeWithHeaders() throws IOException {
        StringWriter output = new StringWriter();
        
        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();
        
        CsvWriter writer = new CsvWriter(output, config);

        // Write header
        List<String> headers = Arrays.asList("Name", "Age", "City");
        writer.writeHeader(headers);

        // Write data rows
        writer.writeRow("John", "30", "New York");
        writer.writeRow("Jane", "25", "San Francisco");
        writer.writeRow("Bob", "35", "Chicago");

        writer.close();
        System.out.println(output.toString());
    }

    private static void writeWithCustomDelimiter() throws IOException {
        StringWriter output = new StringWriter();
        
        CsvConfig config = CsvConfig.builder()
                .delimiter(';')
                .build();
        
        CsvWriter writer = new CsvWriter(output, config);

        writer.writeRow("Product", "Price", "Stock");
        writer.writeRow("Apple", "1.50", "100");
        writer.writeRow("Banana", "0.75", "200");

        writer.close();
        System.out.println(output.toString());
    }

    private static void writeWithSpecialCharacters() throws IOException {
        StringWriter output = new StringWriter();
        CsvWriter writer = new CsvWriter(output);

        // Fields with commas, quotes, and newlines are automatically quoted
        writer.writeRow("John Smith", "Sales, Marketing", "New York City");
        writer.writeRow("Jane \"Doc\" Doe", "Senior Developer", "San Francisco");
        writer.writeRow("Bob", "Notes:\nLine 1\nLine 2", "Chicago");

        writer.close();
        System.out.println(output.toString());
    }
}
