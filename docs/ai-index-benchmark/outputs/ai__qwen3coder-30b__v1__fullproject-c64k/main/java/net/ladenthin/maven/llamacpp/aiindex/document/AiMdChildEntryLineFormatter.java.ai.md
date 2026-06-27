### AiMdChildEntryLineFormatter.java
- H: 1.0
- C: 12412458
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:01:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates deterministic checksum lines for package-level manifest entries from child names and headers.

#### Purpose
- Formats structured manifest lines for AI index package checksums.
- Supports aggregation of child entries in a deterministic order.

#### Type
class AiMdChildEntryLineFormatter final

#### Input
- Constructor: no parameters.
- Method format: name (String), childHeader (AiMdHeader).

#### Output
- Return value: String with format `<name>|<c>|<d>|<x>\n`.

#### Core logic
- Concatenates child name and header fields (c, d, x) using a pipe separator.
- Appends newline character to form a complete manifest line.
- Ensures deterministic order via fixed field sequence.

#### Public API
format(name, childHeader) -> String builds checksum line

#### Dependencies
AiMdHeader
net.ladenthin.maven.llamacpp.aiindex.document.AiMdChildEntryLineFormatter

#### Exceptions / Errors
No explicit exceptions. Null handling depends on input parameters.

#### Concurrency
No concurrency concerns; stateless and immutable.
