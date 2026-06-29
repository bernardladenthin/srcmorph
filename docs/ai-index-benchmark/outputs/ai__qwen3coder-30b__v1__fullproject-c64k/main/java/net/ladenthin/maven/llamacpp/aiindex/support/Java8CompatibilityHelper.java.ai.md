### Java8CompatibilityHelper.java
- H: 1.0
- C: B9EE5FC2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:08:30Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides Java 8 compatibility for Java 9+ APIs through wrapper methods for string, file, and collection operations.

#### Purpose
- Wraps Java 9+ APIs to maintain compatibility with Java 1.8.
- Centralizes compatibility logic for testing and reuse.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString annotation.

#### Input
- Constructor: no parameters.
- isBlank: String value.
- formatted: Format string and varargs of objects.
- readString: Path to file.
- writeString: Path, content string, charset.
- toList: Stream of elements.
- listOf: Varargs of elements.
- hashMapCapacityFor: Integer number of mappings.

#### Output
- isBlank: Boolean indicating blank or empty string.
- formatted: Formatted string result.
- readString: File content as String.
- writeString: No return; writes content to file.
- toList: List of stream elements.
- listOf: Immutable list of input elements.
- hashMapCapacityFor: Integer initial capacity for HashMap.

#### Core logic
- Checks if a string is empty or contains only whitespace.
- Formats strings using String.format as substitute for String.formatted.
- Reads file content using Files.readAllBytes with UTF-8 decoding.
- Writes string to file with specified charset, defaulting to UTF-8.
- Collects stream elements into a list using Collectors.toList.
- Creates immutable list from varargs using Arrays.asList.
- Calculates HashMap initial capacity based on load factor and expected entries.

#### Public API
- isBlank(str) -> boolean: Checks if string is blank or empty.
- formatted(format, args) -> String: Formats string like Java 15+.
- readString(path) -> String: Reads file content as string.
- writeString(path, content, charset) -> void: Writes string to file.
- toList(stream) -> List<T>: Collects stream into list.
- listOf(elements) -> List<T>: Creates immutable list from elements.
- hashMapCapacityFor(numMappings) -> int: Computes HashMap capacity.

#### Dependencies
java.io.IOException, java.nio.charset.Charset, java.nio.charset.StandardCharsets, java.nio.file.Files, java.nio.file.Path, java.util.Arrays, java.util.List, java.util.stream.Collectors, java.util.stream.Stream, lombok.ToString

#### Exceptions / Errors
- readString throws IOException on file read errors.
- writeString throws IOException on file write errors.
- isBlank throws NullPointerException if input string is null.

#### Concurrency
No concurrency handling; class is stateless and thread-safe for immutable inputs.
