### AiPreparedPrompt.java
- H: 1.0
- C: EBEFC327
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:56:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Represents the outcome of prompt preparation, including substituted text and trimming metrics.

#### Purpose
- Encapsulates results from prompt processing
- Tracks source text trimming and character usage

#### Type
Final class implementing value semantics via Lombok annotations; marked for future record migration

#### Input
Constructor accepts: prompt String, sourceText String, trimmed boolean, originalSourceLength int, trimmedSourceLength int, availableSourceChars int; validates non-null prompt and sourceText

#### Output
Public accessors return: prompt String, sourceText String, trimmed boolean, originalSourceLength int, trimmedSourceLength int, availableSourceChars int

#### Core logic
- Stores immutable prompt preparation results
- Tracks whether source text was truncated
- Preserves character count metrics for source trimming analysis

#### Public API
prompt() -> String returns prepared prompt text  
sourceText() -> String returns substituted source text  
trimmed() -> boolean indicates if source was trimmed  
originalSourceLength() -> int returns original source length  
trimmedSourceLength() -> int returns trimmed source length  
availableSourceChars() -> int returns character budget  

#### Dependencies
lombok.EqualsAndHashCode, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord

#### Exceptions / Errors
Throws NullPointerException for null prompt or sourceText parameters

#### Concurrency
Immutable design ensures thread-safe usage without synchronization
