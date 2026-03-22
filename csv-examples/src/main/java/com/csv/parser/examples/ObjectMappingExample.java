package com.csv.parser.examples;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import com.csv.parser.parser.CsvRowMapper;
import com.csv.parser.parser.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Example demonstrating object mapping from CSV rows.
 * 
 * Shows how to:
 * - Map CSV rows to custom objects
 * - Use CsvRowMapper for type-safe conversions
 * - Handle data transformations
 */
public class ObjectMappingExample {

    /**
     * Simple Person class to demonstrate mapping.
     */
    public static class Person {
        public final String name;
        public final int age;
        public final String city;

        public Person(String name, int age, String city) {
            this.name = name;
            this.age = age;
            this.city = city;
        }

        @Override
        public String toString() {
            return String.format("Person{name='%s', age=%d, city='%s'}", name, age, city);
        }
    }

    public static void main(String[] args) throws IOException {
        String csvData = "name,age,city\n" +
                         "John,30,New York\n" +
                         "Jane,25,San Francisco\n" +
                         "Bob,35,Chicago";

        // Define a mapper function that converts CsvRow to Person
        CsvRowMapper<Person> personMapper = row -> new Person(
                row.get("name"),
                Integer.parseInt(row.get("age")),
                row.get("city")
        );

        CsvConfig config = CsvConfig.builder()
                .hasHeader(true)
                .build();

        CsvParser parser = CsvParser.builder()
                .config(config)
                .build();

        Iterator<Person> people = parser.mapIterator(new StringReader(csvData), personMapper);
        System.out.println("=== Mapped Objects ===");
        while (people.hasNext()) {
            System.out.println(people.next());
        }
    }
}
