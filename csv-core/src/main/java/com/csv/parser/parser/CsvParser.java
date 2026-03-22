package com.csv.parser.parser;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.exception.ExceptionMessages;
import com.csv.parser.model.CsvRow;
import com.csv.parser.tokenizer.BufferedCsvTokenizer;
import com.csv.parser.tokenizer.CsvTokenizer;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * RFC 4180-compliant CSV parser for reading CSV data from files or readers.
 * 
 * <p>Supports both iterator and stream-based parsing with lazy evaluation.
 * The parser handles quoted fields, escaped quotes, custom delimiters, and headers.</p>
 * 
 * <p>Usage example:
 * <pre>{@code
 * CsvParser parser = CsvParser.builder().build();
 * try (Iterator<CsvRow> rows = parser.iterator(file)) {
 *     while (rows.hasNext()) {
 *         CsvRow row = rows.next();
 *         System.out.println(row.get(0));
 *     }
 * }
 * }</pre>
 * </p>
 */
public class CsvParser {

    private final CsvConfig config;

    private CsvParser(Builder builder) {
        this.config = builder.config;
    }

    /**
     * Creates a new CsvParser builder with default configuration.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parse CSV data from a file as an iterator.
     * 
     * <p>The returned iterator manages the underlying FileReader.
     * If the iterator implements Closeable, the file will be properly closed.</p>
     * 
     * @param file The CSV file to parse
     * @return An iterator over the CSV rows
     * @throws IllegalArgumentException if file is null
     * @throws IOException if the file cannot be read
     */
    public Iterator<CsvRow> iterator(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(ExceptionMessages.FILE_CANNOT_BE_NULL);
        }
        FileReader reader = new FileReader(file);
        try {
            return new CloseableIteratorWrapper(iterator(reader), reader);
        } catch (Exception e) {
            reader.close();
            throw e;
        }
    }

    /**
     * Parse CSV data from a reader as an iterator.
     * 
     * <p>The caller is responsible for closing the reader if needed.</p>
     * 
     * @param reader The source of CSV data
     * @return An iterator over the CSV rows
     * @throws IllegalArgumentException if reader is null
     */
    public Iterator<CsvRow> iterator(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException(ExceptionMessages.READER_CANNOT_BE_NULL);
        }
        CsvTokenizer tokenizer = new BufferedCsvTokenizer(reader, config.getBufferSize());
        return new CsvStateMachine(tokenizer, config);
    }

    /**
     * Parse CSV data from a file as a stream.
     * 
     * <p>The stream must be closed to release the underlying file resource.</p>
     * 
     * @param file The CSV file to parse
     * @return A stream of CSV rows
     * @throws IllegalArgumentException if file is null
     * @throws IOException if the file cannot be read
     */
    public Stream<CsvRow> stream(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(ExceptionMessages.FILE_CANNOT_BE_NULL);
        }
        FileReader reader = new FileReader(file);
        try {
            Iterator<CsvRow> iterator = iterator(reader);
            return StreamSupport.stream(
                    ((Iterable<CsvRow>) () -> iterator).spliterator(),
                    false
            ).onClose(() -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(ExceptionMessages.ERROR_CLOSING_READER, e);
                }
            });
        } catch (Exception e) {
            reader.close();
            throw e;
        }
    }

    /**
     * Parse CSV data from a reader as a stream.
     * 
     * @param reader The source of CSV data
     * @return A stream of CSV rows
     * @throws IllegalArgumentException if reader is null
     */
    public Stream<CsvRow> stream(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException(ExceptionMessages.READER_CANNOT_BE_NULL);
        }
        Iterator<CsvRow> it = iterator(reader);
        return StreamSupport.stream(
                ((Iterable<CsvRow>) () -> it).spliterator(),
                false
        );
    }

    /**
     * Map CSV rows from a file to objects of type T using the provided mapper.
     * 
     * <p>The returned iterator manages the underlying FileReader.</p>
     * 
     * @param <T> The type of objects produced by the mapper
     * @param file The CSV file to parse
     * @param mapper Function to transform each CSV row to the target type
     * @return An iterator over the mapped objects
     * @throws IllegalArgumentException if file or mapper is null
     * @throws IOException if the file cannot be read
     */
    public <T> Iterator<T> mapIterator(File file, CsvRowMapper<T> mapper) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(ExceptionMessages.FILE_CANNOT_BE_NULL);
        }
        if (mapper == null) {
            throw new IllegalArgumentException(ExceptionMessages.MAPPER_CANNOT_BE_NULL);
        }
        FileReader reader = new FileReader(file);
        try {
            return new CloseableMappingIteratorWrapper<>(mapIterator(iterator(reader), mapper), reader);
        } catch (Exception e) {
            reader.close();
            throw e;
        }
    }

    /**
     * Map CSV rows from a reader to objects of type T using the provided mapper.
     * 
     * @param <T> The type of objects produced by the mapper
     * @param reader The source of CSV data
     * @param mapper Function to transform each CSV row to the target type
     * @return An iterator over the mapped objects
     * @throws IllegalArgumentException if reader or mapper is null
     */
    public <T> Iterator<T> mapIterator(Reader reader, CsvRowMapper<T> mapper) {
        if (reader == null) {
            throw new IllegalArgumentException(ExceptionMessages.READER_CANNOT_BE_NULL);
        }
        if (mapper == null) {
            throw new IllegalArgumentException(ExceptionMessages.MAPPER_CANNOT_BE_NULL);
        }
        return mapIterator(iterator(reader), mapper);
    }

    /**
     * Apply a mapper function to an existing row iterator.
     * 
     * @param <T> The type of objects produced by the mapper
     * @param rowIterator Iterator over CSV rows
     * @param mapper Function to transform each CSV row to the target type
     * @return An iterator over the mapped objects
     * @throws IllegalArgumentException if rowIterator or mapper is null
     */
    public <T> Iterator<T> mapIterator(Iterator<CsvRow> rowIterator, CsvRowMapper<T> mapper) {
        if (rowIterator == null) {
            throw new IllegalArgumentException(ExceptionMessages.ROW_ITERATOR_CANNOT_BE_NULL);
        }
        if (mapper == null) {
            throw new IllegalArgumentException(ExceptionMessages.MAPPER_CANNOT_BE_NULL);
        }
        return new MappingIterator<>(rowIterator, mapper);
    }

    /**
     * Map CSV rows from a file to objects of type T as a stream.
     * 
     * <p>The stream must be closed to release the underlying file resource.</p>
     * 
     * @param <T> The type of objects produced by the mapper
     * @param file The CSV file to parse
     * @param mapper Function to transform each CSV row to the target type
     * @return A stream of mapped objects
     * @throws IllegalArgumentException if file or mapper is null
     * @throws IOException if the file cannot be read
     */
    public <T> Stream<T> mapStream(File file, CsvRowMapper<T> mapper) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(ExceptionMessages.FILE_CANNOT_BE_NULL);
        }
        if (mapper == null) {
            throw new IllegalArgumentException(ExceptionMessages.MAPPER_CANNOT_BE_NULL);
        }
        FileReader reader = new FileReader(file);
        try {
            Stream<CsvRow> rowStream = StreamSupport.stream(
                    ((Iterable<CsvRow>) () -> iterator(reader)).spliterator(),
                    false
            ).onClose(() -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(ExceptionMessages.ERROR_CLOSING_READER, e);
                }
            });
            return rowStream.map(mapper::map);
        } catch (Exception e) {
            reader.close();
            throw e;
        }
    }

    /**
     * Map CSV rows from a reader to objects of type T as a stream.
     * 
     * @param <T> The type of objects produced by the mapper
     * @param reader The source of CSV data
     * @param mapper Function to transform each CSV row to the target type
     * @return A stream of mapped objects
     * @throws IllegalArgumentException if reader or mapper is null
     */
    public <T> Stream<T> mapStream(Reader reader, CsvRowMapper<T> mapper) {
        if (reader == null) {
            throw new IllegalArgumentException(ExceptionMessages.READER_CANNOT_BE_NULL);
        }
        if (mapper == null) {
            throw new IllegalArgumentException(ExceptionMessages.MAPPER_CANNOT_BE_NULL);
        }
        return stream(reader).map(mapper::map);
    }

    /**
     * Wrapper for mapping iterator
     */
    private static class MappingIterator<T> implements Iterator<T> {
        private final Iterator<CsvRow> delegate;
        private final CsvRowMapper<T> mapper;

        MappingIterator(Iterator<CsvRow> delegate, CsvRowMapper<T> mapper) {
            this.delegate = delegate;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return mapper.map(delegate.next());
        }
    }

    /**
     * Wrapper for closeable mapping iterator to manage reader lifecycle
     */
    private static class CloseableMappingIteratorWrapper<T> implements Iterator<T>, Closeable {
        private final Iterator<T> delegate;
        private final Reader reader;

        CloseableMappingIteratorWrapper(Iterator<T> delegate, Reader reader) {
            this.delegate = delegate;
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public T next() {
            return delegate.next();
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }

    /**
     * Wrapper for closeable iterator to manage reader lifecycle
     */
    private static class CloseableIteratorWrapper implements Iterator<CsvRow>, Closeable {
        private final Iterator<CsvRow> delegate;
        private final Reader reader;

        CloseableIteratorWrapper(Iterator<CsvRow> delegate, Reader reader) {
            this.delegate = delegate;
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public CsvRow next() {
            return delegate.next();
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }

    /**
     * Builder for CsvParser with fluent configuration API.
     */
    public static class Builder {

        private CsvConfig config = CsvConfig.builder().build();

        /**
         * Set the CSV configuration.
         * @param config The CSV configuration
         * @return This builder for method chaining
         */
        public Builder config(CsvConfig config) {
            this.config = config;
            return this;
        }

        /**
         * Build and return the CsvParser instance.
         * @return A new CsvParser configured with the builder's settings
         */
        public CsvParser build() {
            return new CsvParser(this);
        }
    }

}
