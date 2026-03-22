package com.csv.parser.writer;

import com.csv.parser.config.CsvConfig;
import com.csv.parser.exception.ExceptionMessages;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
/**
 * RFC 4180-compliant CSV writer for producing properly formatted CSV output.
 * 
 * <p>CsvWriter provides methods to write CSV data with proper escaping, quoting, and line ending
 * handling as specified in RFC 4180. It ensures that:
 * <ul>
 *   <li>Fields are properly quoted when they contain delimiters, quotes, or newlines</li>
 *   <li>Quote characters within quoted fields are escaped according to the configured escape character</li>
 *   <li>Lines are terminated with CRLF (\r\n) for RFC 4180 compliance</li>
 *   <li>The writer is resource-safe and implements AutoCloseable</li>
 * </ul>
 * 
 * <p>The writer uses a configurable {@link CsvConfig} to control delimiters, quote characters, and
 * other formatting parameters. If no configuration is provided, default RFC 4180 settings are used.
 * 
 * <p>Example usage:
 * <pre>{@code
 * try (CsvWriter writer = new CsvWriter(new FileWriter("output.csv"))) {
 *     writer.writeHeader("Name", "Age", "City");
 *     writer.writeRow("John Doe", "30", "New York");
 *     writer.writeRow("Jane, Smith", "25", "San Francisco");  // Comma in field triggers quoting
 * }
 * }</pre>
 * 
 * <p>This class is not thread-safe. If multiple threads need to write CSV data, external
 * synchronization is required.
 * 
 * @see CsvConfig
 */
public class CsvWriter implements AutoCloseable {

    private final Writer writer;
    private final CsvConfig config;
    private boolean closed = false;

    public CsvWriter(Writer writer, CsvConfig config) {
        this.writer = writer;
        this.config = config;
    }

    public CsvWriter(Writer writer) {
        this(writer, CsvConfig.builder().build());
    }

    /**
     * Writes a row of values to CSV with delimiter and newline handling.
     * 
     * <p>Each value is written as a field with proper quoting and escaping applied. Fields
     * are separated by the configured delimiter character, and the row is terminated with CRLF.
     * 
     * @param values the field values to write
     * @throws IOException if an I/O error occurs while writing
     * @throws IllegalStateException if the writer has been closed
     * @throws IllegalArgumentException if any field value is null
     */
    public void writeRow(String... values) throws IOException {
        checkNotClosed();
        writeRow(List.of(values));
    }

    /**
     * Writes a row of values from a list to CSV with delimiter and newline handling.
     * 
     * <p>Each value is written as a field with proper quoting and escaping applied. Fields
     * are separated by the configured delimiter character, and the row is terminated with CRLF.
     * 
     * @param values the list of field values to write
     * @throws IOException if an I/O error occurs while writing
     * @throws IllegalStateException if the writer has been closed
     * @throws IllegalArgumentException if any field value is null
     */
    public void writeRow(List<String> values) throws IOException {
        checkNotClosed();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                writer.write(config.getDelimiter());
            }
            writeField(values.get(i));
        }
        writer.write("\r\n");
    }

    /**
     * Writes a header row to CSV from variable arguments.
     * 
     * <p>This is a convenience method equivalent to {@link #writeRow(String...)}. Header rows
     * follow the same formatting and escaping rules as regular data rows.
     * 
     * @param headers the column header names to write
     * @throws IOException if an I/O error occurs while writing
     * @throws IllegalStateException if the writer has been closed
     * @throws IllegalArgumentException if any header value is null
     */
    public void writeHeader(String... headers) throws IOException {
        checkNotClosed();
        writeRow(headers);
    }

    /**
     * Writes a header row to CSV from a list.
     * 
     * <p>This is a convenience method equivalent to {@link #writeRow(List)}. Header rows
     * follow the same formatting and escaping rules as regular data rows.
     * 
     * @param headers the list of column header names to write
     * @throws IOException if an I/O error occurs while writing
     * @throws IllegalStateException if the writer has been closed
     * @throws IllegalArgumentException if any header value is null
     */
    public void writeHeader(List<String> headers) throws IOException {
        checkNotClosed();
        writeRow(headers);
    }

    /**
     * Flushes any buffered data to the underlying writer.
     * 
     * <p>This method calls flush on the underlying writer without closing it. Use this to
     * ensure all written data is flushed to the output stream without terminating the writer.
     * 
     * @throws IOException if an I/O error occurs while flushing
     * @throws IllegalStateException if the writer has been closed
     */
    public void flush() throws IOException {
        checkNotClosed();
        writer.flush();
    }

    /**
     * Closes the writer and releases underlying resources.
     * 
     * <p>Once closed, the writer cannot be reused. Subsequent write operations will throw
     * an IOException. This method is idempotent and safe to call multiple times.
     * 
     * @throws IOException if an I/O error occurs while closing the underlying writer
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            writer.close();
            closed = true;
        }
    }

    /**
     * Writes a single field with proper quoting and escaping.
     * 
     * <p>This method applies RFC 4180 quoting and escaping rules:
     * <ul>
     *   <li>If the field contains delimiters, quotes, or newlines, it is enclosed in quotes</li>
     *   <li>Quote characters within quoted fields are escaped using the configured escape character</li>
     *   <li>Unquoted fields are written as-is</li>
     * </ul>
     * 
     * @param value the field value to write
     * @throws IOException if an I/O error occurs while writing
     * @throws IllegalArgumentException if the value is null
     */
    private void writeField(String value) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException(ExceptionMessages.FIELD_VALUE_CANNOT_BE_NULL);
        }

        boolean needsQuotes = needsQuoting(value);

        if (needsQuotes) {
            writer.write(config.getQuoteChar());
        }

        for (char c : value.toCharArray()) {
            if (c == config.getQuoteChar()) {
                writer.write(config.getEscapeChar());
                writer.write(config.getQuoteChar());
            } else {
                writer.write(c);
            }
        }

        if (needsQuotes) {
            writer.write(config.getQuoteChar());
        }
    }

    /**
     * Determines whether a field value requires quoting.
     * 
     * <p>A field requires quoting if it contains any of the following characters:
     * <ul>
     *   <li>The configured delimiter character</li>
     *   <li>The configured quote character</li>
     *   <li>Carriage return (\r)</li>
     *   <li>Line feed (\n)</li>
     * </ul>
     * 
     * @param value the field value to check
     * @return true if the field requires quoting, false otherwise
     */
    private boolean needsQuoting(String value) {
        for (char c : value.toCharArray()) {
            if (c == config.getDelimiter() || 
                c == config.getQuoteChar() || 
                c == '\r' || 
                c == '\n') {
                return true;
            }
        }
        return false;
    }

    private void checkNotClosed() throws IOException {
        if (closed) {
            throw new IOException(ExceptionMessages.WRITER_IS_CLOSED);
        }
    }
}
