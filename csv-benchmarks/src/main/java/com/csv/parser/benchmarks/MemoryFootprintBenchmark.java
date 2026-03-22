package com.csv.parser.benchmarks;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.parser.CsvParser;
import com.csv.parser.model.CsvRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

/**
 * Memory footprint and allocation benchmarks.
 * These benchmarks measure object allocation rates and memory pressure.
 */
@Fork(value = 1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class MemoryFootprintBenchmark {

    private static final int ROWS = 50_000;
    private String csvData;

    @Setup
    public void setup() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,name,email,description,value\n");
        
        for (int i = 0; i < ROWS; i++) {
            sb.append(i).append(",")
              .append("User_").append(i).append(",")
              .append("user").append(i).append("@test.com,")
              .append("\"This is a longer description with, commas and special chars: !@#$%\",")
              .append(String.format("%.2f", Math.random() * 10000)).append("\n");
        }
        
        csvData = sb.toString();
    }

    /**
     * Our parser: typical throughput test
     */
    @Benchmark
    public void ourParser_Throughput(Blackhole bh) throws IOException {
        CsvParser parser = CsvParser.builder().build();
        var iterator = parser.iterator(new StringReader(csvData));
        while (iterator.hasNext()) {
            CsvRow row = iterator.next();
            bh.consume(row);
        }
    }

    /**
     * Apache Commons: typical throughput test
     */
    @Benchmark
    public void apacheCommons_Throughput(Blackhole bh) throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.parse(new StringReader(csvData))) {
            for (var record : parser) {
                bh.consume(record);
            }
        }
    }

    /**
     * Our parser with field access
     */
    @Benchmark
    public void ourParser_WithFieldAccess(Blackhole bh) throws IOException {
        CsvParser parser = CsvParser.builder()
                .config(CsvConfig.builder().hasHeader(true).build())
                .build();
        var iterator = parser.iterator(new StringReader(csvData));
        while (iterator.hasNext()) {
            CsvRow row = iterator.next();
            bh.consume(row.get("name"));
            bh.consume(row.get("email"));
            bh.consume(row.get("value"));
        }
    }

    /**
     * Apache Commons with field access
     */
    @Benchmark
    public void apacheCommons_WithFieldAccess(Blackhole bh) throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(new StringReader(csvData))) {
            for (var record : parser) {
                bh.consume(record.get("name"));
                bh.consume(record.get("email"));
                bh.consume(record.get("value"));
            }
        }
    }
}
