package com.csv.parser.exception;

/**
 * Centralized repository for all exception messages used throughout the CSV parser.
 * 
 * <p>This class defines all exception messages as constants to ensure consistency,
 * maintainability, and ease of localization. Exception messages are organized by category
 * for better organization.
 */
public final class ExceptionMessages {

    private ExceptionMessages() {
        throw new AssertionError("This class should not be instantiated");
    }

    // ============ Configuration Validation Messages ============

    /**
     * Error message when delimiter and quote character are the same.
     */
    public static final String DELIMITER_EQUALS_QUOTE_CHAR =
            "Delimiter and quote character cannot be the same.";

    /**
     * Error message when escape character equals delimiter.
     */
    public static final String ESCAPE_CHAR_EQUALS_DELIMITER =
            "Escape character cannot be the same as delimiter.";

    /**
     * Error message when buffer size is invalid.
     */
    public static final String INVALID_BUFFER_SIZE =
            "Buffer size must be greater than 0.";

    // ============ Input Validation Messages ============

    /**
     * Error message when file input is null.
     */
    public static final String FILE_CANNOT_BE_NULL = "File cannot be null";

    /**
     * Error message when reader input is null.
     */
    public static final String READER_CANNOT_BE_NULL = "Reader cannot be null";

    /**
     * Error message when mapper function is null.
     */
    public static final String MAPPER_CANNOT_BE_NULL = "Mapper cannot be null";

    /**
     * Error message when row iterator is null.
     */
    public static final String ROW_ITERATOR_CANNOT_BE_NULL = "Row iterator cannot be null";

    /**
     * Error message when tokenizer is null.
     */
    public static final String TOKENIZER_CANNOT_BE_NULL = "Tokenizer cannot be null";

    /**
     * Error message when CSV config is null.
     */
    public static final String CONFIG_CANNOT_BE_NULL = "Config cannot be null";

    /**
     * Error message when headers are null.
     */
    public static final String HEADERS_CANNOT_BE_NULL = "Headers cannot be null";

    /**
     * Error message when field value is null.
     */
    public static final String FIELD_VALUE_CANNOT_BE_NULL = "Field value cannot be null";

    // ============ CSV Parsing Messages ============

    /**
     * Error message for general CSV parsing errors.
     */
    public static final String ERROR_PARSING_CSV = "Error parsing CSV";

    /**
     * Error message template for unclosed quoted field.
     * Parameters: lineNumber, fieldNumber
     */
    public static final String UNCLOSED_QUOTED_FIELD =
            "Unclosed quoted field at line %d, field %d";

    /**
     * Error message template for invalid character after closing quote (strict mode).
     * Parameters: character, lineNumber, fieldNumber
     */
    public static final String INVALID_CHAR_AFTER_QUOTE_STRICT =
            "Invalid character '%c' after closing quote at line %d, field %d";

    /**
     * Error message template for invalid character after closing quote (lenient mode).
     * Parameters: character, lineNumber, fieldNumber
     */
    public static final String INVALID_CHAR_AFTER_QUOTE_LENIENT =
            "Invalid character '%c' after closing quote at line %d, field %d";

    // ============ CSV Row Messages ============

    /**
     * Error message when accessing row data without headers.
     */
    public static final String CSV_DOES_NOT_CONTAIN_HEADERS =
            "CSV does not contain headers.";

    /**
     * Error message template for non-existent column.
     * Parameter: columnName
     */
    public static final String COLUMN_NOT_FOUND = "Column not found: ";

    // ============ Writer Messages ============

    /**
     * Error message when writer is closed.
     */
    public static final String WRITER_IS_CLOSED = "Writer is closed";

    /**
     * Error message for reader close failures.
     */
    public static final String ERROR_CLOSING_READER = "Error closing reader";

}
