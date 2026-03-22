package com.csv.parser.examples;

import com.csv.parser.model.CsvRow;
import com.csv.parser.parser.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Basic example demonstrating simple CSV parsing.
 * 
 * Shows how to:
 * - Parse CSV from a string
 * - Iterate through rows
 * - Access columns by index
 */
public class BasicParsingExample {

    public static void main(String[] args) throws IOException {
        String csvData = "name,age,city\n" +
                         "John,30,New York\n" +
                         "Jane,25,San Francisco\n" +
                         "Bob,35,Chicago";

        CsvParser parser = CsvParser.builder().build();

        Iterator<CsvRow> rows = parser.iterator(new StringReader(csvData));
        while (rows.hasNext()) {
            CsvRow row = rows.next();
            System.out.printf("Name: %s, Age: %s, City: %s%n",
                    row.get(0), row.get(1), row.get(2));
        }
    }
}
