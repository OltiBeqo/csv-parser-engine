package com.csv.parser.parser;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.model.CsvRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Row Mapper Tests")
class CsvRowMapperTest {

    private static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    @DisplayName("should map rows to objects")
    void testMapRows() {
        String csv = "John,30\nJane,25\n";
        CsvParser parser = CsvParser.builder().build();

        CsvRowMapper<Person> mapper = row -> 
                new Person(row.get(0), Integer.parseInt(row.get(1)));

        List<Person> people = new ArrayList<>();
        var iterator = parser.mapIterator(new StringReader(csv), mapper);
        iterator.forEachRemaining(people::add);

        assertEquals(2, people.size());
        assertEquals("John", people.get(0).name);
        assertEquals(30, people.get(0).age);
        assertEquals("Jane", people.get(1).name);
        assertEquals(25, people.get(1).age);
    }

    @Test
    @DisplayName("should map with header-based access")
    void testMapWithHeaders() {
        String csv = "Name,Age\nJohn,30\nJane,25\n";
        CsvConfig config = CsvConfig.builder().hasHeader(true).build();
        CsvParser parser = CsvParser.builder().config(config).build();

        CsvRowMapper<Person> mapper = row -> 
                new Person(row.get("Name"), Integer.parseInt(row.get("Age")));

        List<Person> people = new ArrayList<>();
        var iterator = parser.mapIterator(new StringReader(csv), mapper);
        iterator.forEachRemaining(people::add);

        assertEquals(2, people.size());
        assertEquals("John", people.get(0).name);
        assertEquals(30, people.get(0).age);
    }

    @Test
    @DisplayName("should throw exception for null row iterator")
    void testNullRowIterator() {
        CsvRowMapper<String> mapper = row -> row.get(0);
        CsvParser parser = CsvParser.builder().build();

        assertThrows(IllegalArgumentException.class, () ->
                parser.mapIterator((java.util.Iterator<CsvRow>) null, mapper)
        );
    }

    @Test
    @DisplayName("should handle mapper that transforms to different types")
    void testMapToDifferentTypes() {
        String csv = "10,20,30\n40,50,60\n";
        CsvParser parser = CsvParser.builder().build();

        CsvRowMapper<Integer> mapper = row -> 
                Integer.parseInt(row.get(0)) + Integer.parseInt(row.get(1));

        List<Integer> sums = new ArrayList<>();
        var rowIterator = parser.iterator(new StringReader(csv));
        var iterator = parser.mapIterator(rowIterator, mapper);
        iterator.forEachRemaining(sums::add);

        assertEquals(2, sums.size());
        assertEquals(30, sums.get(0));
        assertEquals(90, sums.get(1));
    }

}
