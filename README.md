# CSV Parser Engine

A high-performance, RFC 4180-compliant Java CSV parsing library featuring a streaming, memory-efficient architecture based on a buffered tokenizer and a state-machine parser.

## Features

### Core Capabilities
- **RFC 4180 Compliant**: Fully compliant with RFC 4180 CSV standard
- **Streaming Architecture**: Process large CSV files with minimal memory overhead
- **Buffered Tokenizer**: Configurable buffer size for optimal performance  
- **State Machine Parser**: Robust handling of delimiters, quoted fields, and escaped quotes
- **Multiline Values**: Support for values spanning multiple lines
- **CRLF Variations**: Handles both CRLF and LF line endings
- **Custom Delimiters**: Configure any delimiter character

### Design
- **Low Allocation Overhead**: Reuses buffers and minimizes object creation
- **Modular Architecture**: Clear separation between tokenizer, parser, and builder
- **Iterator and Stream Support**: Both iterator and Java Stream API support
- **Immutable Row Model**: Thread-safe row objects with header and index access
- **Builder Pattern**: Fluent configuration API
- **Exception Hierarchy**: Dedicated exception types for different error scenarios

## Architecture

### Components

1. **CsvTokenizer / BufferedCsvTokenizer**
   - Handles character-level input streaming
   - Manages internal buffering with configurable buffer size
   - Provides peek and next character operations

2. **CsvStateMachine**
   - Implements RFC 4180-compliant state machine parsing
   - Handles quoted fields, escaped characters, and line breaks
   - Supports header processing

3. **CsvRow**
   - Immutable representation of a CSV row
   - Supports both indexed and header-based field access
   - Throws exceptions for missing columns/indices

4. **CsvConfig / CsvConfig.Builder**
   - Configuration for parsing behavior
   - Delimiter, quote character, escape character
   - Buffer size and header processing options

5. **CsvWriter**
   - Writes CSV data with proper RFC 4180 escaping
   - Automatic quote escaping and field quoting
   - CRLF line termination

## Usage

### Reading CSV Files

#### From String
```java
String csv = "name,age\nJohn,30\nJane,25\n";
CsvParser parser = CsvParser.builder().build();
Iterator<CsvRow> iterator = parser.iterator(new StringReader(csv));

while (iterator.hasNext()) {
    CsvRow row = iterator.next();
    System.out.println(row.get(0) + " is " + row.get(1) + " years old");
}
```

#### From File
```java
CsvParser parser = CsvParser.builder().build();
Iterator<CsvRow> iterator = parser.iterator(new File("data.csv"));

while (iterator.hasNext()) {
    CsvRow row = iterator.next();
    // Process row
}
```

#### With Headers
```java
CsvConfig config = CsvConfig.builder()
    .hasHeader(true)
    .build();

CsvParser parser = CsvParser.builder()
    .config(config)
    .build();

Iterator<CsvRow> iterator = parser.iterator(new StringReader(csv));

while (iterator.hasNext()) {
    CsvRow row = iterator.next();
    String name = row.get("name");  // Access by header name
    String age = row.get("age");
}
```

#### Using Streams
```java
CsvConfig config = CsvConfig.builder().hasHeader(true).build();
CsvParser parser = CsvParser.builder().config(config).build();

parser.stream(new File("data.csv"))
    .map(row -> row.get("name"))
    .forEach(System.out::println);
```

#### Custom Configuration
```java
CsvConfig config = CsvConfig.builder()
    .delimiter(';')
    .quoteChar('"')
    .hasHeader(true)
    .bufferSize(16384)
    .build();

CsvParser parser = CsvParser.builder().config(config).build();
```

### Writing CSV Files

#### Basic Writing
```java
StringWriter sw = new StringWriter();
CsvWriter writer = new CsvWriter(sw);

writer.writeHeader("name", "age", "city");
writer.writeRow("John", "30", "NYC");
writer.writeRow("Jane", "25", "LA");
writer.close();

String csv = sw.toString();
```

#### With Files
```java
try (FileWriter fw = new FileWriter("output.csv");
     CsvWriter writer = new CsvWriter(fw)) {
    
    writer.writeHeader("name", "age");
    writer.writeRow("John", "30");
    writer.writeRow("Jane", "25");
}
```

#### Custom Configuration
```java
CsvConfig config = CsvConfig.builder()
    .delimiter(';')
    .build();

CsvWriter writer = new CsvWriter(fileWriter, config);
```

## Configuration Options

### CsvConfig

- **delimiter** (default: `,`): Field separator character
- **quoteChar** (default: `"`): Quote character for escaping
- **escapeChar** (default: `"`): Character used to escape quotes
- **hasHeader** (default: `false`): Whether first row is a header
- **bufferSize** (default: `8192`): Buffer size for tokenizer

## Exception Handling

The library provides a dedicated exception hierarchy:

- `CsvException`: Base exception for all CSV-related errors
- `CsvParseException`: Thrown when parsing fails (extends `CsvException`)
- `CsvConfigurationException`: Thrown for invalid configuration (extends `CsvException`)

## Performance Characteristics

- **Memory**: Streaming design means memory usage is proportional to buffer size and field size, not file size
- **Allocation**: Reuses internal buffers to minimize garbage collection
- **Speed**: State machine parser with minimal object creation

## RFC 4180 Compliance

The parser implements all RFC 4180 requirements:
- Fields containing special characters must be enclosed in double quotes
- Within a quoted field, quote characters are escaped by doubling them
- Line breaks can occur within quoted fields
- CRLF or LF line endings supported
- Empty fields supported
- Header processing optional

## Module Structure

```
csv-parser-engine/
├── csv-core/              # Core library
│   ├── src/
│   │   ├── config/        # Configuration classes
│   │   ├── exception/     # Exception hierarchy
│   │   ├── model/         # Data models (CsvRow)
│   │   ├── parser/        # Parsing logic
│   │   ├── tokenizer/     # Tokenization
│   │   └── writer/        # CSV writing
│   └── tests/             # Unit tests
├── csv-examples/          # Usage examples
├── csv-benchmarks/        # Performance benchmarks
└── pom.xml               # Maven build configuration
```

## Building

### Maven
```bash
mvn clean install
```

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn package
```

## Contributing

Contributions are welcome! Please ensure:
- All tests pass
- Code follows project style guidelines
- New features include tests
- Documentation is updated
