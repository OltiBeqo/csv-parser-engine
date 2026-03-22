package com.csv.parser.examples;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import com.csv.parser.parser.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Example demonstrating custom delimiters and quote characters.
 * 
 * Shows how to:
 * - Parse CSV with custom delimiters (e.g., semicolon, pipe)
 * - Handle quoted fields
 * - Configure escape characters
 */
public class CustomDelimiterExample {

    public static void main(String[] args) throws IOException {
        // Example with semicolon delimiter
        String semiColonData = "name;description;price\n" +
                               "\"Apple\";\"Sweet, red fruit\";1.50\n" +
                               "\"Banana\";\"Yellow, soft fruit\";0.75";

        CsvConfig config = CsvConfig.builder()
                .delimiter(';')
                .quoteChar('"')
                .build();
        
        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        System.out.println("=== Semicolon-delimited CSV ===");
        Iterator<CsvRow> rows = parser.iterator(new StringReader(semiColonData));
        while (rows.hasNext()) {
            CsvRow row = rows.next();
            System.out.printf("Item: %s, Description: %s, Price: %s%n",
                    row.get(0), row.get(1), row.get(2));
        }

        // Example with pipe delimiter
        String pipeData = "id|name|status\n" +
                         "1|John Doe|Active\n" +
                         "2|Jane Smith|Inactive";

        CsvConfig pipeConfig = CsvConfig.builder()
                .delimiter('|')
                .build();
        
        CsvParser pipeParser = CsvParser.builder()
                .config(pipeConfig)
                .build();

        System.out.println("\n=== Pipe-delimited CSV ===");
        rows = pipeParser.iterator(new StringReader(pipeData));
        while (rows.hasNext()) {
            CsvRow row = rows.next();
            System.out.printf("ID: %s, Name: %s, Status: %s%n",
                    row.get(0), row.get(1), row.get(2));
        }
    }
}
