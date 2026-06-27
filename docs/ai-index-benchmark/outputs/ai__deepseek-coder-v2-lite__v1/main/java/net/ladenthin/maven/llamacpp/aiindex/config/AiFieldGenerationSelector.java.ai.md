### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:07:06Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the {@link AiFieldGenerationConfig} that applies to a given source file, based on the entries' {@link AiFieldGenerationConfig#getFileExtensions() file extensions}.

#### Purpose
- To determine the appropriate {@link AiFieldGenerationConfig} for a given source file based on its file extension.

#### Type
- Class (`public final class AiFieldGenerationSelector`)

#### Input
- `configs`: An iterable of {@link AiFieldGenerationConfig} objects, which may be null and will be skipped.
- `fileName`: The name of the source file (e.g., `Foo.java`).

#### Output
- Returns the first matching {@link AiFieldGenerationConfig} based on the file extension or the first fallback entry if no match is found. If no entry matches and no fallback is configured, returns `null`.

#### Core logic
1. Initialize a variable to hold the fallback configuration.
2. Iterate through each {@link AiFieldGenerationConfig} in the provided iterable.
3. Check if the current config is null; if so, skip to the next iteration.
4. Retrieve the file extensions for the current config.
5. If the extension list is null or empty, set this config as the fallback and continue.
6. Iterate through each extension in the current config's extension list.
7. Check if the file name ends with the current extension; if so, return this config.
8. After the loop, return the fallback config if no match was found.

#### Public API
- `selectForFileName(configs: Iterable<AiFieldGenerationConfig>, fileName: String) -> @Nullable AiFieldGenerationConfig`
  - Purpose: Returns the {@link AiFieldGenerationConfig} that applies to the given file name based on its extensions.

#### Dependencies
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws no exceptions; handles null entries in the configs iterable by skipping them.

#### Concurrency
- The class and its methods are not thread-safe or synchronized, implying single-threaded usage.
