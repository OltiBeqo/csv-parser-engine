package com.csv.parser.benchmarks;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.parser.CsvParser;
import com.csv.parser.model.CsvRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmarks comparing CSV parser implementations.
 * 
 * Run with: mvn clean package && java -jar target/benchmarks.jar
 */
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class CsvParserBenchmark {

    private static final int ROWS = 100_000;
    private String smallDataset;
    private String largeDataset;
    private byte[] largeDatasetBytes;

    @Setup
    public void setup() throws IOException {
        smallDataset = generateCsvData(1000);
        largeDataset = generateCsvData(ROWS);
        largeDatasetBytes = largeDataset.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generate CSV data with specified number of rows
     */
    private String generateCsvData(int rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,name,email,phone,country,city,address\n");
        
        for (int i = 0; i < rows; i++) {
            sb.append(i).append(",")
              .append("User").append(i).append(",")
              .append("user").append(i).append("@example.com,")
              .append("+1-555-").append(String.format("%04d", i % 10000)).append(",")
              .append("USA,")
              .append("City").append(i % 50).append(",")
              .append(i).append(" Main Street\n");
        }
        
        return sb.toString();
    }

    // ==================== CSV Parser Engine Benchmarks ====================

    @Benchmark
    public long ourParser_SmallDataset() throws IOException {
        CsvParser parser = CsvParser.builder().build();
        var iterator = parser.iterator(new StringReader(smallDataset));
        long count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    @Benchmark
    public long ourParser_LargeDataset() throws IOException {
        CsvParser parser = CsvParser.builder().build();
        var iterator = parser.iterator(new StringReader(largeDataset));
        long count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    @Benchmark
    public long ourParser_WithHeaderAccess() throws IOException {
        CsvParser parser = CsvParser.builder()
                .config(CsvConfig.builder().hasHeader(true).build())
                .build();
        var iterator = parser.iterator(new StringReader(largeDataset));
        long count = 0;
        while (iterator.hasNext()) {
            CsvRow row = iterator.next();
            // Access by header name (second column)
            row.get("name");
            count++;
        }
        return count;
    }

    // ==================== Apache Commons CSV Benchmarks ====================

    @Benchmark
    public long apacheCommons_SmallDataset() throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(new StringReader(smallDataset))) {
            long count = 0;
            for (var record : parser) {
                count++;
            }
            return count;
        }
    }

    @Benchmark
    public long apacheCommons_LargeDataset() throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(new StringReader(largeDataset))) {
            long count = 0;
            for (var record : parser) {
                count++;
            }
            return count;
        }
    }

    @Benchmark
    public long apacheCommons_WithHeaderAccess() throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(new StringReader(largeDataset))) {
            long count = 0;
            for (var record : parser) {
                // Access by header name
                record.get("name");
                count++;
            }
            return count;
        }
    }

    // ==================== Memory Efficiency Benchmarks ====================

    @Benchmark
    @Fork(value = 1, jvmArgs = "-Xmx512m")
    public long memoryTest_OurParser() throws IOException {
        CsvParser parser = CsvParser.builder().build();
        var iterator = parser.iterator(new StringReader(largeDataset));
        long count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    @Benchmark
    @Fork(value = 1, jvmArgs = "-Xmx512m")
    public long memoryTest_ApacheCommons() throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.parse(new StringReader(largeDataset))) {
            long count = 0;
            for (var record : parser) {
                count++;
            }
            return count;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CsvParserBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
