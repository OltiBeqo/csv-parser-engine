# CSV Parser Examples

This module contains practical usage examples demonstrating all key features of the CSV Parser Engine.

## Examples

### 1. BasicParsingExample
Simple CSV parsing from a string with index-based column access.

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.BasicParsingExample"
```

**Features:**
- Parse CSV from StringReader
- Access columns by index (0-based)
- Iterate through rows

### 2. HeaderAccessExample
Parse CSV with headers and access columns by name (case-insensitive).

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.HeaderAccessExample"
```

**Features:**
- Enable header parsing with `hasHeader(true)`
- Access columns by header name
- Case-insensitive column lookups

### 3. CustomDelimiterExample
Parse CSV files with non-standard delimiters (semicolon, pipe, etc.).

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.CustomDelimiterExample"
```

**Features:**
- Configure custom delimiters with `delimiter(char)`
- Support for quoted fields
- Multiple delimiter examples (semicolon, pipe)

### 4. StreamParsingExample
Process CSV data using Java Streams for functional-style operations.

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.StreamParsingExample"
```

**Features:**
- Stream-based parsing with `stream()`
- Filter rows with predicates
- Map and transform data
- Count, collect, and aggregate operations

### 5. ObjectMappingExample
Map CSV rows to custom Java objects using CsvRowMapper.

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.ObjectMappingExample"
```

**Features:**
- Define mapper functions with `CsvRowMapper<T>`
- Type-safe row conversion
- Map iterators with `mapIterator()`

### 6. WritingExample
Create and write CSV files programmatically.

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.WritingExample"
```

**Features:**
- Write rows with `writeRow(String...)`
- Write headers with `writeHeader(List<String>)`
- Custom delimiters for CSV output
- Automatic quoting of special characters

### 7. ErrorHandlingExample
Handle parsing errors, validate data, and implement recovery strategies.

```bash
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.ErrorHandlingExample"
```

**Features:**
- Basic error handling with NumberFormatException
- Validate row structure and field counts
- Graceful degradation with default values
- Data quality checks

## Running All Examples

```bash
cd csv-examples
mvn clean install

# Run specific example
mvn exec:java -Dexec.mainClass="com.csv.parser.examples.BasicParsingExample"

# Run all examples
for example in BasicParsing HeaderAccess CustomDelimiter StreamParsing ObjectMapping Writing ErrorHandling; do
    echo "=== $example ==="
    mvn exec:java -Dexec.mainClass="com.csv.parser.examples.${example}Example"
done
```

## Configuration Options

The CSV Parser supports extensive configuration via `CsvConfig.Builder`:

```java
CsvConfig config = CsvConfig.builder()
    .delimiter(',')           // Field delimiter (default: ,)
    .quoteChar('"')           // Quote character (default: ")
    .escapeChar('"')          // Escape character for quotes (default: ")
    .hasHeader(true)          // Enable header row parsing (default: false)
    .ignoreCase(true)         // Case-insensitive column lookup (default: true)
    .bufferSize(8192)         // Buffer size in bytes (default: 8192)
    .strictQuoteValidation(true) // RFC 4180 compliance (default: true)
    .build();
```

## API Overview

### Parsing

```java
CsvParser parser = CsvParser.builder().config(config).build();

// Iterator-based (memory-efficient)
Iterator<CsvRow> rows = parser.iterator(file);
while (rows.hasNext()) {
    CsvRow row = rows.next();
    String value = row.get(0);
}

// Stream-based (functional operations)
try (Stream<CsvRow> rows = parser.stream(file)) {
    rows.filter(...).map(...).forEach(...);
}

// Object mapping
Iterator<Person> people = parser.mapIterator(file, row -> new Person(
    row.get("name"),
    Integer.parseInt(row.get("age"))
));
```

### Writing

```java
CsvWriter writer = new CsvWriter(outputFile, config);
writer.writeHeader("Name", "Age", "City");
writer.writeRow("John", "30", "New York");
writer.writeRow("Jane", "25", "San Francisco");
writer.close();
```

### Column Access

```java
CsvRow row = parser.iterator(file).next();

// By index
String name = row.get(0);

// By header name
String name = row.get("name");

// Check size
int columnCount = row.size();

// Contains column
boolean hasName = row.containsHeader("name");
```

## Performance Considerations

1. **Iterator vs Stream**: Use iterators for memory-efficient sequential processing
2. **Buffer Size**: Configure buffer size based on your CSV file characteristics
3. **Headers**: Only enable if your CSV contains headers (small performance cost)
4. **Mapping**: Use mappers to convert strings to typed objects efficiently

## RFC 4180 Compliance

The parser fully complies with [RFC 4180](https://tools.ietf.org/html/rfc4180):
- Proper quoting of fields with special characters
- Escape sequences for quotes within fields
- CRLF line terminators
- Support for quoted line breaks within fields

## Troubleshooting

### "Column not found" Exception
- Ensure `hasHeader(true)` is set if using header-based access
- Check column name case (default is case-insensitive)
- Verify column index is within bounds

### Parsing Issues
- Verify delimiter matches your CSV file
- Check quote character configuration
- Ensure input file encoding is UTF-8

### Performance Issues
- Increase buffer size for large files
- Use iterators instead of loading entire file into memory
- Consider parallel stream processing for very large files

## License

See the main CSV Parser Engine repository for license information.
