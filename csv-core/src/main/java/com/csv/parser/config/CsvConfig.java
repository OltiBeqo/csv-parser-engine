package com.csv.parser.config;

import com.csv.parser.exception.CsvConfigurationException;

/**
 * Immutable configuration class for CSV parsing and writing operations.
 * 
 * <p>CsvConfig encapsulates all configurable parameters for CSV processing, including delimiter,
 * quote character, escape character, header presence, and buffer size. This class validates
 * configuration parameters to ensure consistency and prevent invalid character combinations.
 * 
 * <p>Instances are created using the fluent builder pattern via {@link #builder()}, which provides
 * default values for all parameters:
 * <ul>
 *   <li>Delimiter: comma (,)</li>
 *   <li>Quote character: double quote (")</li>
 *   <li>Escape character: double quote (")</li>
 *   <li>Has header: false</li>
 *   <li>Buffer size: 8192 bytes</li>
 *   <li>Ignore case for column names: true</li>
 *   <li>Strict quote validation: true (RFC 4180 compliant)</li>
 * </ul>
 * 
 * <p>This class is thread-safe and immutable after construction.
 * 
 * @see CsvConfig.Builder
 */
public final class CsvConfig {

    private final char delimiter;
    private final char quoteChar;
    private final char escapeChar;
    private final boolean hasHeader;
    private final int bufferSize;
    private final boolean ignoreCase;
    private final boolean strictQuoteValidation;

    private CsvConfig(Builder builder) {
        this.delimiter = builder.delimiter;
        this.quoteChar = builder.quoteChar;
        this.escapeChar = builder.escapeChar;
        this.hasHeader = builder.hasHeader;
        this.bufferSize = builder.bufferSize;
        this.ignoreCase = builder.ignoreCase;
        this.strictQuoteValidation = builder.strictQuoteValidation;

        validate();
    }

    /**
     * Validates the configuration parameters to ensure consistency and correctness.
     * 
     * <p>This method enforces the following constraints:
     * <ul>
     *   <li>Delimiter and quote character must be different</li>
     *   <li>Escape character must be different from delimiter</li>
     *   <li>Buffer size must be greater than 0</li>
     * </ul>
     * 
     * @throws CsvConfigurationException if any validation constraint is violated
     */
    private void validate() {

        if (delimiter == quoteChar) {
            throw new CsvConfigurationException(
                    "Delimiter and quote character cannot be the same."
            );
        }

        if (escapeChar == delimiter) {
            throw new CsvConfigurationException(
                    "Escape character cannot be the same as delimiter."
            );
        }

        if (bufferSize <= 0) {
            throw new CsvConfigurationException(
                    "Buffer size must be greater than 0."
            );
        }
    }

    /**
     * Returns the field delimiter character used to separate values in a CSV row.
     * 
     * @return the delimiter character
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Returns the character used to quote field values containing special characters.
     * 
     * @return the quote character
     */
    public char getQuoteChar() {
        return quoteChar;
    }

    /**
     * Returns the character used to escape quote characters within quoted fields.
     * 
     * @return the escape character
     */
    public char getEscapeChar() {
        return escapeChar;
    }

    /**
     * Indicates whether the CSV input contains a header row.
     * 
     * @return true if the first row contains column headers, false otherwise
     */
    public boolean hasHeader() {
        return hasHeader;
    }

    /**
     * Returns the buffer size in bytes for I/O operations.
     * 
     * @return the buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Indicates whether column name lookups should be case-insensitive.
     * 
     * @return true if column name comparisons should ignore case, false otherwise
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * Indicates whether strict RFC 4180 compliance is enforced for quote validation.
     * 
     * <p>When true (default), any character other than delimiter or newline after a closing quote
     * will cause a parsing error. When false, whitespace characters are skipped after closing quotes,
     * allowing for more lenient parsing of real-world CSV files.
     * 
     * @return true if strict quote validation is enabled, false for lenient parsing
     */
    public boolean isStrictQuoteValidation() {
        return strictQuoteValidation;
    }

    /**
     * Creates a new builder for constructing a CsvConfig instance.
     * 
     * @return a new Builder with default configuration values
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for constructing CsvConfig instances with custom parameters.
     * 
     * <p>The Builder class provides a convenient way to configure CSV parsing and writing
     * operations with reasonable defaults. All configuration parameters can be overridden
     * using the builder methods, which return the builder instance for method chaining.
     * 
     * <p>Example usage:
     * <pre>{@code
     * CsvConfig config = CsvConfig.builder()
     *     .delimiter(';')
     *     .hasHeader(true)
     *     .bufferSize(16384)
     *     .build();
     * }</pre>
     * 
     * @see CsvConfig
     */
    public static class Builder {

        private char delimiter = ',';
        private char quoteChar = '"';
        private char escapeChar = '"';
        private boolean hasHeader = false;
        private int bufferSize = 8192;
        private boolean ignoreCase = true;
        private boolean strictQuoteValidation = true;

        /**
         * Sets the field delimiter character.
         * 
         * @param delimiter the delimiter character to use (must be different from quote character)
         * @return this builder instance for method chaining
         */
        public Builder delimiter(char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        /**
         * Sets the quote character used to quote field values.
         * 
         * @param quoteChar the quote character to use (must be different from delimiter)
         * @return this builder instance for method chaining
         */
        public Builder quoteChar(char quoteChar) {
            this.quoteChar = quoteChar;
            return this;
        }

        /**
         * Sets the escape character used within quoted fields.
         * 
         * @param escapeChar the escape character to use (must be different from delimiter)
         * @return this builder instance for method chaining
         */
        public Builder escapeChar(char escapeChar) {
            this.escapeChar = escapeChar;
            return this;
        }

        /**
         * Specifies whether the CSV input contains a header row.
         * 
         * @param hasHeader true if the first row contains column headers, false otherwise
         * @return this builder instance for method chaining
         */
        public Builder hasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
            return this;
        }

        /**
         * Sets the buffer size for I/O operations.
         * 
         * @param bufferSize the buffer size in bytes (must be greater than 0)
         * @return this builder instance for method chaining
         */
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * Specifies whether column name lookups should be case-insensitive.
         * 
         * @param ignoreCase true to ignore case when looking up column names, false for case-sensitive lookup
         * @return this builder instance for method chaining
         */
        public Builder ignoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            return this;
        }

        /**
         * Specifies whether strict RFC 4180 quote validation should be enforced.
         * 
         * <p>When set to true (default), any character other than delimiter or newline after
         * a closing quote will cause a parsing error. When set to false, whitespace characters
         * are skipped after closing quotes, allowing for more lenient parsing of real-world CSV files.
         * 
         * @param strictQuoteValidation true to enforce strict RFC 4180 compliance, false for lenient parsing
         * @return this builder instance for method chaining
         */
        public Builder strictQuoteValidation(boolean strictQuoteValidation) {
            this.strictQuoteValidation = strictQuoteValidation;
            return this;
        }

        /**
         * Builds and returns a new CsvConfig instance with the configured parameters.
         * 
         * @return a new CsvConfig instance with the configured parameters
         * @throws CsvConfigurationException if the configuration parameters are invalid
         */
        public CsvConfig build() {
            return new CsvConfig(this);
        }
    }
}
